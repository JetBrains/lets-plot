/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.core.util.PlotSvgExport

object PlotSvgExport {
    /**
     * @param plotSpec Raw specification of a plot.
     * @param plotSize Desired plot size.
     * @param sizeUnit Size unit for the plot size. The default is pixels (PX).
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        sizeUnit: SizeUnit = SizeUnit.PX
    ): String {
        return PlotSvgExport.buildSvgImageFromRawSpecs(
            plotSpec = plotSpec,
            plotSize = plotSize,
            sizeUnit = sizeUnit
        )
    }
}
