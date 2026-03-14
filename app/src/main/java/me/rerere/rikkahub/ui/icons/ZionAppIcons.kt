package me.rerere.rikkahub.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
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

    val Assistant = ImageVector.Builder(
        name = "assistant",
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
            moveTo(8f, 5f)
            lineTo(16f, 5f)
            arcTo(4f, 4f, 0f, false, true, 20f, 9f)
            lineTo(20f, 13f)
            arcTo(4f, 4f, 0f, false, true, 16f, 17f)
            lineTo(13.5f, 17f)
            lineTo(10.5f, 20f)
            lineTo(10.5f, 17f)
            lineTo(8f, 17f)
            arcTo(4f, 4f, 0f, false, true, 4f, 13f)
            lineTo(4f, 9f)
            arcTo(4f, 4f, 0f, false, true, 8f, 5f)
            moveTo(9.5f, 10f)
            lineTo(9.5f, 10f)
            moveTo(14.5f, 10f)
            lineTo(14.5f, 10f)
            moveTo(9f, 13.5f)
            curveTo(10f, 14.5f, 14f, 14.5f, 15f, 13.5f)
        }
    }.build()
}
