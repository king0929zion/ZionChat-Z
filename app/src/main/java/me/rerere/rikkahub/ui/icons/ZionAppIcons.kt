package me.rerere.rikkahub.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object ZionAppIcons {
    val HamburgerMenu = ImageVector.Builder(
        name = "hamburger_menu",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C1C1E)),
            stroke = null,
            strokeLineWidth = 0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(4f, 8f)
            horizontalLineToRelative(16f)
            arcToRelative(1f, 1f, 0f, true, true, 0f, -2f)
            horizontalLineTo(4f)
            arcToRelative(1f, 1f, 0f, true, false, 0f, -2f)
            close()
            moveTo(4f, 13f)
            horizontalLineToRelative(10f)
            arcToRelative(1f, 1f, 0f, true, false, 0f, -2f)
            horizontalLineTo(4f)
            arcToRelative(1f, 1f, 0f, true, false, 0f, -2f)
            close()
        }
    }.build()

    val NewChat = ImageVector.Builder(
        name = "new_chat",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null
        ) {
            moveTo(15.673f, 3.913f)
            curveTo(16.892f, 2.694f, 18.868f, 2.694f, 20.087f, 3.913f)
            curveTo(21.306f, 5.132f, 21.306f, 7.108f, 20.087f, 8.327f)
            lineTo(14.15f, 14.264f)
            curveTo(13.385f, 15.029f, 12.392f, 15.525f, 11.321f, 15.678f)
            lineTo(9.141f, 15.99f)
            curveTo(8.83f, 16.034f, 8.515f, 15.929f, 8.293f, 15.707f)
            curveTo(8.07f, 15.484f, 7.966f, 15.17f, 8.01f, 14.858f)
            lineTo(8.321f, 12.678f)
            curveTo(8.474f, 11.607f, 8.971f, 10.615f, 9.736f, 9.85f)
            lineTo(15.673f, 3.913f)
            close()
            moveTo(18.673f, 5.327f)
            curveTo(18.235f, 4.889f, 17.525f, 4.889f, 17.087f, 5.327f)
            lineTo(11.15f, 11.264f)
            curveTo(10.691f, 11.723f, 10.393f, 12.319f, 10.301f, 12.961f)
            lineTo(10.179f, 13.821f)
            lineTo(11.039f, 13.698f)
            curveTo(11.681f, 13.607f, 12.277f, 13.309f, 12.736f, 12.85f)
            lineTo(18.673f, 6.913f)
            curveTo(19.111f, 6.475f, 19.111f, 5.765f, 18.673f, 5.327f)
            close()
            moveTo(11f, 3.999f)
            curveTo(11f, 4.551f, 10.553f, 5f, 10.001f, 5f)
            curveTo(9.002f, 5.001f, 8.298f, 5.008f, 7.747f, 5.061f)
            curveTo(7.207f, 5.112f, 6.885f, 5.201f, 6.638f, 5.327f)
            curveTo(6.074f, 5.614f, 5.615f, 6.073f, 5.327f, 6.638f)
            curveTo(5.193f, 6.901f, 5.101f, 7.249f, 5.051f, 7.854f)
            curveTo(5.001f, 8.471f, 5f, 9.263f, 5f, 10.4f)
            verticalLineTo(13.6f)
            curveTo(5f, 14.736f, 5.001f, 15.529f, 5.051f, 16.146f)
            curveTo(5.101f, 16.751f, 5.193f, 17.098f, 5.327f, 17.362f)
            curveTo(5.615f, 17.926f, 6.074f, 18.385f, 6.638f, 18.673f)
            curveTo(6.901f, 18.807f, 7.249f, 18.899f, 7.854f, 18.949f)
            curveTo(8.471f, 18.999f, 9.263f, 19f, 10.4f, 19f)
            horizontalLineTo(13.6f)
            curveTo(14.737f, 19f, 15.529f, 18.999f, 16.146f, 18.949f)
            curveTo(16.751f, 18.899f, 17.099f, 18.807f, 17.362f, 18.673f)
            curveTo(17.927f, 18.385f, 18.385f, 17.926f, 18.673f, 17.362f)
            curveTo(18.799f, 17.115f, 18.888f, 16.793f, 18.939f, 16.253f)
            curveTo(18.992f, 15.702f, 18.999f, 14.998f, 19f, 13.999f)
            curveTo(19f, 13.447f, 19.448f, 12.999f, 20.001f, 13f)
            curveTo(20.553f, 13f, 21f, 13.448f, 21f, 14.001f)
            curveTo(20.999f, 14.979f, 20.993f, 15.781f, 20.93f, 16.442f)
            curveTo(20.866f, 17.116f, 20.739f, 17.713f, 20.455f, 18.27f)
            curveTo(19.976f, 19.211f, 19.211f, 19.976f, 18.27f, 20.455f)
            curveTo(17.678f, 20.757f, 17.038f, 20.882f, 16.309f, 20.942f)
            curveTo(15.601f, 21f, 14.727f, 21f, 13.643f, 21f)
            horizontalLineTo(10.357f)
            curveTo(9.273f, 21f, 8.399f, 21f, 7.691f, 20.942f)
            curveTo(6.963f, 20.882f, 6.322f, 20.757f, 5.73f, 20.455f)
            curveTo(4.789f, 19.976f, 4.024f, 19.211f, 3.545f, 18.27f)
            curveTo(3.243f, 17.677f, 3.117f, 17.037f, 3.058f, 16.309f)
            curveTo(3f, 15.601f, 3f, 14.726f, 3f, 13.643f)
            verticalLineTo(10.357f)
            curveTo(3f, 9.273f, 3f, 8.399f, 3.058f, 7.691f)
            curveTo(3.117f, 6.962f, 3.243f, 6.322f, 3.545f, 5.73f)
            curveTo(4.024f, 4.789f, 4.789f, 4.024f, 5.73f, 3.545f)
            curveTo(6.286f, 3.261f, 6.884f, 3.133f, 7.557f, 3.069f)
            curveTo(8.219f, 3.007f, 9.021f, 3.001f, 9.999f, 3f)
            curveTo(10.552f, 3f, 11f, 3.447f, 11f, 3.999f)
            close()
        }
    }.build()

    val Back = ImageVector.Builder(
        name = "back",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 12f)
            horizontalLineTo(5f)
            moveTo(12f, 19f)
            lineTo(5f, 12f)
            lineTo(12f, 5f)
        }
    }.build()

    val Plus = ImageVector.Builder(
        name = "plus",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 5f)
            verticalLineTo(19f)
            moveTo(5f, 12f)
            horizontalLineTo(19f)
        }
    }.build()

    val Camera = ImageVector.Builder(
        name = "camera",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null
        ) {
            moveTo(12f, 4f)
            arcToRelative(3f, 3f, 0f, false, false, -2.6f, 1.5f)
            arcToRelative(1f, 1f, 0f, false, true, -0.865f, 0.5f)
            horizontalLineTo(5f)
            arcToRelative(1f, 1f, 0f, false, false, -1f, 1f)
            verticalLineToRelative(11f)
            arcToRelative(1f, 1f, 0f, false, false, 1f, 1f)
            horizontalLineToRelative(14f)
            arcToRelative(1f, 1f, 0f, false, false, 1f, -1f)
            verticalLineTo(7f)
            arcToRelative(1f, 1f, 0f, false, false, -1f, -1f)
            horizontalLineToRelative(-3.535f)
            arcToRelative(1f, 1f, 0f, false, true, -0.866f, -0.5f)
            arcToRelative(2.998f, 2.998f, 0f, false, false, -2.599f, -1.5f)
            close()
            moveTo(8f, 4f)
            arcToRelative(4.993f, 4.993f, 0f, false, true, 4f, -2f)
            arcToRelative(4.99f, 4.99f, 0f, false, true, 4f, 2f)
            horizontalLineToRelative(3f)
            arcToRelative(3f, 3f, 0f, false, true, 3f, 3f)
            verticalLineToRelative(11f)
            arcToRelative(3f, 3f, 0f, false, true, -3f, 3f)
            horizontalLineTo(5f)
            arcToRelative(3f, 3f, 0f, false, true, -3f, -3f)
            verticalLineTo(7f)
            arcToRelative(3f, 3f, 0f, false, true, 3f, -3f)
            horizontalLineToRelative(3f)
            close()
            moveTo(12f, 10f)
            arcToRelative(2f, 2f, 0f, true, false, 0f, 4f)
            arcToRelative(2f, 2f, 0f, true, false, 0f, -4f)
            close()
            moveTo(8f, 12f)
            arcToRelative(4f, 4f, 0f, true, true, 8f, 0f)
            arcToRelative(4f, 4f, 0f, true, true, -8f, 0f)
            close()
        }
    }.build()

    val Files = ImageVector.Builder(
        name = "files",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null
        ) {
            moveTo(9f, 7f)
            arcToRelative(5f, 5f, 0f, false, true, 10f, 0f)
            verticalLineToRelative(8f)
            arcToRelative(7f, 7f, 0f, true, true, -14f, 0f)
            verticalLineTo(9f)
            arcToRelative(1f, 1f, 0f, false, true, 2f, 0f)
            verticalLineToRelative(6f)
            arcToRelative(5f, 5f, 0f, false, false, 10f, 0f)
            verticalLineTo(7f)
            arcToRelative(3f, 3f, 0f, true, false, -6f, 0f)
            verticalLineTo(8f)
            arcToRelative(1f, 1f, 0f, true, false, 2f, 0f)
            verticalLineTo(9f)
            arcToRelative(1f, 1f, 0f, true, true, 2f, 0f)
            verticalLineTo(6f)
            arcToRelative(3f, 3f, 0f, true, true, -6f, 0f)
            verticalLineTo(7f)
            close()
        }
    }.build()

    val Globe = ImageVector.Builder(
        name = "globe",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null
        ) {
            moveTo(12f, 2f)
            curveTo(17.523f, 2f, 22f, 6.477f, 22f, 12f)
            curveTo(22f, 17.523f, 17.523f, 22f, 12f, 22f)
            curveTo(6.477f, 22f, 2f, 17.523f, 2f, 12f)
            curveTo(2f, 6.477f, 6.477f, 2f, 12f, 2f)
            close()
            moveTo(9.771f, 13f)
            curveTo(9.854f, 14.99f, 10.179f, 16.742f, 10.643f, 18.024f)
            curveTo(10.914f, 18.775f, 11.213f, 19.313f, 11.495f, 19.644f)
            curveTo(11.781f, 19.978f, 11.955f, 20f, 12f, 20f)
            curveTo(12.045f, 20f, 12.219f, 19.978f, 12.505f, 19.644f)
            curveTo(12.787f, 19.313f, 13.086f, 18.775f, 13.357f, 18.024f)
            curveTo(13.821f, 16.742f, 14.146f, 14.99f, 14.229f, 13f)
            horizontalLineTo(9.771f)
            close()
            moveTo(4.064f, 13f)
            curveTo(4.431f, 15.941f, 6.394f, 18.385f, 9.06f, 19.44f)
            curveTo(8.953f, 19.204f, 8.854f, 18.958f, 8.762f, 18.703f)
            curveTo(8.208f, 17.171f, 7.853f, 15.181f, 7.77f, 13f)
            horizontalLineTo(4.064f)
            close()
            moveTo(16.23f, 13f)
            curveTo(16.147f, 15.181f, 15.792f, 17.171f, 15.238f, 18.703f)
            curveTo(15.146f, 18.958f, 15.046f, 19.204f, 14.939f, 19.44f)
            curveTo(17.606f, 18.385f, 19.569f, 15.942f, 19.935f, 13f)
            horizontalLineTo(16.23f)
            close()
            moveTo(14.939f, 4.56f)
            curveTo(15.046f, 4.796f, 15.146f, 5.042f, 15.238f, 5.297f)
            curveTo(15.792f, 6.829f, 16.147f, 8.819f, 16.23f, 11f)
            horizontalLineTo(19.935f)
            curveTo(19.569f, 8.058f, 17.606f, 5.614f, 14.939f, 4.56f)
            close()
            moveTo(12f, 4f)
            curveTo(11.955f, 4f, 11.781f, 4.022f, 11.495f, 4.356f)
            curveTo(11.213f, 4.687f, 10.914f, 5.225f, 10.643f, 5.976f)
            curveTo(10.179f, 7.258f, 9.854f, 9.01f, 9.771f, 11f)
            horizontalLineTo(14.229f)
            curveTo(14.146f, 9.01f, 13.821f, 7.258f, 13.357f, 5.976f)
            curveTo(13.086f, 5.225f, 12.787f, 4.687f, 12.505f, 4.356f)
            curveTo(12.219f, 4.022f, 12.045f, 4f, 12f, 4f)
            close()
            moveTo(9.06f, 4.56f)
            curveTo(6.394f, 5.614f, 4.431f, 8.058f, 4.064f, 11f)
            horizontalLineTo(7.77f)
            curveTo(7.853f, 8.819f, 8.208f, 6.829f, 8.762f, 5.297f)
            curveTo(8.854f, 5.042f, 8.953f, 4.795f, 9.06f, 4.56f)
            close()
        }
    }.build()

    val Close = ImageVector.Builder(
        name = "close",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF8E8E93)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(18f, 6f)
            lineTo(6f, 18f)
            moveTo(6f, 6f)
            lineTo(18f, 18f)
        }
    }.build()

    val Send = ImageVector.Builder(
        name = "send",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 19f)
            verticalLineTo(5f)
            moveTo(5f, 12f)
            lineTo(12f, 5f)
            lineTo(19f, 12f)
        }
    }.build()

    val Think = ImageVector.Builder(
        name = "think",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF8E8E93)),
            stroke = null
        ) {
            moveTo(12f, 2f)
            curveTo(8.134f, 2f, 5f, 5.134f, 5f, 9f)
            curveTo(5f, 11.422f, 6.154f, 13.576f, 8f, 14.915f)
            verticalLineTo(17f)
            curveTo(8f, 17.552f, 8.448f, 18f, 9f, 18f)
            horizontalLineTo(10f)
            verticalLineTo(19f)
            curveTo(10f, 19.552f, 10.448f, 20f, 11f, 20f)
            horizontalLineTo(13f)
            curveTo(13.552f, 20f, 14f, 19.552f, 14f, 19f)
            verticalLineTo(18f)
            horizontalLineTo(15f)
            curveTo(15.552f, 18f, 16f, 17.552f, 16f, 17f)
            verticalLineTo(14.915f)
            curveTo(17.846f, 13.576f, 19f, 11.422f, 19f, 9f)
            curveTo(19f, 5.134f, 15.866f, 2f, 12f, 2f)
            close()
            moveTo(12f, 4f)
            curveTo(14.761f, 4f, 17f, 6.239f, 17f, 9f)
            curveTo(17f, 10.797f, 16.062f, 12.373f, 14.583f, 13.238f)
            curveTo(14.237f, 13.439f, 14f, 13.804f, 14f, 14.207f)
            verticalLineTo(16f)
            horizontalLineTo(10f)
            verticalLineTo(14.207f)
            curveTo(10f, 13.804f, 9.763f, 13.439f, 9.417f, 13.238f)
            curveTo(7.938f, 12.373f, 7f, 10.797f, 7f, 9f)
            curveTo(7f, 6.239f, 9.239f, 4f, 12f, 4f)
            close()
            moveTo(12f, 6.5f)
            curveTo(11.172f, 6.5f, 10.5f, 7.172f, 10.5f, 8f)
            curveTo(10.5f, 8.828f, 11.172f, 9.5f, 12f, 9.5f)
            curveTo(12.828f, 9.5f, 13.5f, 8.828f, 13.5f, 8f)
            curveTo(13.5f, 7.172f, 12.828f, 6.5f, 12f, 6.5f)
            close()
        }
    }.build()

    val Tool = ImageVector.Builder(
        name = "tool",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF8E8E93)),
            stroke = null,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(14.7f, 6.3f)
            arcTo(1f, 1f, 0f, false, false, 14.7f, 7.7f)
            lineTo(16.3f, 9.3f)
            arcTo(1f, 1f, 0f, false, false, 17.7f, 9.3f)
            lineTo(21.47f, 5.53f)
            arcTo(6f, 6f, 0f, false, true, 13.53f, 13.47f)
            lineTo(6.62f, 20.38f)
            arcTo(2.12f, 2.12f, 0f, false, true, 3.62f, 17.38f)
            lineTo(10.53f, 10.47f)
            arcTo(6f, 6f, 0f, false, true, 18.47f, 2.53f)
            lineTo(14.71f, 6.29f)
            close()
        }
    }.build()

    val Image = ImageVector.Builder(
        name = "image",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3f, 3f)
            lineTo(21f, 3f)
            lineTo(21f, 21f)
            lineTo(3f, 21f)
            close()
            moveTo(8.5f, 8.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 0.01f, 0f)
            close()
            moveTo(21f, 15f)
            lineTo(16f, 10f)
            lineTo(5f, 21f)
        }
    }.build()

    val Search = ImageVector.Builder(
        name = "search",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2.2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(10.5f, 18f)
            arcTo(7.5f, 7.5f, 0f, true, true, 10.5f, 3f)
            arcTo(7.5f, 7.5f, 0f, false, true, 10.5f, 18f)
            moveTo(16.5f, 16.5f)
            lineTo(21f, 21f)
        }
    }.build()

    val History = ImageVector.Builder(
        name = "history",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2.2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(4f, 12f)
            arcTo(8f, 8f, 0f, true, true, 6.343f, 17.657f)
            moveTo(4f, 12f)
            lineTo(4f, 6.5f)
            moveTo(4f, 12f)
            lineTo(8.5f, 12f)
            moveTo(12f, 8f)
            lineTo(12f, 12f)
            lineTo(15f, 14f)
        }
    }.build()

    val Favorite = ImageVector.Builder(
        name = "favorite",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 20f)
            curveTo(11f, 19.2f, 4f, 14.3f, 4f, 9.2f)
            curveTo(4f, 6.5f, 6f, 4.5f, 8.6f, 4.5f)
            curveTo(10.2f, 4.5f, 11.2f, 5.3f, 12f, 6.4f)
            curveTo(12.8f, 5.3f, 13.8f, 4.5f, 15.4f, 4.5f)
            curveTo(18f, 4.5f, 20f, 6.5f, 20f, 9.2f)
            curveTo(20f, 14.3f, 13f, 19.2f, 12f, 20f)
        }
    }.build()

    val Stats = ImageVector.Builder(
        name = "stats",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2.2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5f, 19f)
            lineTo(19f, 19f)
            moveTo(7.5f, 19f)
            lineTo(7.5f, 11f)
            moveTo(12f, 19f)
            lineTo(12f, 7f)
            moveTo(16.5f, 19f)
            lineTo(16.5f, 13.5f)
        }
    }.build()

    val Settings = ImageVector.Builder(
        name = "settings",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2.2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5f, 7f)
            lineTo(19f, 7f)
            moveTo(5f, 17f)
            lineTo(19f, 17f)
            moveTo(8f, 12f)
            lineTo(19f, 12f)
            moveTo(12f, 7f)
            arcTo(2f, 2f, 0f, true, true, 12f, 3f)
            arcTo(2f, 2f, 0f, false, true, 12f, 7f)
            moveTo(16f, 17f)
            arcTo(2f, 2f, 0f, true, true, 16f, 13f)
            arcTo(2f, 2f, 0f, false, true, 16f, 17f)
            moveTo(8f, 12f)
            arcTo(2f, 2f, 0f, true, true, 8f, 8f)
            arcTo(2f, 2f, 0f, false, true, 8f, 12f)
        }
    }.build()

    val Bot = ImageVector.Builder(
        name = "bot",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 8f)
            verticalLineTo(4f)
            horizontalLineTo(8f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(6f, 8f)
            horizontalLineTo(18f)
            arcTo(2f, 2f, 0f, false, true, 20f, 10f)
            verticalLineTo(18f)
            arcTo(2f, 2f, 0f, false, true, 18f, 20f)
            horizontalLineTo(6f)
            arcTo(2f, 2f, 0f, false, true, 4f, 18f)
            verticalLineTo(10f)
            arcTo(2f, 2f, 0f, false, true, 6f, 8f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(2f, 14f)
            horizontalLineTo(4f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(20f, 14f)
            horizontalLineTo(22f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(15f, 13f)
            verticalLineTo(15f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9f, 13f)
            verticalLineTo(15f)
        }
    }.build()

    val Assistant = Bot

    val Model = ImageVector.Builder(
        name = "model",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            pathFillType = PathFillType.NonZero
        ) {
            addPathNodes(
                "M491,621q70,0 119,-45t49,-109q0,-57 -36.5,-96.5T534,331q-47,0 -79.5,30T422,435q0,19 7.5,37t21.5,33l57,-57q-3,-2 -4.5,-5t-1.5,-7q0,-11 9,-17.5t23,-6.5q20,0 33,16.5t13,39.5q0,31 -25.5,52.5T492,542q-47,0 -79.5,-38T380,411q0,-29 11,-55.5t31,-46.5l-57,-57q-32,31 -49,72t-17,86q0,88 56,149.5T491,621ZM240,880v-172q-57,-52 -88.5,-121.5T120,440q0,-150 105,-255t255,-105q125,0 221.5,73.5T827,345l52,205q5,19 -7,34.5T840,600h-80v120q0,33 -23.5,56.5T680,800h-80v80h-80v-160h160v-200h108l-38,-155q-23,-91 -98,-148t-172,-57q-116,0 -198,81t-82,197q0,60 24.5,114t69.5,96l26,24v208h-80ZM494,520Z"
            )
        }
    }.build()

    val ModelServices = ImageVector.Builder(
        name = "model_services",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1F1F1F)),
            stroke = null,
            pathFillType = PathFillType.NonZero
        ) {
            addPathNodes(
                "M260,800q-91,0 -155.5,-63T40,583q0,-78 47,-139t123,-78q25,-92 100,-149t170,-57q117,0 198.5,81.5T760,440q69,8 114.5,59.5T920,620q0,75 -52.5,127.5T740,800L260,800ZM260,720h480q42,0 71,-29t29,-71q0,-42 -29,-71t-71,-29h-60v-80q0,-83 -58.5,-141.5T480,240q-83,0 -141.5,58.5T280,440h-20q-58,0 -99,41t-41,99q0,58 41,99t99,41ZM480,480Z"
            )
        }
    }.build()

    val ChatGPTLogo = ImageVector.Builder(
        name = "chatgpt_logo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C1C1E)),
            stroke = null
        ) {
            moveTo(12.18f, 11.818f)
            curveTo(12.295f, 12.638f, 11.723f, 13.397f, 10.903f, 13.512f)
            curveTo(10.083f, 13.627f, 9.324f, 13.056f, 9.209f, 12.235f)
            curveTo(9.094f, 11.415f, 9.665f, 10.657f, 10.485f, 10.541f)
            curveTo(11.306f, 10.426f, 12.064f, 10.998f, 12.18f, 11.818f)
            close()
            moveTo(14.279f, 1.707f)
            lineTo(17.736f, 2.132f)
            curveTo(18.535f, 2.23f, 19.194f, 2.311f, 19.726f, 2.421f)
            curveTo(20.278f, 2.535f, 20.779f, 2.696f, 21.231f, 3f)
            curveTo(21.931f, 3.473f, 22.464f, 4.154f, 22.753f, 4.948f)
            curveTo(22.939f, 5.46f, 22.974f, 5.985f, 22.951f, 6.548f)
            curveTo(22.929f, 7.091f, 22.848f, 7.75f, 22.75f, 8.549f)
            lineTo(22.326f, 12.006f)
            curveTo(22.228f, 12.805f, 22.146f, 13.464f, 22.037f, 13.996f)
            curveTo(21.923f, 14.549f, 21.762f, 15.049f, 21.457f, 15.501f)
            curveTo(20.985f, 16.201f, 20.303f, 16.734f, 19.509f, 17.023f)
            curveTo(19.428f, 17.052f, 19.345f, 17.079f, 19.262f, 17.101f)
            curveTo(18.729f, 17.246f, 18.18f, 16.931f, 18.035f, 16.398f)
            curveTo(17.89f, 15.865f, 18.205f, 15.316f, 18.738f, 15.171f)
            curveTo(18.769f, 15.163f, 18.797f, 15.154f, 18.825f, 15.144f)
            curveTo(19.222f, 14.999f, 19.563f, 14.733f, 19.799f, 14.382f)
            curveTo(19.897f, 14.237f, 19.989f, 14.022f, 20.078f, 13.592f)
            curveTo(20.169f, 13.149f, 20.241f, 12.571f, 20.346f, 11.721f)
            lineTo(20.76f, 8.347f)
            curveTo(20.864f, 7.496f, 20.934f, 6.918f, 20.953f, 6.467f)
            curveTo(20.971f, 6.028f, 20.934f, 5.797f, 20.874f, 5.632f)
            curveTo(20.729f, 5.235f, 20.463f, 4.894f, 20.112f, 4.658f)
            curveTo(19.967f, 4.56f, 19.752f, 4.468f, 19.322f, 4.379f)
            curveTo(18.879f, 4.288f, 18.302f, 4.216f, 17.451f, 4.112f)
            lineTo(14.077f, 3.698f)
            curveTo(13.226f, 3.593f, 12.648f, 3.523f, 12.197f, 3.505f)
            curveTo(11.758f, 3.487f, 11.527f, 3.524f, 11.362f, 3.584f)
            curveTo(10.965f, 3.728f, 10.624f, 3.995f, 10.388f, 4.345f)
            curveTo(10.321f, 4.444f, 10.257f, 4.576f, 10.196f, 4.784f)
            curveTo(10.039f, 5.314f, 9.482f, 5.616f, 8.953f, 5.459f)
            curveTo(8.423f, 5.302f, 8.121f, 4.745f, 8.278f, 4.216f)
            curveTo(8.383f, 3.862f, 8.523f, 3.533f, 8.73f, 3.226f)
            curveTo(9.203f, 2.526f, 9.884f, 1.993f, 10.678f, 1.704f)
            curveTo(11.19f, 1.518f, 11.715f, 1.483f, 12.278f, 1.506f)
            curveTo(12.821f, 1.528f, 13.48f, 1.609f, 14.279f, 1.707f)
            close()
            moveTo(11.76f, 8.8f)
            curveTo(11.309f, 8.819f, 10.731f, 8.889f, 9.881f, 8.993f)
            lineTo(6.506f, 9.408f)
            curveTo(5.656f, 9.512f, 5.078f, 9.584f, 4.636f, 9.675f)
            curveTo(4.205f, 9.764f, 3.99f, 9.856f, 3.845f, 9.954f)
            curveTo(3.495f, 10.19f, 3.228f, 10.531f, 3.084f, 10.928f)
            curveTo(3.024f, 11.093f, 2.987f, 11.323f, 3.005f, 11.763f)
            curveTo(3.023f, 12.214f, 3.093f, 12.792f, 3.198f, 13.642f)
            lineTo(3.475f, 15.903f)
            curveTo(3.684f, 15.676f, 3.877f, 15.475f, 4.059f, 15.3f)
            curveTo(4.45f, 14.926f, 4.85f, 14.616f, 5.335f, 14.418f)
            curveTo(6.09f, 14.109f, 6.922f, 14.039f, 7.719f, 14.216f)
            curveTo(8.231f, 14.33f, 8.676f, 14.568f, 9.124f, 14.872f)
            curveTo(9.555f, 15.163f, 10.05f, 15.56f, 10.649f, 16.039f)
            lineTo(14.314f, 18.971f)
            curveTo(14.455f, 18.798f, 14.566f, 18.603f, 14.644f, 18.391f)
            curveTo(14.704f, 18.226f, 14.741f, 17.995f, 14.723f, 17.556f)
            curveTo(14.705f, 17.105f, 14.634f, 16.527f, 14.53f, 15.676f)
            lineTo(14.116f, 12.302f)
            curveTo(14.011f, 11.452f, 13.939f, 10.874f, 13.848f, 10.432f)
            curveTo(13.759f, 10.001f, 13.667f, 9.786f, 13.569f, 9.641f)
            curveTo(13.333f, 9.29f, 12.992f, 9.024f, 12.595f, 8.88f)
            curveTo(12.43f, 8.82f, 12.2f, 8.782f, 11.76f, 8.8f)
            close()
            moveTo(12.143f, 19.795f)
            lineTo(9.431f, 17.626f)
            curveTo(8.793f, 17.115f, 8.36f, 16.77f, 8.003f, 16.528f)
            curveTo(7.657f, 16.293f, 7.45f, 16.206f, 7.283f, 16.169f)
            curveTo(6.885f, 16.08f, 6.469f, 16.115f, 6.092f, 16.269f)
            curveTo(5.934f, 16.334f, 5.745f, 16.455f, 5.443f, 16.745f)
            curveTo(5.131f, 17.043f, 4.762f, 17.456f, 4.219f, 18.066f)
            lineTo(3.813f, 18.523f)
            curveTo(3.834f, 18.654f, 3.856f, 18.775f, 3.879f, 18.887f)
            curveTo(3.968f, 19.318f, 4.06f, 19.533f, 4.158f, 19.678f)
            curveTo(4.394f, 20.028f, 4.735f, 20.295f, 5.132f, 20.439f)
            curveTo(5.297f, 20.499f, 5.528f, 20.536f, 5.967f, 20.518f)
            curveTo(6.418f, 20.5f, 6.996f, 20.43f, 7.847f, 20.326f)
            lineTo(11.221f, 19.911f)
            curveTo(11.572f, 19.868f, 11.876f, 19.831f, 12.143f, 19.795f)
            close()
            moveTo(11.679f, 6.802f)
            curveTo(12.242f, 6.779f, 12.767f, 6.814f, 13.279f, 7f)
            curveTo(14.073f, 7.289f, 14.755f, 7.822f, 15.227f, 8.522f)
            curveTo(15.532f, 8.974f, 15.693f, 9.475f, 15.807f, 10.027f)
            curveTo(15.917f, 10.559f, 15.998f, 11.218f, 16.096f, 12.017f)
            lineTo(16.52f, 15.474f)
            curveTo(16.618f, 16.273f, 16.699f, 16.932f, 16.721f, 17.475f)
            curveTo(16.744f, 18.038f, 16.709f, 18.563f, 16.523f, 19.075f)
            curveTo(16.234f, 19.869f, 15.701f, 20.551f, 15.001f, 21.023f)
            curveTo(14.549f, 21.327f, 14.049f, 21.488f, 13.496f, 21.602f)
            curveTo(12.964f, 21.712f, 12.305f, 21.793f, 11.506f, 21.891f)
            lineTo(8.049f, 22.316f)
            curveTo(7.25f, 22.414f, 6.591f, 22.495f, 6.048f, 22.517f)
            curveTo(5.485f, 22.54f, 4.96f, 22.505f, 4.448f, 22.319f)
            curveTo(3.654f, 22.03f, 2.973f, 21.497f, 2.5f, 20.797f)
            curveTo(2.196f, 20.345f, 2.035f, 19.844f, 1.921f, 19.292f)
            curveTo(1.811f, 18.76f, 1.73f, 18.101f, 1.632f, 17.302f)
            lineTo(1.207f, 13.845f)
            curveTo(1.109f, 13.046f, 1.028f, 12.387f, 1.006f, 11.844f)
            curveTo(0.983f, 11.281f, 1.018f, 10.756f, 1.204f, 10.244f)
            curveTo(1.493f, 9.45f, 2.026f, 8.768f, 2.726f, 8.296f)
            curveTo(3.178f, 7.991f, 3.679f, 7.83f, 4.231f, 7.716f)
            curveTo(4.763f, 7.607f, 5.422f, 7.526f, 6.221f, 7.428f)
            lineTo(9.678f, 7.003f)
            curveTo(10.477f, 6.905f, 11.136f, 6.824f, 11.679f, 6.802f)
            close()
        }
    }.build()

    val Check = ImageVector.Builder(
        name = "check",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF1C1C1E)),
            strokeLineWidth = 2.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(20f, 6f)
            lineTo(9f, 17f)
            lineTo(4f, 12f)
        }
    }.build()

    val ChevronRight = ImageVector.Builder(
        name = "chevron_right",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF8E8E93)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9f, 18f)
            lineTo(15f, 12f)
            lineTo(9f, 6f)
        }
    }.build()

    val User = ImageVector.Builder(
        name = "user",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF666666)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(20f, 21f)
            verticalLineToRelative(-2f)
            arcTo(4f, 4f, 0f, false, false, 16f, 15f)
            horizontalLineTo(8f)
            arcTo(4f, 4f, 0f, false, false, 4f, 17f)
            verticalLineTo(21f)
            moveTo(12f, 7f)
            arcTo(4f, 4f, 0f, true, true, 0f, 0.01f)
            close()
        }
    }.build()

    val Info = ImageVector.Builder(
        name = "info",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF141413)),
            stroke = null,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(440f, 680f)
            horizontalLineTo(520f)
            verticalLineTo(440f)
            horizontalLineTo(440f)
            verticalLineTo(680f)
            close()
            moveTo(480f, 360f)
            quadTo(497f, 360f, 508.5f, 348.5f)
            quadTo(520f, 337f, 520f, 320f)
            quadTo(520f, 303f, 508.5f, 291.5f)
            quadTo(497f, 280f, 480f, 280f)
            quadTo(463f, 280f, 451.5f, 291.5f)
            quadTo(440f, 303f, 440f, 320f)
            quadTo(440f, 337f, 451.5f, 348.5f)
            quadTo(463f, 360f, 480f, 360f)
            close()
            moveTo(480f, 880f)
            quadTo(397f, 880f, 324f, 848.5f)
            quadTo(251f, 817f, 197f, 763f)
            quadTo(143f, 709f, 111.5f, 636f)
            quadTo(80f, 563f, 80f, 480f)
            quadTo(80f, 397f, 111.5f, 324f)
            quadTo(143f, 251f, 197f, 197f)
            quadTo(251f, 143f, 324f, 111.5f)
            quadTo(397f, 80f, 480f, 80f)
            quadTo(563f, 80f, 636f, 111.5f)
            quadTo(709f, 143f, 763f, 197f)
            quadTo(817f, 251f, 848.5f, 324f)
            quadTo(880f, 397f, 880f, 480f)
            quadTo(880f, 563f, 848.5f, 636f)
            quadTo(817f, 709f, 763f, 763f)
            quadTo(709f, 817f, 636f, 848.5f)
            quadTo(563f, 880f, 480f, 880f)
            close()
            moveTo(480f, 800f)
            quadTo(614f, 800f, 707f, 707f)
            quadTo(800f, 614f, 800f, 480f)
            quadTo(800f, 346f, 707f, 253f)
            quadTo(614f, 160f, 480f, 160f)
            quadTo(346f, 160f, 253f, 253f)
            quadTo(160f, 346f, 160f, 480f)
            quadTo(160f, 614f, 253f, 707f)
            quadTo(346f, 800f, 480f, 800f)
            close()
        }
    }.build()

    val MCPTools = ImageVector.Builder(
        name = "mcp_tools",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C1C1E)),
            stroke = null,
            pathFillType = PathFillType.EvenOdd
        ) {
            addPathNodes(
                "M15.688 2.343a2.588 2.588 0 00-3.61 0l-9.626 9.44a.863.863 0 01-1.203 0 .823.823 0 010-1.18l9.626-9.44a4.313 4.313 0 016.016 0 4.116 4.116 0 011.204 3.54 4.3 4.3 0 013.609 1.18l.05.05a4.115 4.115 0 010 5.9l-8.706 8.537a.274.274 0 000 .393l1.788 1.754a.823.823 0 010 1.18.863.863 0 01-1.203 0l-1.788-1.753a1.92 1.92 0 010-2.754l8.706-8.538a2.47 2.47 0 000-3.54l-.05-.049a2.588 2.588 0 00-3.607-.003l-7.172 7.034-.002.002-.098.097a.863.863 0 01-1.204 0 .823.823 0 010-1.18l7.273-7.133a2.47 2.47 0 00-.003-3.537z"
            )
        }
        path(
            fill = SolidColor(Color(0xFF1C1C1E)),
            stroke = null,
            pathFillType = PathFillType.EvenOdd
        ) {
            addPathNodes(
                "M14.485 4.703a.823.823 0 000-1.18.863.863 0 00-1.204 0l-7.119 6.982a4.115 4.115 0 000 5.9 4.314 4.314 0 006.016 0l7.12-6.982a.823.823 0 000-1.18.863.863 0 00-1.204 0l-7.119 6.982a2.588 2.588 0 01-3.61 0 2.47 2.47 0 010-3.54l7.12-6.982z"
            )
        }
    }.build()

    val Volume = ImageVector.Builder(
        name = "volume",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF374151)),
            stroke = null
        ) {
            moveTo(11f, 4.91f)
            curveTo(11f, 4.475f, 10.483f, 4.247f, 10.162f, 4.541f)
            lineTo(7.501f, 6.98f)
            curveTo(6.786f, 7.636f, 5.85f, 8f, 4.88f, 8f)
            curveTo(3.842f, 8f, 3f, 8.842f, 3f, 9.88f)
            verticalLineTo(14.12f)
            curveTo(3f, 15.158f, 3.842f, 16f, 4.88f, 16f)
            curveTo(5.85f, 16f, 6.786f, 16.364f, 7.501f, 17.02f)
            lineTo(10.162f, 19.459f)
            curveTo(10.483f, 19.753f, 11f, 19.525f, 11f, 19.09f)
            verticalLineTo(4.91f)
            close()
            moveTo(8.811f, 3.067f)
            curveTo(10.414f, 1.597f, 13f, 2.735f, 13f, 4.91f)
            verticalLineTo(19.09f)
            curveTo(13f, 21.265f, 10.414f, 22.403f, 8.811f, 20.933f)
            lineTo(6.15f, 18.494f)
            curveTo(5.803f, 18.176f, 5.35f, 18f, 4.88f, 18f)
            curveTo(2.737f, 18f, 1f, 16.263f, 1f, 14.12f)
            verticalLineTo(9.88f)
            curveTo(1f, 7.737f, 2.737f, 6f, 4.88f, 6f)
            curveTo(5.35f, 6f, 5.803f, 5.824f, 6.15f, 5.506f)
            lineTo(8.811f, 3.067f)
            close()
            moveTo(20.317f, 6.357f)
            curveTo(20.802f, 6.093f, 21.409f, 6.273f, 21.673f, 6.758f)
            curveTo(22.52f, 8.318f, 23f, 10.104f, 23f, 12f)
            curveTo(23f, 13.851f, 22.542f, 15.597f, 21.733f, 17.13f)
            curveTo(21.475f, 17.618f, 20.87f, 17.805f, 20.382f, 17.547f)
            curveTo(19.893f, 17.289f, 19.706f, 16.684f, 19.964f, 16.196f)
            curveTo(20.625f, 14.944f, 21f, 13.517f, 21f, 12f)
            curveTo(21f, 10.446f, 20.607f, 8.986f, 19.915f, 7.713f)
            curveTo(19.652f, 7.227f, 19.832f, 6.62f, 20.317f, 6.357f)
            close()
            moveTo(15.8f, 7.9f)
            curveTo(16.241f, 7.569f, 16.868f, 7.658f, 17.2f, 8.099f)
            curveTo(18.016f, 9.186f, 18.5f, 10.538f, 18.5f, 12f)
            curveTo(18.5f, 13.313f, 18.11f, 14.537f, 17.439f, 15.56f)
            curveTo(17.136f, 16.022f, 16.516f, 16.151f, 16.054f, 15.848f)
            curveTo(15.592f, 15.545f, 15.464f, 14.925f, 15.766f, 14.464f)
            curveTo(16.23f, 13.756f, 16.5f, 12.911f, 16.5f, 12f)
            curveTo(16.5f, 10.986f, 16.166f, 10.052f, 15.601f, 9.301f)
            curveTo(15.269f, 8.859f, 15.358f, 8.232f, 15.8f, 7.9f)
            close()
        }
    }.build()

    val Sun = ImageVector.Builder(
        name = "sun",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C1C1E)),
            stroke = null,
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(12f, 1f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, 1f)
            verticalLineToRelative(2f)
            arcToRelative(1f, 1f, 0f, true, true, -2f, 0f)
            verticalLineTo(2f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, -1f)
            close()
            moveTo(4.222f, 4.222f)
            arcToRelative(1f, 1f, 0f, false, true, 1.414f, 0f)
            lineTo(7.05f, 5.636f)
            arcToRelative(1f, 1f, 0f, true, true, -1.414f, 1.414f)
            lineTo(4.222f, 5.636f)
            arcToRelative(1f, 1f, 0f, false, true, 0f, -1.414f)
            close()
            moveTo(19.778f, 4.222f)
            arcToRelative(1f, 1f, 0f, false, true, 0f, 1.414f)
            lineTo(18.364f, 7.05f)
            arcToRelative(1f, 1f, 0f, false, true, -1.414f, -1.414f)
            lineToRelative(1.414f, -1.414f)
            arcToRelative(1f, 1f, 0f, false, true, 1.414f, 0f)
            close()
            moveTo(12f, 9f)
            arcToRelative(3f, 3f, 0f, true, false, 0f, 6f)
            arcToRelative(3f, 3f, 0f, false, false, 0f, -6f)
            close()
            moveTo(7f, 12f)
            arcToRelative(5f, 5f, 0f, true, true, 10f, 0f)
            arcToRelative(5f, 5f, 0f, false, true, -10f, 0f)
            close()
            moveTo(1f, 12f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, -1f)
            horizontalLineToRelative(2f)
            arcToRelative(1f, 1f, 0f, true, true, 0f, 2f)
            horizontalLineTo(2f)
            arcToRelative(1f, 1f, 0f, false, true, -1f, -1f)
            close()
            moveTo(19f, 12f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, -1f)
            horizontalLineToRelative(2f)
            arcToRelative(1f, 1f, 0f, true, true, 0f, 2f)
            horizontalLineToRelative(-2f)
            arcToRelative(1f, 1f, 0f, false, true, -1f, -1f)
            close()
            moveTo(7.05f, 16.95f)
            arcToRelative(1f, 1f, 0f, false, true, 0f, 1.414f)
            lineToRelative(-1.414f, 1.414f)
            arcToRelative(1f, 1f, 0f, false, true, -1.414f, -1.414f)
            lineToRelative(1.414f, -1.414f)
            arcToRelative(1f, 1f, 0f, false, true, 1.414f, 0f)
            close()
            moveTo(16.95f, 16.95f)
            arcToRelative(1f, 1f, 0f, false, true, 1.414f, 0f)
            lineToRelative(1.414f, 1.414f)
            arcToRelative(1f, 1f, 0f, false, true, -1.414f, 1.414f)
            lineToRelative(-1.414f, -1.414f)
            arcToRelative(1f, 1f, 0f, false, true, 0f, -1.414f)
            close()
            moveTo(12f, 19f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, 1f)
            verticalLineToRelative(2f)
            arcToRelative(1f, 1f, 0f, true, true, -2f, 0f)
            verticalLineToRelative(-2f)
            arcToRelative(1f, 1f, 0f, false, true, 1f, -1f)
            close()
        }
    }.build()
}
