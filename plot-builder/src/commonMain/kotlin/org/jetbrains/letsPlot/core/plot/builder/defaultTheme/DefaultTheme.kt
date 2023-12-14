/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.aes.GeomTheme
import org.jetbrains.letsPlot.core.plot.base.theme.*
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.COLOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.FILL
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.Margin
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.SIZE
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry

class DefaultTheme internal constructor(
    private val options: Map<String, Any>,
    override val fontFamilyRegistry: FontFamilyRegistry = DefaultFontFamilyRegistry(),
) : Theme {
    private val axisX = DefaultAxisTheme("x", options, fontFamilyRegistry)
    private val axisY = DefaultAxisTheme("y", options, fontFamilyRegistry)
    private val legend = DefaultLegendTheme(options, fontFamilyRegistry)
    private val panel = DefaultPanelTheme(options, fontFamilyRegistry)
    private val facets = DefaultFacetsTheme(options, fontFamilyRegistry)
    private val plot = DefaultPlotTheme(options, fontFamilyRegistry)
    private val tooltips = DefaultTooltipsTheme(options, fontFamilyRegistry)
    private val annotations = DefaultAnnotationsTheme(options, fontFamilyRegistry)
    private val geometries: MutableMap<GeomKind, GeomTheme> = HashMap()
    private val colors = DefaultColorTheme(options, fontFamilyRegistry)

    override val exponentFormat: ExponentFormat = options[ThemeOption.EXPONENT_FORMAT] as? ExponentFormat ?: ExponentFormat.E

    override fun horizontalAxis(flipAxis: Boolean): AxisTheme = if (flipAxis) axisY else axisX

    override fun verticalAxis(flipAxis: Boolean): AxisTheme = if (flipAxis) axisX else axisY

    override fun legend(): LegendTheme = legend

    override fun panel(): PanelTheme = panel

    override fun facets(): FacetsTheme = facets

    override fun plot(): PlotTheme = plot

    override fun tooltips(): TooltipsTheme = tooltips

    override fun annotations(): AnnotationsTheme = annotations

    override fun geometries(geomKind: GeomKind): GeomTheme = geometries.getOrPut(geomKind) {
        DefaultGeomTheme.forGeomKind(geomKind, colors)
    }

    override fun colors(): ColorTheme = colors

    override fun toInherited(containerTheme: Theme): Theme {
        if (!(containerTheme is DefaultTheme)) return this  // can't inherit

        val inheritedOptions = containerTheme.options.mapValues { (k, v) ->
            // Inherit all but color and size of container's background rect.
            when (k) {
                ThemeOption.PLOT_BKGR_RECT -> {
                    mapOf(
                        BLANK to !containerTheme.plot.showBackground(),
                        FILL to containerTheme.plot.backgroundFill(),
                        COLOR to this.plot.backgroundColor(),
                        SIZE to this.plot.backgroundStrokeWidth(),
                    )
                }

                ThemeOption.PLOT_MARGIN -> with (this.plot.plotMargins()) {
                    mapOf(
                        Margin.TOP to top,
                        Margin.RIGHT to right,
                        Margin.BOTTOM to bottom,
                        Margin.LEFT to left
                    )
                }

                else -> v
            }
        }

        return DefaultTheme(inheritedOptions, fontFamilyRegistry)
    }

    companion object {
        // For demo and tests
        fun minimal2() = ThemeUtil.buildTheme(ThemeOption.Name.LP_MINIMAL)
    }
}