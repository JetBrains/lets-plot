/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.aes.GeomTheme

interface Theme {
    val fontFamilyRegistry: FontFamilyRegistry

    val exponentFormat: ExponentFormat

    fun horizontalAxis(flipAxis: Boolean): AxisTheme

    fun verticalAxis(flipAxis: Boolean): AxisTheme

    fun legend(): LegendTheme

    fun facets(): FacetsTheme

    fun plot(): PlotTheme

    fun panel(): PanelTheme

    fun tooltips(): TooltipsTheme

    fun annotations(): AnnotationsTheme

    fun geometries(geomKind: GeomKind): GeomTheme

    fun colors(): ColorTheme

    fun toInherited(containerTheme: Theme): Theme
}
