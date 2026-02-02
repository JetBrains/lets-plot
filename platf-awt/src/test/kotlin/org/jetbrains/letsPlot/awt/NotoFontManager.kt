package org.jetbrains.letsPlot.awt

import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.core.canvas.Font.FontVariant.*
import org.jetbrains.letsPlot.visualtesting.AwtCanvasTck
import org.jetbrains.letsPlot.visualtesting.AwtFont
import java.io.IOException

object NotoFontManager {
    val INSTANCE = FontManager(
        fontResolver = { font ->
            when (font.fontFamily) {
                "Noto Sans" -> notoSans[font.variant]
                "Noto Serif" -> notoSerif[font.variant]
                "Noto Sans Mono" -> notoMono[font.variant]
                "Noto Sans Regular" -> notoSans[NORMAL]
                "Noto Serif Regular" -> notoSerif[NORMAL]
                "Noto Sans Mono Regular" -> notoMono[NORMAL]

                else -> notoSans[font.variant] // default font family
            } ?: error("Font not found: $font")
        }
    )

    private val notoSans = mapOf(
        NORMAL to createFont("fonts/NotoSans-Regular.ttf"),
        BOLD to createFont("fonts/NotoSans-Bold.ttf"),
        ITALIC to createFont("fonts/NotoSans-Italic.ttf"),
        BOLD_ITALIC to createFont("fonts/NotoSans-BoldItalic.ttf")
    )

    private val notoSerif = mapOf(
        NORMAL to createFont("fonts/NotoSerif-Regular.ttf"),
        BOLD to createFont("fonts/NotoSerif-Bold.ttf"),
        ITALIC to createFont("fonts/NotoSerif-Italic.ttf"),
        BOLD_ITALIC to createFont("fonts/NotoSerif-BoldItalic.ttf")
    )

    private val notoMono = mapOf(
        NORMAL to createFont("fonts/NotoSansMono-Regular.ttf"),
        BOLD to createFont("fonts/NotoSansMono-Bold.ttf"),
        ITALIC to createFont("fonts/NotoSansMono-Regular.ttf"),
        BOLD_ITALIC to createFont("fonts/NotoSansMono-Bold.ttf")
    )

    private fun createFont(resourceName: String): AwtFont {
        val fontStream = AwtCanvasTck::class.java.getClassLoader().getResourceAsStream(resourceName)
            ?: error("Font resource not found: $resourceName")
        try {
            return AwtFont.createFont(AwtFont.TRUETYPE_FONT, fontStream)
                ?: error("Cannot create font from resource: $resourceName")
        } finally {
            try {
                fontStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
