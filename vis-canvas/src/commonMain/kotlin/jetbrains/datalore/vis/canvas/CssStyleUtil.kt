/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas


object CssStyleUtil {
    private val FONT_ATTRIBUTE = Regex("font:(.+);")
    private const val FONT = 1

    fun String.extractFontStyle(): Context2d.Font.FontStyle {
        val italicRegex ="italic".toRegex(RegexOption.IGNORE_CASE)

        return if (italicRegex.containsMatchIn(this))
            Context2d.Font.FontStyle.ITALIC
        else
            Context2d.Font.FontStyle.NORMAL
    }

    fun String.extractFontWeight(): Context2d.Font.FontWeight {
        val boldRegex = "600|700|800|900|bold".toRegex(RegexOption.IGNORE_CASE)

        return if (boldRegex.containsMatchIn(this))
            Context2d.Font.FontWeight.BOLD
        else
            Context2d.Font.FontWeight.NORMAL
    }

    fun extractStyleFont(style: String?): String? {
        if (style == null) {
            return null
        }
        val matchResult = FONT_ATTRIBUTE.find(style)
        return matchResult?.groupValues?.get(FONT)?.trim()
    }

    internal fun scaleFont(font: String, scale: Double): String {
        val parser = CssFontParser.create(font) ?: return font
        val beforeScaling = parser.sizeString ?: return font

        var afterScaling = scaleFontValue(parser.fontSize, scale)
        val value = parser.lineHeight
        val scaledHeight = scaleFontValue(value, scale)

        if (scaledHeight.isNotEmpty()) {
            afterScaling = "$afterScaling/$scaledHeight"
        }

        return font.replaceFirst(beforeScaling.toRegex(), afterScaling)
    }

    private fun scaleFontValue(value: Double?, scale: Double): String {
        return if (value == null) "" else (value * scale).toString() + "px"
    }
}
