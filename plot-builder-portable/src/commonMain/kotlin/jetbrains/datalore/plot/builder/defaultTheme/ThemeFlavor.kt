/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption

class ThemeFlavor(
    private val fill: Color,
    private val color: Color,
) {
    constructor(fillHexColor: String, colorHexColor: String) : this(
        Color.parseHex(fillHexColor),
        Color.parseHex(colorHexColor)
    )

    fun updateColors(options: Map<String, Any>): Map<String, Any> {
        return options.mapValues { (_, value) ->
            if (value is Map<*, *> && value != ThemeOption.ELEMENT_BLANK) {
                val updated = value.toMutableMap()

                // todo skip FILL or/and COLOR for some options...
                updated[ThemeOption.Elem.FILL] = fill
                updated[ThemeOption.Elem.COLOR] = color

                updated
            } else {
                value
            }
        }
    }

    companion object {
        fun forName(flavor: String): ThemeFlavor {
            return when (flavor) {
                ThemeOption.Flavor.DARCULA -> ThemeFlavor("#3c3f41", "#bbbbbb")
                ThemeOption.Flavor.INTELLIJ -> ThemeFlavor("#F2F2F2", "#000000")
                ThemeOption.Flavor.SOLARIZED_LIGHT -> ThemeFlavor("#EEE8D5", "#2E4E58")
                ThemeOption.Flavor.SOLARIZED_DARK -> ThemeFlavor("#0E3C4A", "#A7B6BA")
                ThemeOption.Flavor.HIGH_CONTRAST_LIGHT -> ThemeFlavor("#FFFFFF", "#000000")
                ThemeOption.Flavor.HIGH_CONTRAST_DARK -> ThemeFlavor("#000000", "#FFFFFF")
                else -> throw IllegalArgumentException("Unsupported theme flavor: '$flavor'")
            }
        }
    }
}