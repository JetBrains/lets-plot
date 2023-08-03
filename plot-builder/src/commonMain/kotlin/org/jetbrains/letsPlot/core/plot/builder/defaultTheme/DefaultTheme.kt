/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.aes.GeomTheme
import org.jetbrains.letsPlot.core.plot.base.theme.*
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues.Companion.mergeWith
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValuesLPMinimal2
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.presentation.FontFamilyRegistry

class DefaultTheme(
    private val themeSettings: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry = DefaultFontFamilyRegistry(),
    userOptions: Map<String, Any> = emptyMap()
) : Theme {
    // User specific options are applied to the theme settings (combination of named theme and flavor options)
    private val options = themeSettings.mergeWith(userOptions)

    private val axisX = DefaultAxisTheme("x", options, fontFamilyRegistry)
    private val axisY = DefaultAxisTheme("y", options, fontFamilyRegistry)
    private val legend = DefaultLegendTheme(options, fontFamilyRegistry)
    private val panel = DefaultPanelTheme(options, fontFamilyRegistry)
    private val facets = DefaultFacetsTheme(options, fontFamilyRegistry)
    private val plot = DefaultPlotTheme(options, fontFamilyRegistry)
    private val tooltips = DefaultTooltipsTheme(options, fontFamilyRegistry)
    private val geometries: MutableMap<GeomKind, GeomTheme> = HashMap()
    private val colors = DefaultColorTheme(options, fontFamilyRegistry)

    override fun horizontalAxis(flipAxis: Boolean): AxisTheme = if (flipAxis) axisY else axisX

    override fun verticalAxis(flipAxis: Boolean): AxisTheme = if (flipAxis) axisX else axisY

    override fun legend(): LegendTheme = legend

    override fun panel(): PanelTheme = panel

    override fun facets(): FacetsTheme = facets

    override fun plot(): PlotTheme = plot

    override fun tooltips(): TooltipsTheme = tooltips

    override fun geometries(geomKind: GeomKind): GeomTheme = geometries.getOrPut(geomKind) {
        // use settings from named theme and flavor options (without specified in theme())
        DefaultGeomTheme.forGeomKind(geomKind, colors)
    }

    override fun colors(): ColorTheme = colors

    companion object {
        // For demo and tests
        fun minimal2() =
            DefaultTheme(ThemeValuesLPMinimal2().values)
    }
}