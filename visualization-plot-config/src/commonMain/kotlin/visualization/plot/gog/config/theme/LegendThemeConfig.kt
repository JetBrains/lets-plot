package jetbrains.datalore.visualization.plot.gog.config.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.builder.guide.LegendDirection
import jetbrains.datalore.visualization.plot.builder.guide.LegendJustification
import jetbrains.datalore.visualization.plot.builder.guide.LegendPosition
import jetbrains.datalore.visualization.plot.builder.theme.LegendTheme
import jetbrains.datalore.visualization.plot.gog.config.ConfigUtil
import jetbrains.datalore.visualization.plot.gog.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.visualization.plot.gog.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.visualization.plot.gog.config.Option.Theme.LEGEND_POSITION
import jetbrains.datalore.visualization.plot.gog.config.OptionsAccessor

internal class LegendThemeConfig(options: Map<*, *>, defOptions: Map<*, *>) : OptionsAccessor(options, defOptions), LegendTheme {

    override fun keySize(): Double {
        return ThemeConfig.DEF.legend().keySize()
    }

    override fun margin(): Double {
        return ThemeConfig.DEF.legend().margin()
    }

    override fun padding(): Double {
        return ThemeConfig.DEF.legend().padding()
    }

    override fun position(): LegendPosition {
        val o = get(LEGEND_POSITION)
        if (o is String) {
            when (o) {
                "right" -> return LegendPosition.RIGHT
                "left" -> return LegendPosition.LEFT
                "top" -> return LegendPosition.TOP
                "bottom" -> return LegendPosition.BOTTOM
                "none" -> return LegendPosition.NONE
                else -> throw IllegalArgumentException("Illegal value '" +
                        o +
                        "', " + LEGEND_POSITION + " expected values are: left/right/top/bottom/none or or two-element numeric list")
            }
        } else if (o is List<*>) {
            val v = ConfigUtil.toNumericPair((o as List<*>?)!!)
            return LegendPosition(v.x, v.y)
        } else if (o is LegendPosition) {
            return o
        }
        return ThemeConfig.DEF.legend().position()
    }

    override fun justification(): LegendJustification {
        // "center" or two-element numeric vector
        val o = get(LEGEND_JUSTIFICATION)
        if (o is String) {
            when (o) {
                "center" -> return LegendJustification.CENTER
                else -> throw IllegalArgumentException("Illegal value '" +
                        o +
                        "', " + LEGEND_JUSTIFICATION + " expected values are: 'center' or two-element numeric list")
            }
        } else if (o is List<*>) {
            val v = ConfigUtil.toNumericPair((o as List<*>?)!!)
            return LegendJustification(v.x, v.y)
        } else if (o is LegendJustification) {
            return o
        }
        return ThemeConfig.DEF.legend().justification()
    }

    override fun direction(): LegendDirection {
        // "horizontal" or "vertical"
        val o = get(LEGEND_DIRECTION)
        if (o is String) {
            when (o) {
                "horizontal" -> return LegendDirection.HORIZONTAL
                "vertical" -> return LegendDirection.VERTICAL
            }
        }
        return LegendDirection.AUTO
    }

    override fun backgroundFill(): Color {
        return ThemeConfig.DEF.legend().backgroundFill()
    }
}
