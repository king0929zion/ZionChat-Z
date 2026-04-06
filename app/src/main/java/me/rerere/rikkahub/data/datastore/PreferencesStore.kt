package me.rerere.rikkahub.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.pebbletemplates.pebble.PebbleEngine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.AppScope
import me.rerere.rikkahub.data.ai.mcp.McpServerConfig
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_OCR_PROMPT
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_SUGGESTION_PROMPT
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_TITLE_PROMPT
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_TRANSLATION_PROMPT
import me.rerere.rikkahub.data.datastore.migration.PreferenceStoreV1Migration
import me.rerere.rikkahub.data.datastore.migration.PreferenceStoreV2Migration
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.Avatar
import me.rerere.rikkahub.data.model.Tag
import me.rerere.rikkahub.data.plugin.PluginSettings
import me.rerere.rikkahub.data.sync.s3.S3Config
import me.rerere.rikkahub.ui.theme.PresetThemes
import me.rerere.rikkahub.utils.JsonInstant
import me.rerere.rikkahub.utils.toMutableStateFlow
import me.rerere.search.SearchCommonOptions
import me.rerere.search.SearchServiceOptions
import me.rerere.tts.provider.TTSProviderSetting
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.uuid.Uuid

private const val TAG = "PreferencesStore"
private const val OPENAI_OFFICIAL_HOST = "api.openai.com"

private val Context.settingsStore by preferencesDataStore(
    name = "settings",
    produceMigrations = { context ->
        listOf(
            PreferenceStoreV1Migration(),
            PreferenceStoreV2Migration()
        )
    }
)

class SettingsStore(
    context: Context,
    scope: AppScope,
) : KoinComponent {
    companion object {
        // 版本号
        val VERSION = intPreferencesKey("data_version")

        // UI设置
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val THEME_ID = stringPreferencesKey("theme_id")
        val DISPLAY_SETTING = stringPreferencesKey("display_setting")
        val DEVELOPER_MODE = booleanPreferencesKey("developer_mode")

        // 模型选择
        val ENABLE_WEB_SEARCH = booleanPreferencesKey("enable_web_search")
        val FAVORITE_MODELS = stringPreferencesKey("favorite_models")
        val SELECT_MODEL = stringPreferencesKey("chat_model")
        val TITLE_MODEL = stringPreferencesKey("title_model")
        val TRANSLATE_MODEL = stringPreferencesKey("translate_model")
        val SUGGESTION_MODEL = stringPreferencesKey("suggestion_model")
        val IMAGE_GENERATION_MODEL = stringPreferencesKey("image_generation_model")
        val TITLE_PROMPT = stringPreferencesKey("title_prompt")
        val TRANSLATION_PROMPT = stringPreferencesKey("translation_prompt")
        val SUGGESTION_PROMPT = stringPreferencesKey("suggestion_prompt")
        val OCR_MODEL = stringPreferencesKey("ocr_model")
        val OCR_PROMPT = stringPreferencesKey("ocr_prompt")

        // 提供商
        val PROVIDERS = stringPreferencesKey("providers")

        // 助手
        val SELECT_ASSISTANT = stringPreferencesKey("select_assistant")
        val ASSISTANTS = stringPreferencesKey("assistants")
        val ASSISTANT_TAGS = stringPreferencesKey("assistant_tags")

        // 搜索
        val SEARCH_SERVICES = stringPreferencesKey("search_services")
        val SEARCH_COMMON = stringPreferencesKey("search_common")
        val SEARCH_SELECTED = intPreferencesKey("search_selected")

        // MCP
        val MCP_SERVERS = stringPreferencesKey("mcp_servers")

        // WebDAV
        val WEBDAV_CONFIG = stringPreferencesKey("webdav_config")

        // S3
        val S3_CONFIG = stringPreferencesKey("s3_config")

        // TTS
        val TTS_PROVIDERS = stringPreferencesKey("tts_providers")
        val SELECTED_TTS_PROVIDER = stringPreferencesKey("selected_tts_provider")

        // 备份提醒
        val BACKUP_REMINDER_CONFIG = stringPreferencesKey("backup_reminder_config")

        // 统计
        val LAUNCH_COUNT = intPreferencesKey("launch_count")

        // 赞助提醒
        val SPONSOR_ALERT_DISMISSED_AT = intPreferencesKey("sponsor_alert_dismissed_at")

        // 插件
        val PLUGIN_SETTINGS = stringPreferencesKey("plugin_settings")
    }

    private val dataStore = context.settingsStore

    val settingsFlowRaw = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            Settings(
                enableWebSearch = preferences[ENABLE_WEB_SEARCH] == true,
                favoriteModels = preferences[FAVORITE_MODELS]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: emptyList(),
                chatModelId = preferences[SELECT_MODEL]?.let { Uuid.parse(it) }
                    ?: DEFAULT_AUTO_MODEL_ID,
                titleModelId = preferences[TITLE_MODEL]?.let { Uuid.parse(it) }
                    ?: DEFAULT_AUTO_MODEL_ID,
                translateModeId = preferences[TRANSLATE_MODEL]?.let { Uuid.parse(it) }
                    ?: DEFAULT_AUTO_MODEL_ID,
                suggestionModelId = preferences[SUGGESTION_MODEL]?.let { Uuid.parse(it) }
                    ?: DEFAULT_AUTO_MODEL_ID,
                imageGenerationModelId = preferences[IMAGE_GENERATION_MODEL]?.let { Uuid.parse(it) } ?: Uuid.random(),
                titlePrompt = preferences[TITLE_PROMPT] ?: DEFAULT_TITLE_PROMPT,
                translatePrompt = preferences[TRANSLATION_PROMPT] ?: DEFAULT_TRANSLATION_PROMPT,
                suggestionPrompt = preferences[SUGGESTION_PROMPT] ?: DEFAULT_SUGGESTION_PROMPT,
                ocrModelId = preferences[OCR_MODEL]?.let { Uuid.parse(it) } ?: Uuid.random(),
                ocrPrompt = preferences[OCR_PROMPT] ?: DEFAULT_OCR_PROMPT,
                assistantId = preferences[SELECT_ASSISTANT]?.let { Uuid.parse(it) }
                    ?: DEFAULT_ASSISTANT_ID,
                assistantTags = preferences[ASSISTANT_TAGS]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: emptyList(),
                providers = JsonInstant.decodeFromString(preferences[PROVIDERS] ?: "[]"),
                assistants = JsonInstant.decodeFromString(preferences[ASSISTANTS] ?: "[]"),
                dynamicColor = preferences[DYNAMIC_COLOR] != false,
                themeId = preferences[THEME_ID] ?: PresetThemes[0].id,
                developerMode = preferences[DEVELOPER_MODE] == true,
                displaySetting = JsonInstant.decodeFromString(preferences[DISPLAY_SETTING] ?: "{}"),
                searchServices = preferences[SEARCH_SERVICES]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: listOf(SearchServiceOptions.DEFAULT),
                searchCommonOptions = preferences[SEARCH_COMMON]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: SearchCommonOptions(),
                searchServiceSelected = preferences[SEARCH_SELECTED] ?: 0,
                mcpServers = preferences[MCP_SERVERS]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: emptyList(),
                webDavConfig = preferences[WEBDAV_CONFIG]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: WebDavConfig(),
                s3Config = preferences[S3_CONFIG]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: S3Config(),
                ttsProviders = preferences[TTS_PROVIDERS]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: emptyList(),
                selectedTTSProviderId = preferences[SELECTED_TTS_PROVIDER]?.let { Uuid.parse(it) }
                    ?: DEFAULT_SYSTEM_TTS_ID,
                backupReminderConfig = preferences[BACKUP_REMINDER_CONFIG]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: BackupReminderConfig(),
                launchCount = preferences[LAUNCH_COUNT] ?: 0,
                sponsorAlertDismissedAt = preferences[SPONSOR_ALERT_DISMISSED_AT] ?: 0,
                pluginSettings = preferences[PLUGIN_SETTINGS]?.let {
                    JsonInstant.decodeFromString(it)
                } ?: PluginSettings(),
            )
        }
        .map { settings -> settings.mergeBuiltInsAndSanitize() }
        .onEach {
            get<PebbleEngine>().templateCache.invalidateAll()
        }

    val settingsFlow = settingsFlowRaw
        .distinctUntilChanged()
        .toMutableStateFlow(scope, Settings.dummy())

    suspend fun update(settings: Settings) {
        if(settings.init) {
            Log.w(TAG, "Cannot update dummy settings")
            return
        }
        val sanitizedSettings = settings.mergeBuiltInsAndSanitize()
        settingsFlow.value = sanitizedSettings
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR] = sanitizedSettings.dynamicColor
            preferences[THEME_ID] = sanitizedSettings.themeId
            preferences[DEVELOPER_MODE] = sanitizedSettings.developerMode
            preferences[DISPLAY_SETTING] = JsonInstant.encodeToString(sanitizedSettings.displaySetting)

            preferences[ENABLE_WEB_SEARCH] = sanitizedSettings.enableWebSearch
            preferences[FAVORITE_MODELS] = JsonInstant.encodeToString(sanitizedSettings.favoriteModels)
            preferences[SELECT_MODEL] = sanitizedSettings.chatModelId.toString()
            preferences[TITLE_MODEL] = sanitizedSettings.titleModelId.toString()
            preferences[TRANSLATE_MODEL] = sanitizedSettings.translateModeId.toString()
            preferences[SUGGESTION_MODEL] = sanitizedSettings.suggestionModelId.toString()
            preferences[IMAGE_GENERATION_MODEL] = sanitizedSettings.imageGenerationModelId.toString()
            preferences[TITLE_PROMPT] = sanitizedSettings.titlePrompt
            preferences[TRANSLATION_PROMPT] = sanitizedSettings.translatePrompt
            preferences[SUGGESTION_PROMPT] = sanitizedSettings.suggestionPrompt
            preferences[OCR_MODEL] = sanitizedSettings.ocrModelId.toString()
            preferences[OCR_PROMPT] = sanitizedSettings.ocrPrompt

            preferences[PROVIDERS] = JsonInstant.encodeToString(sanitizedSettings.providers)

            preferences[ASSISTANTS] = JsonInstant.encodeToString(sanitizedSettings.assistants)
            preferences[SELECT_ASSISTANT] = sanitizedSettings.assistantId.toString()
            preferences[ASSISTANT_TAGS] = JsonInstant.encodeToString(sanitizedSettings.assistantTags)

            preferences[SEARCH_SERVICES] = JsonInstant.encodeToString(sanitizedSettings.searchServices)
            preferences[SEARCH_COMMON] = JsonInstant.encodeToString(sanitizedSettings.searchCommonOptions)
            preferences[SEARCH_SELECTED] = sanitizedSettings.searchServiceSelected.coerceIn(0, sanitizedSettings.searchServices.size - 1)

            preferences[MCP_SERVERS] = JsonInstant.encodeToString(sanitizedSettings.mcpServers)
            preferences[WEBDAV_CONFIG] = JsonInstant.encodeToString(sanitizedSettings.webDavConfig)
            preferences[S3_CONFIG] = JsonInstant.encodeToString(sanitizedSettings.s3Config)
            preferences[TTS_PROVIDERS] = JsonInstant.encodeToString(sanitizedSettings.ttsProviders)
            sanitizedSettings.selectedTTSProviderId.let {
                preferences[SELECTED_TTS_PROVIDER] = it.toString()
            }
            preferences[BACKUP_REMINDER_CONFIG] = JsonInstant.encodeToString(sanitizedSettings.backupReminderConfig)
            preferences[LAUNCH_COUNT] = sanitizedSettings.launchCount
            preferences[SPONSOR_ALERT_DISMISSED_AT] = sanitizedSettings.sponsorAlertDismissedAt
            preferences[PLUGIN_SETTINGS] = JsonInstant.encodeToString(sanitizedSettings.pluginSettings)
        }
    }

    suspend fun update(fn: (Settings) -> Settings) {
        update(fn(settingsFlow.value))
    }

    suspend fun updateAssistant(assistantId: Uuid) {
        dataStore.edit { preferences ->
            preferences[SELECT_ASSISTANT] = assistantId.toString()
        }
    }

    suspend fun updateAssistantModel(assistantId: Uuid, modelId: Uuid) {
        update { settings ->
            settings.copy(
                assistants = settings.assistants.map { assistant ->
                    if (assistant.id == assistantId) {
                        assistant.copy(chatModelId = modelId)
                    } else {
                        assistant
                    }
                }
            )
        }
    }

    suspend fun updateAssistantThinkingBudget(assistantId: Uuid, thinkingBudget: Int?) {
        update { settings ->
            settings.copy(
                assistants = settings.assistants.map { assistant ->
                    if (assistant.id == assistantId) {
                        assistant.copy(thinkingBudget = thinkingBudget)
                    } else {
                        assistant
                    }
                }
            )
        }
    }

    suspend fun updateAssistantMcpServers(assistantId: Uuid, mcpServers: Set<Uuid>) {
        update { settings ->
            settings.copy(
                assistants = settings.assistants.map { assistant ->
                    if (assistant.id == assistantId) {
                        assistant.copy(mcpServers = mcpServers)
                    } else {
                        assistant
                    }
                }
            )
        }
    }


}

private fun Settings.mergeBuiltInsAndSanitize(): Settings {
    var providers = providers.ifEmpty { DEFAULT_PROVIDERS }
        .filterNot { provider -> provider.id in REMOVED_DEFAULT_PROVIDER_IDS }
        .toMutableList()
    DEFAULT_PROVIDERS.forEach { defaultProvider ->
        if (providers.none { it.id == defaultProvider.id }) {
            providers.add(defaultProvider.copyProvider())
        }
    }
    providers = providers.map { provider ->
        val defaultProvider = DEFAULT_PROVIDERS.find { it.id == provider.id }
        val mergedProvider = if (defaultProvider != null) {
            provider.copyProvider(
                builtIn = defaultProvider.builtIn,
                description = defaultProvider.description,
                shortDescription = defaultProvider.shortDescription,
            )
        } else {
            provider
        }
        mergedProvider.sanitizeProvider()
    }.distinctBy { it.id }.toMutableList()

    val assistants = assistants.ifEmpty { DEFAULT_ASSISTANTS }.toMutableList().apply {
        DEFAULT_ASSISTANTS.forEach { defaultAssistant ->
            if (none { it.id == defaultAssistant.id }) {
                add(defaultAssistant.copy())
            }
        }
    }

    val ttsProviders = ttsProviders.ifEmpty { DEFAULT_TTS_PROVIDERS }.toMutableList().apply {
        DEFAULT_TTS_PROVIDERS.forEach { defaultTTSProvider ->
            if (none { provider -> provider.id == defaultTTSProvider.id }) {
                add(defaultTTSProvider.copyProvider())
            }
        }
    }

    return copy(
        providers = providers,
        assistants = assistants,
        ttsProviders = ttsProviders
    ).sanitizeInvalidReferences()
}

private fun Settings.sanitizeInvalidReferences(): Settings {
    val validMcpServerIds = mcpServers.map { it.id }.toSet()
    val validModelIds = providers.flatMap { it.models }.map { it.id }.toSet()

    val sanitizedAssistants = assistants.distinctBy { it.id }.map { assistant ->
        assistant.copy(
            chatModelId = assistant.chatModelId?.takeIf { it in validModelIds },
            mcpServers = assistant.mcpServers.filter { serverId ->
                serverId in validMcpServerIds
            }.toSet()
        )
    }
    val validAssistantIds = sanitizedAssistants.map { it.id }.toSet()

    return copy(
        chatModelId = chatModelId.takeIf { it in validModelIds } ?: Uuid.random(),
        titleModelId = titleModelId.takeIf { it in validModelIds } ?: Uuid.random(),
        imageGenerationModelId = imageGenerationModelId.takeIf { it in validModelIds } ?: Uuid.random(),
        translateModeId = translateModeId.takeIf { it in validModelIds } ?: Uuid.random(),
        suggestionModelId = suggestionModelId.takeIf { it in validModelIds } ?: Uuid.random(),
        ocrModelId = ocrModelId.takeIf { it in validModelIds } ?: Uuid.random(),
        assistantId = assistantId.takeIf { it in validAssistantIds }
            ?: sanitizedAssistants.firstOrNull()?.id
            ?: DEFAULT_ASSISTANT_ID,
        assistants = sanitizedAssistants,
        ttsProviders = ttsProviders.distinctBy { it.id },
        favoriteModels = favoriteModels.filter { uuid -> uuid in validModelIds },
    )
}

private fun ProviderSetting.sanitizeProvider(): ProviderSetting {
    return when (this) {
        is ProviderSetting.OpenAI -> copy(
            models = models.distinctBy { model -> model.id },
            useResponseApi = if (baseUrl.toHttpUrlOrNull()?.host?.lowercase() == OPENAI_OFFICIAL_HOST) {
                useResponseApi
            } else {
                false
            }
        )

        is ProviderSetting.Google -> copy(
            models = models.distinctBy { model -> model.id }
        )

        is ProviderSetting.Claude -> copy(
            models = models.distinctBy { model -> model.id }
        )
    }
}

@Serializable
data class Settings(
    @Transient
    val init: Boolean = false,
    val dynamicColor: Boolean = true,
    val themeId: String = PresetThemes[0].id,
    val developerMode: Boolean = false,
    val displaySetting: DisplaySetting = DisplaySetting(),
    val enableWebSearch: Boolean = false,
    val favoriteModels: List<Uuid> = emptyList(),
    val chatModelId: Uuid = Uuid.random(),
    val titleModelId: Uuid = Uuid.random(),
    val imageGenerationModelId: Uuid = Uuid.random(),
    val titlePrompt: String = DEFAULT_TITLE_PROMPT,
    val translateModeId: Uuid = Uuid.random(),
    val translatePrompt: String = DEFAULT_TRANSLATION_PROMPT,
    val suggestionModelId: Uuid = Uuid.random(),
    val suggestionPrompt: String = DEFAULT_SUGGESTION_PROMPT,
    val ocrModelId: Uuid = Uuid.random(),
    val ocrPrompt: String = DEFAULT_OCR_PROMPT,
    val assistantId: Uuid = DEFAULT_ASSISTANT_ID,
    val providers: List<ProviderSetting> = DEFAULT_PROVIDERS,
    val assistants: List<Assistant> = DEFAULT_ASSISTANTS,
    val assistantTags: List<Tag> = emptyList(),
    val searchServices: List<SearchServiceOptions> = listOf(SearchServiceOptions.DEFAULT),
    val searchCommonOptions: SearchCommonOptions = SearchCommonOptions(),
    val searchServiceSelected: Int = 0,
    val mcpServers: List<McpServerConfig> = emptyList(),
    val webDavConfig: WebDavConfig = WebDavConfig(),
    val s3Config: S3Config = S3Config(),
    val ttsProviders: List<TTSProviderSetting> = DEFAULT_TTS_PROVIDERS,
    val selectedTTSProviderId: Uuid = DEFAULT_SYSTEM_TTS_ID,
    val backupReminderConfig: BackupReminderConfig = BackupReminderConfig(),
    val launchCount: Int = 0,
    val sponsorAlertDismissedAt: Int = 0,
    val pluginSettings: PluginSettings = PluginSettings(),
) {
    companion object {
        // 构造一个用于初始化的settings, 但它不能用于保存，防止使用初始值存储
        fun dummy() = Settings(init = true)
    }
}

@Serializable
data class DisplaySetting(
    val userAvatar: Avatar = Avatar.Dummy,
    val userNickname: String = "",
    val useAppIconStyleLoadingIndicator: Boolean = true,
    val showUserAvatar: Boolean = true,
    val showAssistantBubble: Boolean = false,
    val showModelIcon: Boolean = true,
    val showModelName: Boolean = false,
    val showDateBelowName: Boolean = false,
    val showTokenUsage: Boolean = true,
    val showThinkingContent: Boolean = true,
    val autoCloseThinking: Boolean = true,
    val showUpdates: Boolean = true,
    val showMessageJumper: Boolean = true,
    val messageJumperOnLeft: Boolean = false,
    val fontSizeRatio: Float = 1.0f,
    val enableMessageGenerationHapticEffect: Boolean = false,
    val skipCropImage: Boolean = false,
    val enableNotificationOnMessageGeneration: Boolean = false,
    val enableLiveUpdateNotification: Boolean = false,
    val codeBlockAutoWrap: Boolean = false,
    val codeBlockAutoCollapse: Boolean = false,
    val showLineNumbers: Boolean = false,
    val ttsOnlyReadQuoted: Boolean = false,
    val autoPlayTTSAfterGeneration: Boolean = false,
    val pasteLongTextAsFile: Boolean = false,
    val pasteLongTextThreshold: Int = 1000,
    val sendOnEnter: Boolean = false,
    val enableAutoScroll: Boolean = true,
    val enableLatexRendering: Boolean = true,
    val enableBlurEffect: Boolean = false,
)

@Serializable
data class WebDavConfig(
    val url: String = "",
    val username: String = "",
    val password: String = "",
    val path: String = "rikkahub_backups",
    val items: List<BackupItem> = listOf(
        BackupItem.DATABASE,
        BackupItem.FILES
    ),
) {
    @Serializable
    enum class BackupItem {
        DATABASE,
        FILES,
    }
}

@Serializable
data class BackupReminderConfig(
    val enabled: Boolean = false,
    val intervalDays: Int = 7,
    val lastBackupTime: Long = 0L,
)

fun Settings.isNotConfigured() = providers.all { it.models.isEmpty() }

fun Settings.findModelById(uuid: Uuid): Model? {
    return this.providers.findModelById(uuid)
}

fun List<ProviderSetting>.findModelById(uuid: Uuid): Model? {
    this.forEach { setting ->
        setting.models.forEach { model ->
            if (model.id == uuid) {
                return model
            }
        }
    }
    return null
}

fun Settings.getCurrentChatModel(): Model? {
    return findModelById(this.getCurrentAssistant().chatModelId ?: this.chatModelId)
}

fun Assistant.isPersonalization(): Boolean = this.id == DEFAULT_ASSISTANT_ID

private fun Assistant.normalizeMemoryBehavior(): Assistant {
    return copy(
        enableMemory = true,
        useGlobalMemory = false,
        enableTimeReminder = false,
    )
}

fun Settings.getPersonalizationAssistant(): Assistant {
    return (this.assistants.find { it.id == DEFAULT_ASSISTANT_ID } ?: this.assistants.first())
        .normalizeMemoryBehavior()
}

fun Settings.getBotAssistants(): List<Assistant> {
    return this.assistants
        .filterNot { it.id in DEFAULT_ASSISTANTS_IDS }
        .map { it.normalizeMemoryBehavior() }
}

fun Settings.getCurrentAssistant(): Assistant {
    return (this.assistants.find { it.id == assistantId } ?: getPersonalizationAssistant())
        .normalizeMemoryBehavior()
}

fun Settings.getAssistantById(id: Uuid): Assistant? {
    return this.assistants.find { it.id == id }?.normalizeMemoryBehavior()
}

fun Settings.getSelectedTTSProvider(): TTSProviderSetting? {
    return selectedTTSProviderId?.let { id ->
        ttsProviders.find { it.id == id }
    } ?: ttsProviders.firstOrNull()
}

fun Model.findProvider(providers: List<ProviderSetting>, checkOverwrite: Boolean = true): ProviderSetting? {
    val provider = findModelProviderFromList(providers) ?: return null
    val providerOverwrite = this.providerOverwrite
    if (checkOverwrite && providerOverwrite != null) {
        return providerOverwrite.copyProvider(models = emptyList())
    }
    return provider
}

private fun Model.findModelProviderFromList(providers: List<ProviderSetting>): ProviderSetting? {
    providers.forEach { setting ->
        setting.models.forEach { model ->
            if (model.id == this.id) {
                return setting
            }
        }
    }
    return null
}

internal val DEFAULT_ASSISTANT_ID = Uuid.parse("0950e2dc-9bd5-4801-afa3-aa887aa36b4e")
internal val DEFAULT_ASSISTANTS = listOf(
    Assistant(
        id = DEFAULT_ASSISTANT_ID,
        name = "",
        systemPrompt = ""
    ),
    Assistant(
        id = Uuid.parse("3d47790c-c415-4b90-9388-751128adb0a0"),
        name = "",
        systemPrompt = """
            You are a helpful assistant, called {{char}}, based on model {{model_name}}.

            ## Info
            - Time: {{cur_datetime}}
            - Locale: {{locale}}
            - Timezone: {{timezone}}
            - Device Info: {{device_info}}
            - System Version: {{system_version}}
            - User Nickname: {{user}}

            ## Hint
            - If the user does not specify a language, reply in the user's primary language.
            - Remember to use Markdown syntax for formatting, and use latex for mathematical expressions.
        """.trimIndent()
    ),
)

val DEFAULT_SYSTEM_TTS_ID = Uuid.parse("026a01a2-c3a0-4fd5-8075-80e03bdef200")
private val DEFAULT_TTS_PROVIDERS = listOf(
    TTSProviderSetting.SystemTTS(
        id = DEFAULT_SYSTEM_TTS_ID,
        name = "",
    ),
    TTSProviderSetting.OpenAI(
        id = Uuid.parse("e36b22ef-ca82-40ab-9e70-60cad861911c"),
        name = "AiHubMix",
        baseUrl = "https://aihubmix.com/v1",
        model = "gpt-4o-mini-tts",
        voice = "alloy",
    )
)

internal val DEFAULT_ASSISTANTS_IDS = DEFAULT_ASSISTANTS.map { it.id }
