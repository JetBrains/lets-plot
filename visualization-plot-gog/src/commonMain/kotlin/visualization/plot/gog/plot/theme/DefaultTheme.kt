package jetbrains.datalore.visualization.plot.gog.plot.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendDirection
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendJustification
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendPosition


class DefaultTheme : Theme {

    override fun axisX(): AxisTheme {
        return AXIS_THEME
    }

    override fun axisY(): AxisTheme {
        return AXIS_THEME
    }

    override fun legend(): LegendTheme {
        return LEGEND_THEME
    }

    companion object {
        private val AXIS_THEME = DefaultAxisTheme()

        private val LEGEND_THEME: LegendTheme = object : LegendTheme {
            override fun keySize(): Double {
                return 23.0
            }

            override fun margin(): Double {
                return 5.0
            }

            override fun padding(): Double {
                return 5.0
            }

            override fun position(): LegendPosition {
                return LegendPosition.RIGHT
            }

            override fun justification(): LegendJustification {
                return LegendJustification.CENTER
            }

            override fun direction(): LegendDirection {
                return LegendDirection.AUTO
            }

            override fun backgroundFill(): Color {
                return Color.WHITE
            }
        }
    }
}
