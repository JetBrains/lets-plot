/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.color

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.color.GradientUtil.interpolateColors

object PaletteUtil {
    val NULL_COLOR = Color.LIGHT_GRAY

    private val PAL_TYPE_BY_PAL_NAME: Map<String, ColorPalette.Type>

    init {
        val map = HashMap<String, ColorPalette.Type>()
        ColorPalette.Sequential.entries.map { it.name }.forEach {
            map[it] = ColorPalette.Type.SEQUENTIAL
        }
        ColorPalette.Diverging.entries.map { it.name }.forEach {
            map[it] = ColorPalette.Type.DIVERGING
        }
        ColorPalette.Qualitative.entries.map { it.name }.forEach {
            map[it] = ColorPalette.Type.QUALITATIVE
        }

        PAL_TYPE_BY_PAL_NAME = map
    }

    fun schemeColors(colorScheme: ColorScheme, colorCount: Int): List<Color> {
        val colorsHex = colorScheme.getColors(colorCount)
        val colors = fromColorsHex(colorsHex)
        if (colors.size < colorCount) {
            return interpolateColors(colors, colorCount)
        }
        return colors
    }

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

    fun paletteTypeByPaletteName(paletteName: String): ColorPalette.Type? =
        PAL_TYPE_BY_PAL_NAME[paletteName]

    fun colorSchemeByIndex(paletteType: ColorPalette.Type, index: Int): ColorScheme {
        @Suppress("UNCHECKED_CAST")
        val values: Array<ColorScheme> = when (paletteType) {
            ColorPalette.Type.SEQUENTIAL -> ColorPalette.Sequential.entries.toTypedArray() as Array<ColorScheme>
            ColorPalette.Type.DIVERGING -> ColorPalette.Diverging.entries.toTypedArray() as Array<ColorScheme>
            ColorPalette.Type.QUALITATIVE -> ColorPalette.Qualitative.entries.toTypedArray() as Array<ColorScheme>
        }

        return values[index % values.size]
    }
}
