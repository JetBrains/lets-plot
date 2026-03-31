package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.Font.FontVariant

class FontManager(
    private val fontResolver: ((Font) -> java.awt.Font?) = { null }
) {
    private val fonts: MutableMap<Pair<String, FontVariant>, AwtFont> = mutableMapOf()

    fun getFont(f: Font): java.awt.Font? {
        val fontBase = fontResolver(f)
            ?: fonts[f.fontFamily to f.variant]
            ?: return null

        val style = when(f.variant) {
            FontVariant.BOLD_ITALIC -> java.awt.Font.BOLD or java.awt.Font.ITALIC
            FontVariant.BOLD -> java.awt.Font.BOLD
            FontVariant.ITALIC -> java.awt.Font.ITALIC
            FontVariant.NORMAL -> java.awt.Font.PLAIN
        }
        val customFont = fontBase.deriveFont(style, f.fontSize.toFloat())
        return customFont
    }

    companion object {
        val DEFAULT = FontManager()
    }
}
