/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.plot.builder.theme.*

class DefaultTheme(
    private val options: Map<String, Any>
) : Theme {

    private val axisX = DefaultAxisTheme("x", options)
    private val axisY = DefaultAxisTheme("y", options)
    private val legend = DefaultLegendTheme(options)
    private val panel = DefaultPanelTheme(options)
    private val facets = DefaultFacetsTheme(options)
    private val plot = DefaultPlotTheme(options)


    override fun axisX(): AxisTheme = axisX

    override fun axisY(): AxisTheme = axisY

    override fun legend(): LegendTheme = legend

    override fun panel(): PanelTheme = panel

    override fun facets(): FacetsTheme = facets

    override fun plot(): PlotTheme = plot

    override fun multiTile(): Theme {
        // The same for now.
        return this
    }
}