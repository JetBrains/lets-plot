package jetbrains.datalore.visualization.plot.gog.config.theme

import jetbrains.datalore.visualization.plot.gog.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.visualization.plot.gog.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.visualization.plot.gog.config.Option.Theme.LEGEND_POSITION
import jetbrains.datalore.visualization.plot.gog.plot.theme.AxisTheme
import jetbrains.datalore.visualization.plot.gog.plot.theme.DefaultTheme
import jetbrains.datalore.visualization.plot.gog.plot.theme.LegendTheme
import jetbrains.datalore.visualization.plot.gog.plot.theme.Theme

class ThemeConfig(options: Map<*, *>) {


    val theme: Theme

    init {
        theme = MyTheme(options, DEF_OPTIONS)
    }

    private class MyTheme internal constructor(options: Map<*, *>, defOptions: Map<*, *>) : Theme {
        private val myAxisXTheme: AxisTheme
        private val myAxisYTheme: AxisTheme
        private val myLegendTheme: LegendTheme

        init {
            myAxisXTheme = AxisThemeConfig.X(options, defOptions)
            myAxisYTheme = AxisThemeConfig.Y(options, defOptions)
            myLegendTheme = LegendThemeConfig(options, defOptions)
        }

        override fun axisX(): AxisTheme {
            return myAxisXTheme
        }

        override fun axisY(): AxisTheme {
            return myAxisYTheme
        }

        override fun legend(): LegendTheme {
            return myLegendTheme
        }
    }

    companion object {
        internal val DEF: Theme = DefaultTheme()
        private val DEF_OPTIONS = mapOf(
                LEGEND_POSITION to DEF.legend().position(),
                LEGEND_JUSTIFICATION to DEF.legend().justification(),
                LEGEND_DIRECTION to DEF.legend().direction()
        )
    }
}
