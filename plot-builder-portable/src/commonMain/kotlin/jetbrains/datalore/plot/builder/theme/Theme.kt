/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.aes.GeomTheme

interface Theme {
    fun horizontalAxis(flipAxis: Boolean): AxisTheme

    fun verticalAxis(flipAxis: Boolean): AxisTheme

    fun legend(): LegendTheme

    fun facets(): FacetsTheme

    fun plot(): PlotTheme

    fun panel(): PanelTheme

    fun tooltips(): TooltipsTheme

    fun geometries(geomKind: GeomKind): GeomTheme
}
