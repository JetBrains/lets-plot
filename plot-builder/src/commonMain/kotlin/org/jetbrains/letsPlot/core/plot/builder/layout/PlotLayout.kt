/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider

interface PlotLayout {
    /**
     * 'plot inner size' is the size of the plot without titles, axis titles, legends, tags, and plot margins.
     */
    fun layoutByPlotSize(
        plotInnerSize: DoubleVector,
        coordProvider: CoordProvider
    ): PlotLayoutInfo

    fun layoutByGeomSize(
        geomContentSize: DoubleVector,
        coordProvider: CoordProvider,
        axisSpacer: Thickness = Thickness.ZERO
    ): PlotLayoutInfo
}
