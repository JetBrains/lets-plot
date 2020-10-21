/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideBreak
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.WithGuideBreaks
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.color.ColorPalette
import jetbrains.datalore.plot.common.color.ColorPalette.Qualitative.Set2
import jetbrains.datalore.plot.common.color.ColorPalette.Qualitative.Set3
import jetbrains.datalore.plot.common.color.ColorPalette.Type.*
import jetbrains.datalore.plot.common.color.ColorScheme
import jetbrains.datalore.plot.common.color.PaletteUtil
import jetbrains.datalore.plot.common.color.PaletteUtil.colorSchemeByIndex


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

    override fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<Color> {
        val colorScheme = colorScheme(true, domainValues.size)
        val colors = colors(colorScheme, domainValues.size)
        return GuideMappers.discreteToDiscrete(domainValues, colors, naValue)
    }

    override fun createContinuousMapper(
        domain: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: Transform?
    ): GuideMapper<Color> {
        val colorScheme = colorScheme(false)
        val colors = colors(colorScheme, colorScheme.maxColors)

        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, lowerLimit, upperLimit, trans)
        return GuideMappers.continuousToDiscrete(domain, colors, naValue)
    }

    private fun colors(colorScheme: ColorScheme, count: Int): List<Color> {
        val colors: List<Color> = PaletteUtil.schemeColors(colorScheme, count)
        return when (direction?.let { direction < 0 } ?: false) {
            true -> Lists.reverse(colors)
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
            paletteType == QUALITATIVE -> {
                if (colorCount != null && colorCount <= Set2.maxColors) Set2
                else Set3
            }
            else -> colorSchemeByIndex(paletteType, 0)
        }
    }


    companion object {
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
                // Replace generic error massage with specific one
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
