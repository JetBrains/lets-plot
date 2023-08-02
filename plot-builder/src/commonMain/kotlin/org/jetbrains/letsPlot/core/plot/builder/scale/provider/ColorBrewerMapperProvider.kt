/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.color.ColorPalette
import org.jetbrains.letsPlot.core.commons.color.ColorPalette.Type.*
import org.jetbrains.letsPlot.core.commons.color.ColorScheme
import org.jetbrains.letsPlot.core.commons.color.PaletteUtil
import org.jetbrains.letsPlot.core.commons.color.PaletteUtil.colorSchemeByIndex
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.GuideMappers


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
) : MapperProviderBase<Color>(naValue) {

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

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        val n = discreteTransform.effectiveDomain.size
        val colorScheme = colorScheme(true, n)
        val colors = colors(colorScheme, n)
        return GuideMappers.discreteToDiscrete(discreteTransform, colors, naValue)
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        val colorScheme = colorScheme(false)
        val colors = colors(colorScheme, colorScheme.maxColors)

        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        return GuideMappers.continuousToDiscrete(domain, colors, naValue)
    }

    private fun colors(colorScheme: ColorScheme, count: Int): List<Color> {
        val colors: List<Color> = PaletteUtil.schemeColors(colorScheme, count)
        return when (direction?.let { direction < 0 } ?: false) {
            true -> colors.reversed()
            false -> colors
        }
    }

    private fun colorScheme(discrete: Boolean, colorCount: Int? = null): ColorScheme {
        val paletteType = when {
            paletteNameOrIndex is String -> {
                val palType = PaletteUtil.paletteTypeByPaletteName(paletteNameOrIndex)
                require(palType != null) { cantFindPaletteError(paletteNameOrIndex) }
                palType
            }

            paletteTypeName != null -> paletteType(paletteTypeName)
            discrete -> QUALITATIVE
            else -> SEQUENTIAL
        }

        return when {
            paletteNameOrIndex is Number -> colorSchemeByIndex(paletteType, paletteNameOrIndex.toInt())
            paletteNameOrIndex is String -> colorSchemeByName(paletteType, paletteNameOrIndex)
            paletteType == QUALITATIVE -> DEFAULT_QUAL_COLOR_SCHEME
            else -> colorSchemeByIndex(paletteType, 0)
        }
    }


    companion object {
        val DEFAULT_QUAL_COLOR_SCHEME: ColorScheme = ColorPalette.Qualitative.Set1

        private fun paletteType(name: String?): ColorPalette.Type {
            if (name == null) {
                return SEQUENTIAL
            }
            return when (name) {
                "seq" -> SEQUENTIAL
                "div" -> DIVERGING
                "qual" -> QUALITATIVE
                else -> throw IllegalArgumentException(
                    "Palette type expected one of 'seq' (sequential), 'div' (diverging) or 'qual' (qualitative) but was: '$name'"
                )
            }
        }

        private fun colorSchemeByName(paletteType: ColorPalette.Type, paletteName: String): ColorScheme {
            try {
                return when (paletteType) {
                    SEQUENTIAL -> ColorPalette.Sequential.valueOf(paletteName)
                    DIVERGING -> ColorPalette.Diverging.valueOf(paletteName)
                    QUALITATIVE -> ColorPalette.Qualitative.valueOf(paletteName)
                }
            } catch (ignore: IllegalArgumentException) {
                // Enum type has no constant with the specified name error.
                // Replace generic error message with specific one
                throw IllegalArgumentException(cantFindPaletteError(paletteName))
            }
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
