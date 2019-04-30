package jetbrains.datalore.visualization.plot.gog.common.color

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.gcommon.collect.Iterables.concat
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.scale.breaks.QuantizeScale
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableSet

object PaletteUtil {
    val NULL_COLOR = Color.LIGHT_GRAY

    private val EXTENSIBLE_COLOR_SCHEMES: Set<ColorScheme>

    init {
        val set = HashSet<ColorScheme>()
        set.add(ColorPalette.Qualitative.Accent)
        set.add(ColorPalette.Qualitative.Dark2)
        set.add(ColorPalette.Qualitative.Pastel1)
        set.add(ColorPalette.Qualitative.Pastel2)
        set.add(ColorPalette.Qualitative.Set1)
        set.add(ColorPalette.Qualitative.Set2)
        set.add(ColorPalette.Qualitative.Set3)
        EXTENSIBLE_COLOR_SCHEMES = unmodifiableSet(set)
    }

    fun isExtensibleScheme(colorScheme: ColorScheme): Boolean {
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

    fun quantizedColorScale(colorScheme: ColorScheme, colorCount: Int, minValue: Double, maxValue: Double): QuantizeScale<Color> {
        val colors = schemeColors(colorScheme, colorCount)
        return QuantizeScale<Color>()
                .range(colors)
                .domain(minValue, maxValue)
    }


    fun fromColorsHex(hexColors: Array<String>): List<Color> {
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
