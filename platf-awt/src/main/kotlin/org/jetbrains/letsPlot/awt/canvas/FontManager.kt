package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight

class FontManager(
) {
    private val fonts: MutableMap<Font, AwtFont> = mutableMapOf()

    fun register(family: String, awtFont: AwtFont, weight: FontWeight = FontWeight.NORMAL, style: FontStyle = FontStyle.NORMAL) {
        val fontKey = Font(
            fontFamily = family,
            fontWeight = weight,
            fontStyle = style,
            fontSize = 1.0
        )
        fonts[fontKey] = awtFont
    }

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
