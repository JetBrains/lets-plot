/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideBreak
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.WithGuideBreaks
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.color.ColorPalette
import jetbrains.datalore.plot.common.color.ColorScheme
import jetbrains.datalore.plot.common.color.PaletteUtil

class ColorBrewerMapperProvider
/**
 * @param type      - One of seq (sequential), div (diverging) or qual (qualitative)
 * @param palette   - If a string, will use that named palette.
 * If a number, will index into the list of palettes of appropriate type
 * @param direction - Sets the order of colors in the scale. If 1, the default, colors are as output by brewer.pal.
 * If -1, the order of colors is reversed
 * @param naValue
 */
constructor(type: String?, palette: Any?, direction: Double?, naValue: Color) : MapperProviderBase<Color>(naValue),
    WithGuideBreaks {

    private val myColorScheme: ColorScheme
    private val myReversedColors: Boolean

    override val guideBreaks: List<GuideBreak<*>>
        get() = emptyList()

    // http://docs.ggplot2.org/current/scale_brewer.html
    private fun paletteType(name: String?): ColorPalette.Type {
        if (name == null) {
            return ColorPalette.Type.SEQUENTIAL
        }
        return when (name) {
            "seq" -> ColorPalette.Type.SEQUENTIAL
            "div" -> ColorPalette.Type.DIVERGING
            "qual" -> ColorPalette.Type.QUALITATIVE
            else -> throw IllegalArgumentException(
                    "Palette type expected one of 'seq' (sequential), 'div' (diverging) or 'qual' (qualitative) but was: '$name'"
            )
        }
    }

    private fun colorSchemeByName(paletteType: ColorPalette.Type, name: String): ColorScheme {
        try {
            return when (paletteType) {
                ColorPalette.Type.SEQUENTIAL -> ColorPalette.Sequential.valueOf(name)
                ColorPalette.Type.DIVERGING -> ColorPalette.Diverging.valueOf(name)
                ColorPalette.Type.QUALITATIVE -> ColorPalette.Qualitative.valueOf(name)
            }// unexpected palette type
        } catch (ignore: IllegalArgumentException) {
            // Enum type has no constant with the specified name error.
            // Replace generic error massage with specific one
            var names = "<unavailable>"
            names = when (paletteType) {
                ColorPalette.Type.SEQUENTIAL -> names(ColorPalette.Sequential.values())
                ColorPalette.Type.DIVERGING -> names(ColorPalette.Diverging.values())
                ColorPalette.Type.QUALITATIVE -> names(ColorPalette.Qualitative.values())
            }

            throw IllegalArgumentException("Palette name expected in: $names but was: '$name'")
        }
    }

    private fun colorSchemeByIndex(paletteType: ColorPalette.Type, index: Int): ColorScheme {
        val values: Array<ColorScheme> = when (paletteType) {
            ColorPalette.Type.SEQUENTIAL -> ColorPalette.Sequential.values() as Array<ColorScheme>
            ColorPalette.Type.DIVERGING -> ColorPalette.Diverging.values() as Array<ColorScheme>
            ColorPalette.Type.QUALITATIVE -> ColorPalette.Qualitative.values() as Array<ColorScheme>
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
        val colors: List<Color> = when {
            data.isNumeric(`var`) -> PaletteUtil.schemeColors(myColorScheme, myColorScheme.maxColors)
            else -> {
                val size = DataFrameUtil.distinctValues(data, `var`).size
                PaletteUtil.schemeColors(myColorScheme, size)
            }
        }

        return if (myReversedColors) {
            Lists.reverse(colors)
        } else colors
    }
}
