package jetbrains.datalore.visualization.plot.common.color

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.gcommon.collect.Iterables.concat
import jetbrains.datalore.base.values.Color

object PaletteUtil {
    val NULL_COLOR = Color.LIGHT_GRAY

    private val EXTENSIBLE_COLOR_SCHEMES: Set<ColorScheme> = setOf(
            ColorPalette.Qualitative.Accent,
            ColorPalette.Qualitative.Dark2,
            ColorPalette.Qualitative.Pastel1,
            ColorPalette.Qualitative.Pastel2,
            ColorPalette.Qualitative.Set1,
            ColorPalette.Qualitative.Set2,
            ColorPalette.Qualitative.Set3
    )

    private fun isExtensibleScheme(colorScheme: ColorScheme): Boolean {
        return EXTENSIBLE_COLOR_SCHEMES.contains(colorScheme)
    }

    fun schemeColors(colorScheme: ColorScheme, colorCount: Int): List<Color> {
        val colorsHex = colorScheme.getColors(colorCount)
        val colors = fromColorsHex(colorsHex)
        if (colorsHex.size < colorCount && isExtensibleScheme(colorScheme)) {
            val addColors = ColorUtil.genColors(colorCount - colorsHex.size, colors)
            return Iterables.toList(concat(colors, addColors))
        }
        return colors
    }

//    fun quantizedColorScale(colorScheme: ColorScheme, colorCount: Int, minValue: Double, maxValue: Double): QuantizeScale<Color> {
//        val colors = schemeColors(colorScheme, colorCount)
//        return QuantizeScale<Color>()
//                .range(colors)
//                .domain(minValue, maxValue)
//    }

    private fun fromColorsHex(hexColors: Array<String>): List<Color> {
        val colors = ArrayList<Color>()
        for (hexColor in hexColors) {
            try {
                colors.add(Color.parseHex(hexColor))
            } catch (e: Exception) {
                // ignore this value
            }

        }
        return colors
    }
}
