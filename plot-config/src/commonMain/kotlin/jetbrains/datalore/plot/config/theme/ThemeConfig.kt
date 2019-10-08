package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION

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
