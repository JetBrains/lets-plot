/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.aes.GeomTheme
import jetbrains.datalore.plot.builder.defaultTheme.DefaultGeomTheme.Companion.InheritedColors
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeValuesLPMinimal2
import jetbrains.datalore.plot.builder.presentation.DefaultFontFamilyRegistry
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.builder.theme.*

class DefaultTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry = DefaultFontFamilyRegistry()
) : Theme {

    private val axisX = DefaultAxisTheme("x", options, fontFamilyRegistry)
    private val axisY = DefaultAxisTheme("y", options, fontFamilyRegistry)
    private val legend = DefaultLegendTheme(options, fontFamilyRegistry)
    private val panel = DefaultPanelTheme(options, fontFamilyRegistry)
    private val facets = DefaultFacetsTheme(options, fontFamilyRegistry)
    private val plot = DefaultPlotTheme(options, fontFamilyRegistry)
    private val tooltips = DefaultTooltipsTheme(options, fontFamilyRegistry)

    private val geomInheritedColors = InheritedColors(options, fontFamilyRegistry)
    private val geometries: MutableMap<GeomKind, GeomTheme> = HashMap()

    override fun horizontalAxis(flipAxis: Boolean): AxisTheme = if (flipAxis) axisY else axisX

    override fun verticalAxis(flipAxis: Boolean): AxisTheme = if (flipAxis) axisX else axisY

    override fun legend(): LegendTheme = legend

    override fun panel(): PanelTheme = panel

    override fun facets(): FacetsTheme = facets

    override fun plot(): PlotTheme = plot

    override fun tooltips(): TooltipsTheme = tooltips

    override fun geometries(geomKind: GeomKind): GeomTheme = geometries.getOrPut(geomKind) {
        DefaultGeomTheme.forGeomKind(geomKind, geomInheritedColors)
    }

    companion object {
        // For demo and tests
        fun minimal2() =
            DefaultTheme(ThemeValuesLPMinimal2().values)
    }
}