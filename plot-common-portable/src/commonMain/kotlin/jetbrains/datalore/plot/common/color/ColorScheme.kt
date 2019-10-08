package jetbrains.datalore.plot.common.color

interface ColorScheme {
    val type: ColorPalette.Type

    val maxColors: Int

    val colorSet: Array<Array<String>>

    fun getColors(count: Int): Array<String>
}
