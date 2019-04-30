package jetbrains.datalore.visualization.plot.gog.plot.theme

interface Theme {
    fun axisX(): AxisTheme

    fun axisY(): AxisTheme

    fun legend(): LegendTheme
}
