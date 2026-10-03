package me.rerere.rikkahub.data.plugin

import me.rerere.rikkahub.data.ai.mcp.McpCommonOptions
import me.rerere.rikkahub.data.ai.mcp.McpServerConfig
import me.rerere.rikkahub.data.datastore.Settings

/**
 * 商店插件静态注册表
 *
 * 三个 MCP 接入的插件均有现成的 MCP 实现:
 * - 高德地图: 官方 MCP (https://lbs.amap.com/api/mcp-server), 端点 https://mcp.amap.com/mcp?key=<KEY>
 * - GitHub: 官方远程 MCP (https://api.githubcopilot.com/mcp/), PAT 作为 Bearer 令牌
 * - 12306: 社区 12306 MCP (Joooook/12306-mcp 等), 需要自建或选择已托管的 HTTP 端点
 */
data class StorePluginSpec(
    val id: String,
    val name: String,
    val category: String,
    val defaultEndpoint: String,
    /** {KEY} 占位符会被 apiKey 替换 */
    val requiresApiKey: Boolean = false,
    /** API Key 是否以 Authorization: Bearer 头发送 (GitHub) */
    val apiKeyAsBearerHeader: Boolean = false,
    val description: String = "",
) {
    fun resolvedEndpoint(config: StorePluginConfig): String {
        if (!requiresApiKey) return config.endpoint.trim()
        return config.endpoint.trim().replace("{KEY}", config.apiKey.trim())
    }
}

object PluginStore {
    val AMAP = StorePluginSpec(
        id = "amap",
        name = "高德地图",
        category = "Maps",
        defaultEndpoint = "https://mcp.amap.com/mcp?key={KEY}",
        requiresApiKey = true,
        description = "高德官方 MCP: 地点搜索 / 路线规划 / 天气 / 地理编码。需要在高德开放平台 (lbs.amap.com) 申请 Key。",
    )

    val GITHUB = StorePluginSpec(
        id = "github",
        name = "GitHub",
        category = "Developer",
        defaultEndpoint = "https://api.githubcopilot.com/mcp/",
        requiresApiKey = true,
        apiKeyAsBearerHeader = true,
        description = "GitHub 官方远程 MCP: 仓库 / Issue / PR / 文件搜索。需要 GitHub Personal Access Token。",
    )

    val RAILWAY_12306 = StorePluginSpec(
        id = "12306",
        name = "12306",
        category = "Travel",
        defaultEndpoint = "",
        description = "12306 车票查询 MCP (车站 / 余票 / 中转 / 经停)。需要填入一个可访问的 12306 MCP 端点 (如自建 12306-mcp)。",
    )

    val PLUGINS = listOf(AMAP, GITHUB, RAILWAY_12306)

    fun specOf(pluginId: String): StorePluginSpec? = PLUGINS.firstOrNull { it.id == pluginId }
}

/**
 * 商店插件 <-> MCP Server 同步
 *
 * 约定: 每个商店插件在 settings.mcpServers 中以 spec.name 命名, 启用即注册/更新, 禁用即移除。
 */
object StorePluginMcpSync {

    private fun findServer(settings: Settings, spec: StorePluginSpec): McpServerConfig? {
        return settings.mcpServers.firstOrNull { it.commonOptions.name == spec.name }
    }

    /**
     * 应用商店插件配置到 settings (返回新 Settings)。
     * enabled=true 时注册/更新对应 MCP Server; false 时移除。
     */
    fun apply(
        settings: Settings,
        spec: StorePluginSpec,
        config: StorePluginConfig,
    ): Settings {
        val pluginSettings = settings.pluginSettings
        val nextPlugins = if (pluginSettings.store.isInstalled(spec.id)) {
            pluginSettings.store.plugins.toMutableMap().apply {
                put(spec.id, config)
            }
        } else {
            pluginSettings.store.plugins + (spec.id to config)
        }
        val nextPluginSettings = pluginSettings.copy(
            store = pluginSettings.store.copy(plugins = nextPlugins)
        )
        var nextSettings = settings.copy(pluginSettings = nextPluginSettings)

        val existing = findServer(nextSettings, spec)
        when {
            config.enabled && config.endpoint.isNotBlank() -> {
                val headers = if (spec.apiKeyAsBearerHeader && config.apiKey.isNotBlank()) {
                    listOf("Authorization" to "Bearer ${config.apiKey.trim()}")
                } else {
                    emptyList()
                }
                val server = McpServerConfig.StreamableHTTPServer(
                    id = existing?.id ?: kotlin.uuid.Uuid.random(),
                    commonOptions = McpCommonOptions(
                        enable = true,
                        name = spec.name,
                        headers = headers,
                    ),
                    url = spec.resolvedEndpoint(config),
                )
                nextSettings = nextSettings.copy(
                    mcpServers = if (existing == null) {
                        nextSettings.mcpServers + server
                    } else {
                        nextSettings.mcpServers.map { if (it.id == server.id) server else it }
                    }
                )
            }

            else -> {
                if (existing != null) {
                    nextSettings = nextSettings.copy(
                        mcpServers = nextSettings.mcpServers.filterNot { it.id == existing.id }
                    )
                }
            }
        }
        return nextSettings
    }

    /** 卸载: 移除插件配置并移除对应 MCP Server */
    fun uninstall(settings: Settings, spec: StorePluginSpec): Settings {
        val pluginSettings = settings.pluginSettings
        val nextPlugins = pluginSettings.store.plugins.toMutableMap().apply { remove(spec.id) }
        var nextSettings = settings.copy(
            pluginSettings = pluginSettings.copy(
                store = pluginSettings.store.copy(plugins = nextPlugins)
            )
        )
        val existing = findServer(nextSettings, spec)
        if (existing != null) {
            nextSettings = nextSettings.copy(
                mcpServers = nextSettings.mcpServers.filterNot { it.id == existing.id }
            )
        }
        return nextSettings
    }
}
