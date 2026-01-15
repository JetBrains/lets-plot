package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.core.canvas.Font

class FontManager(
    private val fonts: Map<Font, AwtFont> = emptyMap()
) {
    fun isFontRegistered(f: Font): Boolean {
        return fonts.containsKey(f.copy(fontSize = 1.0))
    }

    fun getFont(f: Font): java.awt.Font? {
        val fontBase = fonts[f.copy(fontSize = 1.0)] ?: return null
        val style = when {
            f.isBoldItalic -> java.awt.Font.BOLD or java.awt.Font.ITALIC
            f.isBold -> java.awt.Font.BOLD
            f.isItalic -> java.awt.Font.ITALIC
            else -> java.awt.Font.PLAIN
        }
        val customFont = fontBase.deriveFont(style, f.fontSize.toFloat())
        return customFont
    }

    companion object {
        val EMPTY = FontManager()
    }
}
