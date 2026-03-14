## v0.2.0

- 应用安装包名已改为 `io.github.king0929zion.zionchatz`，后续签名构建、Release 附件与新安装包都会使用这个新包名。
- GitHub Actions 现在会从 `app/build.gradle.kts` 读取当前 `applicationId`，并自动同步到构建时生成的 `google-services.json`，避免包名变化后再次卡住自动构建。
- 延续上一版的 Zion 风格页面壳层和自动发版链路，新的 `0.2.0` 版本将作为后续继续重构的基础版本。

## v0.1.0

- 修复翻译页语言选择器在当前 Material3 版本下的下拉菜单兼容问题，恢复 GitHub Actions 的签名 APK 构建。
- 优化统一 Zion 风格头部的长标题截断与输入组件细节，减少 WebView、表单编辑页的视觉和布局风险。
- 延续 `v0.0.1` 的主题基线、页面壳层和自动发布链路改造，本版将作为新的自动发布版本。

## v0.0.1

- 全量切到 ZionChat 风格主题基线，统一浮动头部、字体、无 ripple 点击反馈、页内表单与分组卡片视觉。
- 把 rikkahub 现有页面的头部和主容器批量改造成 Zion 风格，覆盖聊天周边、设置链路、助手详情、备份、搜索、日志、统计、分享、翻译、WebView 等页面。
- 优化发布链路：版本线重置为 `0.0.1 (1)`，默认只构建签名 APK，Release 将自动附带 APK 资产。
- 优化 APK 体积与构建速度：Release 仅输出 `arm64-v8a` 与 `universal` APK，开启 Gradle 并行/缓存/配置缓存，移除 R8 的 `-dontobfuscate` 限制。

## v2.3.0

- 修复 GitHub Actions 自动签名构建的 keystore 路径解析问题，避免继续指向旧的 `app/app.key` 位置。
- 修复首批 Zion 改造里缺失的 Compose 引用与主题导入问题，消除设置页、历史页、助手页、提示词页的编译阻塞。
- 继续推进聊天页重构，已切换 Zion 风格的聊天头部、抽屉背景、会话列表和输入区基础视觉。

## v2.2.0

- 首批接入 ZionChat 风格基础层，统一新的浮动头部、按钮反馈、字体和分组卡片视觉。
- 设置页、历史页、收藏页、助手页、提示词页开始切换到新的 Zion 风格页面壳层。
- GitHub Actions 升级为推送即自动签名构建，并支持在缺少 Firebase secret 时使用占位配置继续出包。
