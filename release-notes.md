## v0.0.37

- 继续把 RikkaHub 的聊天页收口到 ZionChat：侧边栏彻底隐藏 `Bots` 入口，顶部抽屉按钮切回 ZionChat 原生菜单图标。
- `Personalization > Memories` 页面改回原生 Android `Switch` 外观，去掉之前那套自定义黑色开关样式。
- 清理聊天残留模块：删除 `Compress History` 的设置与底层死代码，并移除最后一份 `Prompt Injections` 残留测试文件。

## v0.0.36

- 聊天主链路继续对齐 ZionChat：侧边栏移除主 `ChatGPT` 入口与 `Bots` 标题，`New Chat` 统一回到 `Personalization` 主对话，顶部 `ChatGPT` pill 改为专门唤起聊天模型选择器。
- 工具面板收口成 ZionChat 结构：删除 `Chat Model`、`Preview`、`Prompt Injections`、`Compress History`，只保留附件快捷区与 `Web Search / Reasoning / MCP`，其中 `Reasoning` 改为单开关并映射到 `thinkingBudget = -1 / 0`。
- 完整删除 `Prompt Injections / Lorebooks` 相关页面、selector、transformer、导出逻辑、数据字段与多语言文案；同时重做模型选择器与默认模型页的白底外层 + 灰色模型区层级，并补上 ZionChat 的 `Reasoning / Photo / File` 图标资源与原生黑色 `Memories` 开关。

## v0.0.35

- `Personalization / Memories` 入口与主聊天入口改为直接使用 ZionChat 原版图标，去掉之前混用的旧图标。
- 设置页 `Appearance` 入口移除介绍文案，并把设置首页相关图标统一切回 ZionChat 的原始着色显示。
- 默认模型页与通用模型选择器重新对齐 ZionChat 的灰卡层次，修正模型选择器表面色方向。

## v0.0.34

- `Memories` 页面去掉多余介绍区，只保留最近聊天开关和记忆内容列表，并把开关样式改回更接近 ZionChat 的黑色开关。
- 通用模型选择器收回成白底样式，顶部模型图标改为灰色通用 `Model` 图标，不再反着显示。
- 修正默认模型相关 bottom sheet 顶部重复出现的双指示条，只保留一套 ZionChat 风格拖拽指示。

## v0.0.33

- 去掉设置页 `Personalization` 与 `Bots` 入口的介绍文案，保留更接近 ZionChat 的纯标题入口。
- `Personalization / Memories / Bots Memory` 全面切到助手私有记忆：移除全局记忆与时间提醒实现，记忆默认常开，只保留最近聊天引用开关。
- 个性化与记忆入口图标继续收口到 Zion 风格图标，并同步把记忆说明文案改成“默认开启”的实际行为。

## v0.0.32

- 修复 `v0.0.31` 自动构建里的最后一个 personalization 记忆页编译错误：补回 `HugeIcons.Cancel01` 所需的扩展导入，恢复记忆项滑动操作区的关闭按钮。
- 保留上一轮 `Personalization / Bots / ZionChat` 选择器重构与主聊天对齐逻辑，继续沿用远端签名构建发布。

## v0.0.31

- 修复 `Personalization / Memories / 模型选择器` 相关 Compose 编译问题：移除错误的 `weight` 导入、补齐 `AutoAIIcon` 引用，并把记忆项滑动操作切回仓库稳定的 `SwipeToDismissBox` 实现。
- 保留上一轮主 AI 个性化、Bots 侧边栏分流、ZionChat 风格模型选择器与聊天工具选择器的全部改动，恢复 GitHub 自动签名构建链路。

## v0.0.30

- 新增 ZionChat 风格的 `Personalization / Memories` 页面，并把主 AI 配置与 Bots 结构拆开。
- 侧边栏改成 `ChatGPT + Bots` 分区，主对话默认走 personalization 的设定、模型与记忆。
- 默认模型选择器、聊天工具选择器和上传快捷入口继续按 ZionChat 的白卡与纯灰样式重构，并移除回复上方模型名。

## v0.0.29

- 修复 `v0.0.28` 自动构建里的 `MCP` 图标编译问题：放弃不兼容的 `ImageVector pathData` 写法，改为 ZionChat 原型对应的 drawable/painter 方案，确保设置页和聊天入口都能正常显示。
- 保留并继续发布上一轮预测返回距离收紧调整，让返回手势保持更克制的位移和更自然的层叠感。

## v0.0.28

- 修复 `MCP` 图标在设置页与聊天相关入口里不显示的问题，图标定义改成真正可渲染的 ZionChat `ModelContextProtocol` pathData。
- 单独收紧预测返回动画的位移距离和缩放幅度，让返回手势更像 ZionChat 那种“只拉一段”的层叠回退，而不是整页大幅滑走。

## v0.0.27

- 设置首页把 `Color Mode` 收口成 ZionChat 风格的 `Appearance` 入口，实际颜色模式切换下沉到 `Appearance` 页内部。
- `Appearance` 页补回更接近 ZionChat 的结构，并把开关、滑杆和灰底面板统一成纯灰体系，去掉原先偏蓝的 Material 质感。
- 全局 `MCP` 图标切换为你指定的 ZionChat `ModelContextProtocol` 图标，同时把页面切换和预测返回动画收口到更接近 ZionChat 的层叠滑动节奏。

## v0.0.26

- 供应商配置页继续按 ZionChat 收口：输入区和类型切换统一回灰底体系，配置页移除分享与测试入口，只保留模型跳转和必要操作。
- 供应商模型页重构为独立的 ZionChat 风格结构：测试入口迁移到顶栏，页面改成提供商状态卡 + 模型管理区 + 模型列表的层次，不再沿用旧的 Hero 卡布局。
- 更新供应商头像显示系统：Provider 列表、配置页和模型页都改为基于服务名称的可缩放头像图标，并补齐 ZionChat 的模型测试图标资源。

## v0.0.25

- 供应商详情页移除顶部 `配置/模型` 切换栏，恢复为 ZionChat 风格的单页配置结构；`Models` 现在只通过配置页里的入口跳转到独立模型页。
- 供应商配置页灰阶重新对齐到 ZionChat 的浅灰卡片体系，并把保存动作移回顶部右侧，页面结构更接近原始 `Add Provider` 设计。

## v0.0.24

- 修复 `v0.0.23` 自动构建中的 Provider 配置页动画编译错误，补齐浮动标签所需的 Compose `animateDp` 导入，恢复签名 Release 构建。
- 保留本轮供应商配置页 ZionChat 化重构，以及默认模型和模型服务图标切回 Zion 原版资源的全部改动。

## v0.0.23

- 供应商配置页按 ZionChat `Add Provider` 结构重做为四段式卡片布局：身份卡、配置卡、Models 跳转卡、操作卡，配置内容不再是旧的设置页堆叠样式。
- 模型服务与默认模型相关图标全部切回 ZionChat 原版 drawable，修复设置页和供应商详情页里图标看不见的问题。
- 供应商配置页切到 `Models` 时会先同步当前编辑内容，再进入模型页，避免配置修改在切页时丢失。

## v0.0.22

- 修复 `v0.0.21` 自动构建中的收尾编译问题：移除 `ChatService` 对已删除 web 模块异常类的残留引用，并修正模型服务页错误态文案的 Kotlin smart cast 问题。
- 保留本轮 `Model Services` 命名与图标对齐、供应商编辑页 Zion 化、以及 `web server/web-ui` 完整删除的所有改动。

## v0.0.21

- `Providers` 全面切换为 `Model Services` 语义，设置入口图标与命名同步对齐 ZionChat，默认模型与助手入口图标也切换为 Zion 的 `Model` 与 `Bots`。
- 供应商编辑页继续按 ZionChat 收口：配置页摘要卡与表单灰阶层级统一，模型页补上可用模型同步状态、刷新动作和更清晰的模型卡片信息。
- 完整删除内置 `web server` 与 `web-ui` 模块、设置入口、持久化字段、多语言文案与相关构建依赖，仓库只保留 Android 主应用链路。

## v0.0.20

- 重做 Provider 编辑页骨架：移除底部导航壳层，改为顶部 `配置/模型` 切换，首屏内容不再挤进顶部栏遮罩区。
- Provider 配置页按 ZionChat 节奏收口，新增供应商摘要卡和分组操作区，并把表单内部的固定顶部补白改成可控参数。

## v0.0.19

- 单独下调 Provider 配置详情页的内容起始线，API Key 等表单项不再贴到顶部遮罩区域。
- 保持上一轮其它设置页的统一顶部偏移修正不变，只继续校准 Provider 配置页。

## v0.0.18

- 统一修正设置页顶部偏移的计算方式，移除多余的状态栏叠加留白，解决部分页面过高、部分页面过近的问题。
- Provider 列表与 Provider 详情页进一步上移内容起始线，让表单和服务卡片回到更自然的顶部位置。

## v0.0.17

- 统一抬高设置页内容起始线，修正大部分设置页默认离顶部过近的问题。
- 单独收口 Provider 详情页配置内容的顶部偏移，避免表单继续冲进顶部半透明遮罩区域。
- 聊天页与导出图里的 AI 回复都改为无气泡正文，同时移除显示设置里已经失效的助手气泡开关。

## v0.0.16

- 修复 `v0.0.15` 自动构建里的最后一个 Provider 详情页壳层编译问题，底部半透明壳层改成更稳的 `fillMaxSize()` 实现。
- 保留本轮 Provider 配置页、设置模块删除、消息头像移除和悬浮跳转删除的所有功能改动。

## v0.0.15

- 修复 `v0.0.14` 自动构建里的收尾编译问题，补回消息组件与设置页仍在使用的 Compose import，并彻底移除设置页残留的 Documentation 入口。
- 保留本轮 Provider 配置页灰阶/API Key 高度、`AckAI` 清理、分享/捐赠模块删除、消息头像移除和消息跳转悬浮器删除改动。

## v0.0.14

- 继续收口 Provider 配置页：输入框和操作条灰阶统一回 Zion 主灰，API Key 改回正常单行高度，并把配置内容整体下移，避免紧贴顶部壳层。
- 删除内置 `AckAI`，升级后会自动清理旧配置里的该 Provider 与失效模型引用。
- 设置页移除 `Documentation`、`Donate`、`Share`，并一并删除对应的分享接入、捐赠页、Sponsor API 与相关路由入口。
- 聊天消息不再显示用户/AI 头像，侧边消息快速跳转悬浮器及其设置项也已移除。

## v0.0.13

- 修复 `v0.0.12` 自动构建里的 Compose 顶部遮罩导入错误，恢复签名 Release 构建。
- 保留本轮聊天页遮罩高度、工具面板动效和 Provider 配置页灰阶收口改动。

## v0.0.12

- 修正聊天页顶部遮罩高度异常：顶部半透明层不再把对话区整体压住，消息列表的上下留白也同步收紧。
- 工具面板改回更自然的底部 Sheet 唤起/收回动画，并显著降低遮罩强度，避免一打开就把整页压暗。
- 继续统一 Provider 配置详情页的 Zion 主灰，补齐顶部壳层下的底部导航、模型卡片、操作条和弹层底色。

## v0.0.11

- 侧边栏按 ZionChat 结构收口为 `New chat`、`Images`、`Statistics`，隐藏多余入口，底部头像卡片整块进入设置页。
- 默认模型页改为 `Default Model`，移除独立 `Chat model` 配置卡片，收紧标题与选择器间距，并同步更新相关说明文案。
- 内置 Provider 仅移除 `RikkaHub`、`AiHubMix`、`小马算力`、`JuheNext`、`302.AI`、`腾讯Hunyuan`，升级后会自动清理旧数据和失效模型引用。
- `Response API` 仅对 OpenAI 官方 `api.openai.com` 显示，非官方地址会自动关闭该选项；同时移除额外提示文案和硅基流动的 powered-by 图。
- 统一默认模型页、Provider 页、侧边栏和设置头部的 Zion 主灰，并修正聊天页列表与输入区 padding 叠加导致的内容遮挡问题。

## v0.0.10

- 回退应用代码到上一个可用发布版 `v0.0.7` 的界面与交互状态，撤回 `v0.0.8/v0.0.9` 这轮默认模型、Provider、侧边栏和聊天可见性收口改动。
- 保留新的 GitHub Release 发布说明提取逻辑，后续 Release 仍只显示当前版本段落，不再重复整份历史说明。

## v0.0.9

- 修复 `v0.0.8` 自动签名构建暴露的 Provider 详情页编译问题：改回当前 Material3 版本可用的 `SegmentedButton` 参数形式，并去掉错误的 `weight` 导入，恢复自动签名 Release。
- 延续 `v0.0.8` 的 ZionChat 收口：继续保留新的灰阶、默认模型页、Provider 列表与聊天消息可见性修复。

## v0.0.8

- 修复 Release 说明重复：GitHub Actions 现在只提取当前版本对应的段落发布，不再把整份 `release-notes.md` 重复挂到每个 Release。
- 收紧 ZionChat 灰度与设置骨架：统一灰阶 token、降低顶部半透明遮罩强度，修正设置主页、默认模型页、Provider 页的顶部起始位置与分组卡片灰色。
- 侧边栏按新结构重排：保留顶部搜索和新建按钮，菜单区只显示 `New Chat`、`Images`、`Statistics`，底部头像整块区域进入设置页。
- 默认模型页改为 `Default Model`，移除 `CHAT` 分组；Provider 列表改成更接近 `Model Services` 的单列服务行，并删除内置的 `RikkaHub`、`AiHubMix`、`小马算力`、`JuheNext`、`302.AI`、`腾讯Hunyuan`。
- Provider 配置页继续收口：输入框统一为大圆角椭圆样式，只在 OpenAI 官方地址时显示 `Response API` 选项，移除额外品牌图和相关提示文案。
- 聊天页继续修复消息可见性：收紧消息列表 padding、固定消息气泡横向布局，并让 Markdown 更严格继承正文颜色，减少消息被遮挡或看不见的情况。

## v0.0.7

- 修复 `v0.0.6` 自动签名构建暴露的编译问题，恢复 GitHub Actions 的签名 APK 构建。
- 继续把聊天消息与 Provider 配置页收口到 ZionChat 风格：消息气泡和正文颜色更稳定，Provider 配置页移除 `enable` 与余额入口，并让已配置 Provider 默认视为启用。
- 优化聊天输入区和工具面板底部区域，让阴影和导航栏区域跟随输入区与工具面板一起抬升显示。

## v0.0.6

- 修复聊天页消息仍然不易显示的问题，补强显式消息气泡与正文颜色传递。
- Provider 通用配置页改成更接近 ZionChat 的大圆角椭圆输入和黑白灰开关卡片。
- 聊天页顶部 `ChatGPT` 标识放大，输入区与工具面板继续往 ZionChat 收口。

## v0.0.5

- 聊天页工具面板按 ZionChat 结构改成统一底部工具面板。
- Provider 列表页按 ZionChat `Model Services` 骨架改成单列服务行。
- 修复聊天消息在当前主题下不易显示的问题。

## v0.0.4

- 修复聊天页左下工具按钮点击即闪退的问题，原因是权限状态在错误的弹层上下文里创建。
- 工具面板的颜色和附件胶囊继续回收到 ZionChat 的白底浅灰卡片体系。
- Release 从这一版起继续自动附带 APK 资产。

## v0.0.3

- 聊天工具菜单改成更接近 ZionChat 的底部弹出面板。
- 修正设置页的公共灰度与头部模糊表现。
- 修正设置主页和核心设置页的顶部起始位置。

## v0.0.2

- 修复 `0.0.1` 首轮自动签名构建暴露的编译问题。
- 展示版本号改成 `0.0.x` 递增，同时保持内部构建号继续递增。
- 设置链路继续统一黑白中性风格并减轻顶部遮罩。

## v0.0.1

- 展示版本线重置为 `0.0.1`，后续继续沿用 `0.0.x`。
- 接入 ZionChat 风格主题基线、浮动头部、设置页分组卡片与聊天壳层。
- GitHub Actions 改成推送即自动签名构建并自动附带 APK。

## v0.7.0

- 继续按 ZionChat 收口聊天、设置、侧边栏和主要图标风格。
- 修复设置链路的内容遮挡并补齐多个子页的可见内容。
- 应用名称与图标更新为 `ZionChat Z`，关于页链接切到新仓库。

## v0.5.0

- 修复聊天页、输入区、侧边栏和统一顶栏改造中的一批 Compose 编译问题。
- 延续 ZionChat 聊天壳层重构，保留顶部胶囊导航、胶囊输入框与 Zion 风格侧边栏。

## v0.4.0

- 聊天页顶部改成更贴近 ZionChat 的单层导航。
- 输入区改成“工具按钮 + 胶囊输入框”的统一结构。
- 侧边栏切换到 ZionChat 式搜索、菜单、会话和资料卡布局。

## v0.3.0

- 修复部分设备因 `libsimple.so` 未正确落地导致的启动后点击即崩溃问题。
- 聊天页顶部、输入区和关键图标继续向 ZionChat 收口。

## v0.2.0

- 应用安装包名改为 `io.github.king0929zion.zionchatz`。
- GitHub Actions 会从 `app/build.gradle.kts` 读取当前 `applicationId` 并同步到构建时的 `google-services.json`。

## v0.1.0

- 修复翻译页语言选择器在当前 Material3 版本下的兼容问题。
- 延续首版 Zion 风格主题基线和自动发布链路。

## v2.3.0

- 修复 GitHub Actions 自动签名构建的 keystore 路径解析问题。
- 修复第一批 Zion 改造中的 Compose 引用和主题导入问题，继续推进聊天页视觉收口。

## v2.2.0

- 首批接入 ZionChat 风格基础层与设置页、历史页、收藏页、助手页、提示词页的新壳层。
- GitHub Actions 升级为推送即自动签名构建，并支持在缺少 Firebase secret 时使用占位配置继续出包。
