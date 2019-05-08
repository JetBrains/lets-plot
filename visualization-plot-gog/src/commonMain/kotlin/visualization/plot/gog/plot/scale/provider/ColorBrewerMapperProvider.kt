package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.common.color.ColorPalette
import jetbrains.datalore.visualization.plot.gog.common.color.ColorScheme
import jetbrains.datalore.visualization.plot.gog.common.color.PaletteUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.MapperUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideBreak
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.WithGuideBreaks
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers

internal class ColorBrewerMapperProvider
/**
 * @param type      - One of seq (sequential), div (diverging) or qual (qualitative)
 * @param palette   - If a string, will use that named palette.
 * If a number, will index into the list of palettes of appropriate type
 * @param direction - Sets the order of colors in the scale. If 1, the default, colors are as output by brewer.pal.
 * If -1, the order of colors is reversed
 * @param naValue
 */
(type: String?, palette: Any?, direction: Double?, naValue: Color) : MapperProviderBase<Color>(naValue), WithGuideBreaks {

    private val myColorScheme: ColorScheme
    private val myReversedColors: Boolean

    override val guideBreaks: List<GuideBreak<*>>
        get() = emptyList()

    // http://docs.ggplot2.org/current/scale_brewer.html
    private fun paletteType(name: String?): ColorPalette.Type {
        if (name == null) {
            return ColorPalette.Type.SEQUENTIAL
        }
        when (name) {
            "seq" -> return ColorPalette.Type.SEQUENTIAL
            "div" -> return ColorPalette.Type.DIVERGING
            "qual" -> return ColorPalette.Type.QUALITATIVE
            else -> throw IllegalArgumentException(
                    "Palette type expected one of 'seq' (sequential), 'div' (diverging) or 'qual' (qualitative) but was: '$name'"
            )
        }
    }

    private fun colorSchemeByName(paletteType: ColorPalette.Type, name: String): ColorScheme {
        try {
            when (paletteType) {
                ColorPalette.Type.SEQUENTIAL -> return ColorPalette.Sequential.valueOf(name)
                ColorPalette.Type.DIVERGING -> return ColorPalette.Diverging.valueOf(name)
                ColorPalette.Type.QUALITATIVE -> return ColorPalette.Qualitative.valueOf(name)
            }// unexpected palette type
        } catch (ignore: IllegalArgumentException) {
            // Enum type has no constant with the specified name error.
            // Replace generic error massage with specific one
            var names = "<unavailable>"
            when (paletteType) {
                ColorPalette.Type.SEQUENTIAL -> names = names(ColorPalette.Sequential.values())
                ColorPalette.Type.DIVERGING -> names = names(ColorPalette.Diverging.values())
                ColorPalette.Type.QUALITATIVE -> names = names(ColorPalette.Qualitative.values())
            }

            throw IllegalArgumentException("Palette name expected in: $names but was: '$name'")
        }

        throw IllegalArgumentException("Unexpected palette type: $paletteType")
    }

    private fun colorSchemeByIndex(paletteType: ColorPalette.Type, index: Int): ColorScheme {
        val values: Array<ColorScheme>
        when (paletteType) {
            ColorPalette.Type.SEQUENTIAL -> values = ColorPalette.Sequential.values() as Array<ColorScheme>
            ColorPalette.Type.DIVERGING -> values = ColorPalette.Diverging.values() as Array<ColorScheme>
            ColorPalette.Type.QUALITATIVE -> values = ColorPalette.Qualitative.values() as Array<ColorScheme>
            else -> throw IllegalArgumentException("Unexpected palette type: $paletteType")
        }

        return values[index % values.size]
    }

    private fun <T : Enum<T>> names(values: Array<T>): String {
        val names = ArrayList<String>(values.size)
        for (value in values) {
            names.add("'" + value.name + "'")
        }
        return "[" + names.joinToString(", ") + "]"
    }

    init {
        val paletteType = paletteType(type)
        var colorScheme = colorSchemeByIndex(paletteType, 0)
        if (palette is Number) {
            colorScheme = colorSchemeByIndex(paletteType, palette.toInt())
        } else if (palette is String) {
            colorScheme = colorSchemeByName(paletteType, palette)
        }

        myColorScheme = colorScheme
        myReversedColors = direction != null && direction == -1.0
    }

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Color> {
        val colors = getColors(data, variable)
        return GuideMappers.discreteToDiscrete(data, variable, colors, naValue)
    }

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<Color> {
        val colors = getColors(data, variable)
        return GuideMappers.continuousToDiscrete(
                MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans), colors, naValue)
    }

    private fun getColors(data: DataFrame, `var`: DataFrame.Variable): List<Color> {
        val colors: List<Color>
        if (data.isNumeric(`var`)) {
            colors = PaletteUtil.schemeColors(myColorScheme, myColorScheme.maxColors)
        } else {
            val size = DataFrameUtil.distinctValues(data, `var`).size
            colors = PaletteUtil.schemeColors(myColorScheme, size)
        }

        return if (myReversedColors) {
            Lists.reverse(colors)
        } else colors
    }
}
