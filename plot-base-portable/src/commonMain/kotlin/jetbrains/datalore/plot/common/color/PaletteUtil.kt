/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.color

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

    private val PAL_TYPE_BY_PAL_NAME: Map<String, ColorPalette.Type>

    init {
        val map = HashMap<String, ColorPalette.Type>()
        ColorPalette.Sequential.values().map { it.name }.forEach {
            map[it] = ColorPalette.Type.SEQUENTIAL
        }
        ColorPalette.Diverging.values().map { it.name }.forEach {
            map[it] = ColorPalette.Type.DIVERGING
        }
        ColorPalette.Qualitative.values().map { it.name }.forEach {
            map[it] = ColorPalette.Type.QUALITATIVE
        }

        PAL_TYPE_BY_PAL_NAME = map
    }

    private fun isExtensibleScheme(colorScheme: ColorScheme): Boolean {
        return EXTENSIBLE_COLOR_SCHEMES.contains(colorScheme)
    }

    fun schemeColors(colorScheme: ColorScheme, colorCount: Int): List<Color> {
        val colorsHex = colorScheme.getColors(colorCount)
        val colors = fromColorsHex(colorsHex)
        if (colorsHex.size < colorCount && isExtensibleScheme(colorScheme)) {
            val addColors = ColorUtil.genColors(colorCount - colorsHex.size, colors)
            return colors + addColors
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
            ColorPalette.Type.SEQUENTIAL -> ColorPalette.Sequential.values() as Array<ColorScheme>
            ColorPalette.Type.DIVERGING -> ColorPalette.Diverging.values() as Array<ColorScheme>
            ColorPalette.Type.QUALITATIVE -> ColorPalette.Qualitative.values() as Array<ColorScheme>
        }

        return values[index % values.size]
    }
}
