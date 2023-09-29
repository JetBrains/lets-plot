/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues.Companion.mergeWith
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.presentation.FontFamilyRegistry

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
        val baselineValues = ThemeValues.forName(themeName).values

        val flavorName = if (themeName != ThemeOption.Name.LP_NONE) {
            // use flavor to not 'none' theme only
            userOptions[ThemeOption.FLAVOR] as? String
                ?: baselineValues[ThemeOption.FLAVOR] as? String
                ?: error("Flavor name should be specified")
        } else {
            null
        }

        return (flavorName?.let { applyFlavor(baselineValues, flavorName) } ?: baselineValues)
            .mergeWith(userOptions)
    }

    private fun applyFlavor(themeSettings: Map<String, Any>, flavorName: String): Map<String, Any> {
        val flavor = ThemeFlavor.forName(flavorName)

        val geomThemeOptions = mapOf(
            ThemeOption.GEOM to mapOf(
                ThemeOption.Geom.PEN to flavor.pen,
                ThemeOption.Geom.PAPER to flavor.paper,
                ThemeOption.Geom.BRUSH to flavor.brush
            )
        )

        // resolve symbolic colors
        val withResolvedColors = themeSettings.mapValues { (parameter, options) ->
            if (options !is Map<*, *>) {
                return@mapValues options
            }
            options.mapValues { (key, value) ->
                when (value) {
                    !is SymbolicColor -> value
                    else -> flavor.symbolicColors[value]
                        ?: error("Undefined color in flavor scheme = '$flavorName': '$parameter': '${key}' = '${value.name}'")
                }
            }
        }
            .mergeWith(flavor.specialColors)

        return geomThemeOptions + withResolvedColors
    }
}