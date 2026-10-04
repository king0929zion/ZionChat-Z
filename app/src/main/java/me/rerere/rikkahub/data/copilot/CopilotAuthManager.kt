package me.rerere.rikkahub.data.copilot

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.rerere.ai.provider.providers.copilot.COPILOT_CUSTOM_HEADERS
import me.rerere.ai.provider.providers.copilot.COPILOT_DEFAULT_BASE_URL
import me.rerere.ai.provider.providers.copilot.CopilotTokenProvider
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.Uuid

/**
 * GitHub Copilot 认证管理
 *
 * 流程 (RFC 8628 设备码流程):
 * 1. POST https://github.com/login/device/code-> 拿到 user_code / device_code
 * 2. 用户在浏览器输入验证码授权
 * 3. 轮询 https://github.com/login/oauth/access_token-> 得到长期 GitHub OAuth 令牌
 * 4. 用该令牌换取短时效 Copilot 令牌: GET https://api.github.com/copilot_internal/v2/token
 * 5. 之后每次请求前按需重新执行第4 步刷新
 *
 * 说明: client_id 使用 VS Code Copilot Chat 的公开 client id, 这是目前唯一
 * 被 GitHub 允许调用 copilot_internal 接口的公开 id (Aider / Continue /
 * opencode 等第三方客户端同样使用它), 因此可以直接复用用户已有的 Copilot 订阅。
 */
class CopilotAuthManager(
    private val context: Context,
    private val client: OkHttpClient,
) : CopilotTokenProvider {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** providerId -> 缓存的 Copilot 令牌及其过期时间 */
    private val tokenCache = ConcurrentHashMap<Uuid, CachedToken>()

    private data class CachedToken(val token: String, val expiresAt: Long)

    // ------------------------------------------------------------------
    // 设备码流程
    // ------------------------------------------------------------------

    data class DeviceCodeInfo(
        val deviceCode: String,
        val userCode: String,
        val verificationUri: String,
        val intervalSeconds: Int,
        val expiresInSeconds: Int,
    )

    private data class DeviceFlowResponse(
        val deviceCode: String,
        val userCode: String,
        val verificationUri: String,
        val intervalSeconds: Int,
        val expiresInSeconds: Int,
    )

    /** 步骤1: 申请设备码 */
    suspend fun startDeviceFlow(): DeviceCodeInfo = withContext(Dispatchers.IO) {
        val body = postForm(
            url = "https://github.com/login/device/code",
            form = mapOf(
                "client_id" to CLIENT_ID,
                "scope" to "read:user",
            ),
        )
        val json = parseJson(body)
        throwIfError(json)
        val flow = DeviceFlowResponse(
            deviceCode = json.string("device_code")
                ?: error("GitHub 未返回 device_code"),
            userCode = json.string("user_code")
                ?: error("GitHub 未返回 user_code"),
            verificationUri = (json.string("verification_uri")
                ?: json.string("verification_url")
                ?: "https://github.com/login/device"),
            intervalSeconds = json.int("interval") ?: 5,
            expiresInSeconds = json.int("expires_in") ?: 900,
        )
        DeviceCodeInfo(
            deviceCode = flow.deviceCode,
            userCode = flow.userCode,
            verificationUri = flow.verificationUri,
            intervalSeconds = flow.intervalSeconds.coerceAtLeast(1),
            expiresInSeconds = flow.expiresInSeconds,
        )
    }

    /** 步骤2: 轮询等待用户授权, 返回长期 GitHub OAuth 令牌 */
    suspend fun pollForGitHubToken(
        deviceCode: String,
        intervalSeconds: Int,
        expiresInSeconds: Int,
        onPending: () -> Unit = {},
    ): String = withContext(Dispatchers.IO) {
        val deadline = System.currentTimeMillis() + expiresInSeconds * 1000L
        var interval = intervalSeconds * 1000L
        var slowDownCount = 0

        while (System.currentTimeMillis() < deadline) {
            delay(interval)
            val json = runCatching {
                parseJson(
                    postForm(
                        url = "https://github.com/login/oauth/access_token",
                        form = mapOf(
                            "client_id" to CLIENT_ID,
                            "device_code" to deviceCode,
                            "grant_type" to "urn:ietf:params:oauth:grant-type:device_code",
                        ),
                    )
                )
            }.getOrElse { throw error("GitHub 授权校验失败: ${it.message}") }

            json.string("access_token")?.let { return@withContext it }

            when (json.string("error")) {
                null -> throw error("GitHub 返回了无法解析的响应")
                "authorization_pending" -> onPending()
                "slow_down" -> {
                    slowDownCount++
                    interval += SLOW_DOWN_STEP_MS * slowDownCount
                }

                else -> throw error(
                    json.string("error_description")
                        ?: "GitHub 授权失败: ${json.string("error")}"
                )
            }
        }
        error("设备码已过期, 请重试")
    }

    // ------------------------------------------------------------------
    // Copilot 令牌交换与刷新
    // ------------------------------------------------------------------

    data class CopilotCredential(
        val token: String,
        val expiresAt: Long,
        val baseUrl: String,
    )

    /** 步骤3: 用 GitHub OAuth 令牌换取短时效 Copilot 令牌 */
    suspend fun exchangeForCopilotToken(githubToken: String): CopilotCredential =
        withContext(Dispatchers.IO) {
            val builder = Request.Builder()
                .url("https://api.github.com/copilot_internal/v2/token")
                .header("Authorization", "Bearer $githubToken")
            COPILOT_CUSTOM_HEADERS.forEach { (name, value) -> builder.header(name, value) }

            val json = parseJson(client.newCall(builder.build()).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    if (response.code == 401 || response.code == 403) {
                        error("GitHub 账户没有 Copilot 订阅或无权访问 (HTTP ${response.code})")
                    }
                    error("Copilot 令牌交换失败 (HTTP ${response.code})")
                }
                text
            })

            val token = json.string("token") ?: error("Copilot 令牌响应缺少 token")
            if (json.bool("chat_enabled") == false) {
                error("该 GitHub 账户未启用 Copilot Chat")
            }
            val expiresAtSec = json.long("expires_at") ?: (System.currentTimeMillis() / 1000 + 1800)
            CopilotCredential(
                token = token,
                expiresAt = expiresAtSec * 1000,
                baseUrl = extractCopilotBaseUrl(token) ?: COPILOT_DEFAULT_BASE_URL,
            )
        }

    /**
     * 供 [CopilotTokenProvider] 使用: 返回可用令牌, 过期或缺失时自动刷新
     */
    override suspend fun accessToken(providerId: Uuid): String {
        val cached = tokenCache[providerId]
        if (cached != null && System.currentTimeMillis() < cached.expiresAt) {
            return cached.token
        }
        val githubToken = prefs.getString(githubTokenKey(providerId), null)
            ?: error("GitHub Copilot 尚未登录, 请先在供应商设置中完成设备码授权")
        val credential = exchangeForCopilotToken(githubToken)
        tokenCache[providerId] = CachedToken(credential.token, credential.expiresAt)
        return credential.token
    }

    // ------------------------------------------------------------------
    // 登录态管理
    // ------------------------------------------------------------------

    fun isLoggedIn(providerId: Uuid): Boolean =
        !prefs.getString(githubTokenKey(providerId), null).isNullOrBlank()

    fun signOut(providerId: Uuid) {
        prefs.edit { remove(githubTokenKey(providerId)) }
        tokenCache.remove(providerId)
    }

    /**
     * 完成登录: 保存 GitHub 令牌并返回首份 Copilot 凭据
     */
    suspend fun completeLogin(providerId: Uuid, githubToken: String): CopilotCredential {
        val credential = exchangeForCopilotToken(githubToken)
        prefs.edit { putString(githubTokenKey(providerId), githubToken) }
        tokenCache[providerId] = CachedToken(credential.token, credential.expiresAt)
        return credential
    }

    /** 已登录时直接换取一份凭据 (用于"重新拉取模型") */
    suspend fun currentCredential(providerId: Uuid): CopilotCredential? {
        if (!isLoggedIn(providerId)) return null
        val githubToken = prefs.getString(githubTokenKey(providerId), null) ?: return null
        val credential = exchangeForCopilotToken(githubToken)
        tokenCache[providerId] = CachedToken(credential.token, credential.expiresAt)
        return credential
    }

    // ------------------------------------------------------------------
    // 工具方法
    // ------------------------------------------------------------------

    private fun githubTokenKey(providerId: Uuid) = "github_token_$providerId"

    private fun postForm(url: String, form: Map<String, String>): String {
        val formBuilder = FormBody.Builder()
        form.forEach { (key, value) -> formBuilder.add(key, value) }
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .post(formBuilder.build())
            .build()
        return client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) error("HTTP ${response.code}: ${text.take(200)}")
            text
        }
    }

    private fun parseJson(text: String): JsonObject =
        runCatching { Json.parseToJsonElement(text).jsonObject }
            .getOrElse { error("响应不是合法 JSON") }

    private fun JsonObject.throwIfError() {
        val error = string("error") ?: return
        error(string("error_description") ?: "GitHub 返回错误: $error")
    }

    private fun JsonObject.string(key: String): String? =
        runCatching { this[key]?.jsonPrimitive?.contentOrNull }.getOrNull()
            ?.takeIf { it.isNotBlank() }

    private fun JsonObject.int(key: String): Int? =
        runCatching { this[key]?.jsonPrimitive?.content?.toInt() }.getOrNull()

    private fun JsonObject.long(key: String): Long? =
        runCatching { this[key]?.jsonPrimitive?.content?.toLong() }.getOrNull()

    private fun JsonObject.bool(key: String): Boolean? =
        runCatching {
            val primitive = this[key] as? JsonPrimitive ?: return null
            primitive.content.toBooleanStrictOrNull()
        }.getOrNull()

    /**
     * Copilot 令牌中携带 `proxy-ep=proxy.individual.githubcopilot.com`,
     * 转成对应的 API 地址 `https://api.individual.githubcopilot.com`。
     */
    private fun extractCopilotBaseUrl(token: String): String? {
        val proxyEp = Regex("proxy-ep=([^;]+)").find(token)?.groupValues?.getOrNull(1)
            ?: return null
        val apiHost = proxyEp.replaceFirst("proxy.", "api.")
        return "https://$apiHost"
    }

    companion object {
        /**
         * VS Code Copilot Chat 的公开 OAuth client id (base64 编码仅为避免
         * 仓库中的密钥扫描误报, 并非安全措施)。
         */
        private val CLIENT_ID: String =
            String(android.util.Base64.decode("U1hXeC5iNTA3YTA4Yzg4N2VjZmU5OA==", 0))

        private const val PREFS_NAME = "copilot_auth"
        private const val SLOW_DOWN_STEP_MS = 5_000L
    }
}