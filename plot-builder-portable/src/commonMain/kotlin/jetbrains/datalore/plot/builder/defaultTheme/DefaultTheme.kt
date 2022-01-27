/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeValuesLPMinimal2
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


    override fun axisX(flipAxis: Boolean): AxisTheme = if (flipAxis) axisY else axisX

    override fun axisY(flipAxis: Boolean): AxisTheme = if (flipAxis) axisX else axisY

    override fun legend(): LegendTheme = legend

    override fun panel(): PanelTheme = panel

    override fun facets(): FacetsTheme = facets

    override fun plot(): PlotTheme = plot

    companion object {
        fun minimal2() = DefaultTheme(ThemeValuesLPMinimal2().values)
    }
}