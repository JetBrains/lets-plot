/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme


interface Theme {
    fun axisX(flipAxis: Boolean = false): AxisTheme

    fun axisY(flipAxis: Boolean = false): AxisTheme

    fun legend(): LegendTheme

    fun facets(): FacetsTheme

    fun plot(): PlotTheme

    fun panel(): PanelTheme
}
