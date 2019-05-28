package jetbrains.datalore.visualization.plot.config.aes

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.values.Color

internal class ColorOptionConverter : Function<Any?, Color?> {
    override fun apply(value: Any?): Color? {
        if (value == null) {
            return null
        }
        if (value is Color) {
            return value
        }
        if (value is Number) {
            return TypedContinuousIdentityMappers.COLOR(value.toDouble())
        }

        val s = value.toString()
        try {
            if (s.indexOf('(') > 0) {
                // rgb(...
                return Color.parseColor(s)
            } else if (s.startsWith("#")) {
                return Color.parseHex(s)
            } else if (COLOR_BY_NAME.containsKey(s)) {
                return COLOR_BY_NAME[s]
            }
        } catch (ignored: RuntimeException) {
            // pass
        }

        throw IllegalArgumentException("Can't convert to color: '" + value + "' (" + value::class.simpleName + ")")
    }

    companion object {
        private val COLOR_BY_NAME = HashMap<String, Color>()

        init {
            COLOR_BY_NAME["white"] = Color.WHITE
            COLOR_BY_NAME["black"] = Color.BLACK
            COLOR_BY_NAME["light_gray"] = Color.LIGHT_GRAY
            COLOR_BY_NAME["very_light_gray"] = Color.VERY_LIGHT_GRAY
            COLOR_BY_NAME["gray"] = Color.GRAY
            COLOR_BY_NAME["red"] = Color.RED
            COLOR_BY_NAME["light_green"] = Color.LIGHT_GREEN
            COLOR_BY_NAME["green"] = Color.GREEN
            COLOR_BY_NAME["dark_green"] = Color.DARK_GREEN
            COLOR_BY_NAME["blue"] = Color.BLUE
            COLOR_BY_NAME["dark_blue"] = Color.DARK_BLUE
            COLOR_BY_NAME["light_blue"] = Color.LIGHT_BLUE
            COLOR_BY_NAME["yellow"] = Color.YELLOW
            COLOR_BY_NAME["light_yellow"] = Color.LIGHT_YELLOW
            COLOR_BY_NAME["very_light_yellow"] = Color.VERY_LIGHT_YELLOW
            COLOR_BY_NAME["magenta"] = Color.MAGENTA
            COLOR_BY_NAME["light_magenta"] = Color.LIGHT_MAGENTA
            COLOR_BY_NAME["dark_magenta"] = Color.DARK_MAGENTA
            COLOR_BY_NAME["cyan"] = Color.CYAN
            COLOR_BY_NAME["light_cyan"] = Color.LIGHT_CYAN
            COLOR_BY_NAME["orange"] = Color.ORANGE
            COLOR_BY_NAME["pink"] = Color.PINK
            COLOR_BY_NAME["light_pink"] = Color.LIGHT_PINK
        }
    }
}
