/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.theme.ColorTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.GEOM
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Geom.BRUSH
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Geom.PAPER
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Geom.PEN
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry

internal class DefaultColorTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), ColorTheme {

    private val geomElem = getElemValue(listOf(GEOM))

    override fun pen() = getColor(geomElem, PEN)

    override fun brush() = getColor(geomElem, BRUSH)

    override fun paper() = getColor(geomElem, PAPER)
}