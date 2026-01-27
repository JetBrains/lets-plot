package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight

class FontManager(
    private val fontResolver: ((Font) -> java.awt.Font?) = { null }
) {
    private val fonts: MutableMap<Triple<String, FontWeight, FontStyle>, AwtFont> = mutableMapOf()

    fun register(family: String, awtFont: AwtFont, weight: FontWeight = FontWeight.NORMAL, style: FontStyle = FontStyle.NORMAL) {
        fonts[Triple(family, weight, style)] = awtFont
    }

    fun getFont(f: Font): java.awt.Font? {
        val fontBase = fontResolver(f)
            ?: fonts[Triple(f.fontFamily, f.fontWeight, f.fontStyle)]
            ?: return null

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
