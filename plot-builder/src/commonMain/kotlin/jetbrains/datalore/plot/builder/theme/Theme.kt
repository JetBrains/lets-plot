package jetbrains.datalore.plot.builder.theme

interface Theme {
    fun axisX(): AxisTheme

    fun axisY(): AxisTheme

    fun legend(): LegendTheme
}
