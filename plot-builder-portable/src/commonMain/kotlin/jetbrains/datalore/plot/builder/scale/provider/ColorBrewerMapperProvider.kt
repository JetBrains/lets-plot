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

/**
 * @param paletteTypeName - One of seq (sequential), div (diverging) or qual (qualitative)
 * @param paletteNameOrIndex - If a string, will use that named palette.
 * If a number, will index into the list of palettes of appropriate type
 * @param direction - Sets the order of colors in the scale. If 1, the default, colors are as output by brewer.pal.
 * If -1, the order of colors is reversed
 * @param naValue
 */
class ColorBrewerMapperProvider(
    private val paletteTypeName: String?,
    private val paletteNameOrIndex: Any?,
    private val direction: Double?,
    naValue: Color
) : MapperProviderBase<Color>(naValue),
    WithGuideBreaks {

    override val guideBreaks: List<GuideBreak<*>>
        get() = emptyList()

    init {
        require(paletteNameOrIndex?.let {
            paletteNameOrIndex is String || paletteNameOrIndex is Number
        } ?: true) {
            "palette: expected a name or index but was: ${paletteNameOrIndex!!::class.simpleName}"
        }

        if (paletteNameOrIndex is Number) {
            require(paletteTypeName != null) { "brewer palette type required: 'seq', 'div' or 'qual'." }
        }
    }

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Color> {

        val colors = getColors(data, variable)
        return GuideMappers.discreteToDiscrete(data, variable, colors, naValue)
    }

    override fun createContinuousMapper(
        data: DataFrame,
        variable: DataFrame.Variable,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: Transform?
    ): GuideMapper<Color> {
        val colors = getColors(data, variable)
        return GuideMappers.continuousToDiscrete(
            MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans),
            colors,
            naValue
        )
    }

    private fun getColors(data: DataFrame, variable: DataFrame.Variable): List<Color> {
        val colorScheme = colorScheme(data, variable)
        val colors: List<Color> = when {
            data.isNumeric(variable) -> PaletteUtil.schemeColors(colorScheme, colorScheme.maxColors)
            else -> {
                val size = DataFrameUtil.distinctValues(data, variable).size
                PaletteUtil.schemeColors(colorScheme, size)
            }
        }

        return when (direction?.let { direction < 0 } ?: false) {
            true -> Lists.reverse(colors)
            false -> colors
        }
    }

    private fun colorScheme(data: DataFrame, variable: DataFrame.Variable): ColorScheme {
        val paletteType = when {
            paletteNameOrIndex is String -> {
                val palType = PaletteUtil.paletteTypeByPaletteName(paletteNameOrIndex)
                require(palType != null) { cantFindPaletteError(paletteNameOrIndex) }
                palType
            }
            paletteTypeName != null -> paletteType(paletteTypeName)
            else -> {
                // Default palette type
                when {
                    data.isNumeric(variable) -> ColorPalette.Type.SEQUENTIAL
                    else -> ColorPalette.Type.QUALITATIVE
                }
            }
        }

        return when {
            paletteNameOrIndex is Number -> colorSchemeByIndex(paletteType, paletteNameOrIndex.toInt())
            paletteNameOrIndex is String -> colorSchemeByName(paletteType, paletteNameOrIndex)
            else -> {
                when {
                    data.isNumeric(variable) -> colorSchemeByIndex(paletteType, 0)
                    else -> {
                        val size = DataFrameUtil.distinctValues(data, variable).size
                        when {
                            size <= 8 -> ColorPalette.Qualitative.Set2
                            else -> ColorPalette.Qualitative.Set3
                        }
                    }
                }
            }
        }
    }


    companion object {
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

        private fun colorSchemeByName(paletteType: ColorPalette.Type, paletteName: String): ColorScheme {
            try {
                return when (paletteType) {
                    ColorPalette.Type.SEQUENTIAL -> ColorPalette.Sequential.valueOf(paletteName)
                    ColorPalette.Type.DIVERGING -> ColorPalette.Diverging.valueOf(paletteName)
                    ColorPalette.Type.QUALITATIVE -> ColorPalette.Qualitative.valueOf(paletteName)
                }
            } catch (ignore: IllegalArgumentException) {
                // Enum type has no constant with the specified name error.
                // Replace generic error massage with specific one
                throw IllegalArgumentException(cantFindPaletteError(paletteName))
            }
        }

        private fun colorSchemeByIndex(paletteType: ColorPalette.Type, index: Int): ColorScheme {
            @Suppress("UNCHECKED_CAST")
            val values: Array<ColorScheme> = when (paletteType) {
                ColorPalette.Type.SEQUENTIAL -> ColorPalette.Sequential.values() as Array<ColorScheme>
                ColorPalette.Type.DIVERGING -> ColorPalette.Diverging.values() as Array<ColorScheme>
                ColorPalette.Type.QUALITATIVE -> ColorPalette.Qualitative.values() as Array<ColorScheme>
            }

            return values[index % values.size]
        }

        private fun cantFindPaletteError(paletteName: String): String {
            return """
                |Brewer palette '$paletteName' was not found. 
                |Valid palette names are: 
                |   Type 'seq' (sequential): 
                |       ${names(ColorPalette.Sequential.values())}       
                |   Type 'div' (diverging): 
                |       ${names(ColorPalette.Diverging.values())}       
                |   Type 'qual' (qualitative): 
                |       ${names(ColorPalette.Qualitative.values())}       
            """.trimMargin()
        }

        private fun <T : Enum<T>> names(enums: Array<T>): String {
            return enums.joinToString(", ") { "'${it.name}'" }
        }
    }
}
