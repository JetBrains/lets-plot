package jetbrains.datalore.visualization.plot.common.color

interface ColorScheme {
    val type: ColorPalette.Type

    val maxColors: Int

    val colorSet: Array<Array<String>>

    fun getColors(count: Int): Array<String>
}
