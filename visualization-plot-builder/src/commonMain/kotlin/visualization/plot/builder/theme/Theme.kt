package jetbrains.datalore.visualization.plot.builder.theme

interface Theme {
    fun axisX(): AxisTheme

    fun axisY(): AxisTheme

    fun legend(): LegendTheme
}
