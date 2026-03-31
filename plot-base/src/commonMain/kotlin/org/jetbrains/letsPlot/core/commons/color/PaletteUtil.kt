/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.color

import org.jetbrains.letsPlot.commons.colorspace.HCL
import org.jetbrains.letsPlot.commons.colorspace.hclFromRgb
import org.jetbrains.letsPlot.commons.colorspace.rgbFromHcl
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

    fun schemeColors(
        colorScheme: ColorScheme,
        colorCount: Int,
        overflow: PaletteOverflow = PaletteOverflow.AUTO
    ): List<Color> {
        val colorsHex = colorScheme.getColors(colorCount)
        val colors = fromColorsHex(colorsHex)
        if (colors.size < colorCount) {
            return when (overflow) {
                PaletteOverflow.INTERPOLATE -> interpolateColors(colors, colorCount)
                PaletteOverflow.CYCLE -> cycleColors(colors, colorCount)
                PaletteOverflow.GENERATE -> generateColors(colors, colorCount)
                PaletteOverflow.AUTO -> when (colorScheme.type) {
                    ColorPalette.Type.QUALITATIVE -> generateColors(colors, colorCount)
                    ColorPalette.Type.SEQUENTIAL,
                    ColorPalette.Type.DIVERGING -> interpolateColors(colors, colorCount)
                }
            }
        }
        return colors
    }

    private fun cycleColors(colors: List<Color>, count: Int): List<Color> {
        return (0 until count).map { colors[it % colors.size] }
    }

    private fun generateColors(colors: List<Color>, count: Int): List<Color> {
        if (colors.isEmpty()) return emptyList()

        val hclColors = colors.map { hclFromRgb(it) }
        val needed = count - colors.size

        // Luminance range for generated colors:
        val min = 10.0
        val max = 90.0
        val range = max - min
        val step = 15.0

        // Cycle through palette colors in rounds with alternating luminance deltas:
        // Round 1: +step, Round 2: -step, Round 3: +2*step, Round 4: -2*step, ...
        val newColors = (0 until needed).map { i ->
            val base = hclColors[i % hclColors.size]
            val round = i / hclColors.size
            val magnitude = (round / 2 + 1) * step
            val sign = if (round % 2 == 0) 1 else -1
            // Luminance wraps around within [min, max].
            val newL = ((base.l + sign * magnitude - min) % range + range) % range + min
            rgbFromHcl(HCL(base.h, base.c, newL))
        }

        return colors + newColors
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
