/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues.Companion.mergeWith
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.presentation.FontFamilyRegistry

class ThemeBuilder(
    private val themeName: String,
    private val userOptions: Map<String, Any> = emptyMap(),
    private val fontFamilyRegistry: FontFamilyRegistry = DefaultFontFamilyRegistry()
) {
    fun build(): DefaultTheme {

        val baselineValues = ThemeValues.forName(themeName).values

        val flavorName = userOptions[ThemeOption.FLAVOR] as? String
            ?: baselineValues[ThemeOption.FLAVOR] as? String
            ?: error("Flavor name should be specified")

        val effectiveOptions = ThemeFlavorUtil.applyFlavor(baselineValues, flavorName)
            .mergeWith(userOptions)

        return DefaultTheme(effectiveOptions, fontFamilyRegistry)
    }
}