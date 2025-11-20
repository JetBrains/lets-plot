/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues.Companion.mergeWith

object ThemeUtil {

    fun buildTheme(
        themeName: String,
        userOptions: Map<String, Any> = emptyMap(),
        fontFamilyRegistry: FontFamilyRegistry = DefaultFontFamilyRegistry()
    ) = DefaultTheme(
        getThemeValues(themeName, userOptions),
        fontFamilyRegistry
    )

    // open for ThemeOptionTest
    internal fun getThemeValues(themeName: String, userOptions: Map<String, Any> = emptyMap()): Map<String, Any> {
        val baselineValues = ThemeValues.forName(themeName)

        val effectiveOptions = baselineValues + userOptions

        if (themeName == ThemeOption.Name.LP_NONE) {
            // Not apply flavor to the 'none' theme
            return effectiveOptions
        }

        val userFlavorName = userOptions[ThemeOption.FLAVOR] as? String

        val flavor: ThemeFlavor = when (userFlavorName) {
            null,
            ThemeOption.Flavor.STANDARD -> baselineValues.defaultFlavor()
            else -> ThemeFlavor.forName(userFlavorName)
        }

        val geomThemeOptions = mapOf(
            ThemeOption.GEOM to mapOf(
                ThemeOption.Geom.PEN to flavor.pen,
                ThemeOption.Geom.PAPER to flavor.paper,
                ThemeOption.Geom.BRUSH to flavor.brush
            )
        )

        // resolve symbolic colors
        val withResolvedColors = effectiveOptions.mapValues { (parameter, options) ->
            val flavorNameForError = userFlavorName ?: "default($themeName)"
            val subOptions = options as? Map<*, *> ?: return@mapValues options
            subOptions.mapValues subOptionsScope@{ (key, value) ->
                val color = value as? SymbolicColor ?: return@subOptionsScope value
                flavor.symbolicColors[color]
                    ?: error("Undefined color in flavor scheme = '$flavorNameForError': '$parameter': '${key}' = '${color.name}'")
            }
        }

        return geomThemeOptions
            .mergeWith(flavor.specialColors)
            .mergeWith(withResolvedColors)
    }
}