package jetbrains.datalore.vis.canvas


object CssStyleUtil {
    private val FONT_ATTRIBUTE = Regex("font:(.+);")
    private const val FONT = 1

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
