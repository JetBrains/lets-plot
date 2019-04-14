package jetbrains.datalore.visualization.base.canvas

//import com.google.gwt.regexp.shared.MatchResult
//import com.google.gwt.regexp.shared.RegExp

object CssStyleUtil {
    // ToDo!!!
//    private val FONT_ATTRIBUTE = RegExp.compile("font:(.+);")
    private val FONT = 1

    fun extractStyleFont(style: String): String? {
// ToDo!!!
//        val matchResult = FONT_ATTRIBUTE.exec(style)
//        return if (matchResult == null) null else matchResult!!.getGroup(FONT).trim()
        return null
    }

    internal fun scaleFont(font: String, scale: Double): String {
// ToDo!!!
//        val parser = CssFontParser.create(font) ?: return font
//
//        val beforeScaling = parser.getSizeString() ?: return font
//
//        var afterScaling = scaleFontValue(parser.getFontSize(), scale)
//        val scaledHeight = scaleFontValue(parser.getLineHeight(), scale)
//
//        if (!scaledHeight.isEmpty()) {
//            afterScaling = "$afterScaling/$scaledHeight"
//        }
//
//        return font.replaceFirst(beforeScaling.toRegex(), afterScaling)
        return font
    }

    private fun scaleFontValue(value: Double?, scale: Double): String {
        return if (value == null) "" else (value * scale).toString() + "px"
    }
}
