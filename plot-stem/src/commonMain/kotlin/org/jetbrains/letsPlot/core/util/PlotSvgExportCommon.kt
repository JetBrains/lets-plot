/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit

object PlotSvgExportCommon {
    private val LOG = PortableLogging.logger(PlotSvgExportCommon::class)

    /**
     * @param plotSpec Raw specification of a plot.
     * @param plotSize Desired plot size.
     * @param sizeUnit Size unit for the plot size. The default is pixels (PX).
     */
    @Deprecated(
        message = "Use PlotSvgExport.buildSvgImageFromRawSpecs()",
        replaceWith = ReplaceWith("PlotSvgExport.buildSvgImageFromRawSpecs(plotSpec, plotSize, sizeUnit)")
    )
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        sizeUnit: SizeUnit? = null,
    ): String {
        return PlotSvgExport.buildSvgImageFromRawSpecs(plotSpec, plotSize, sizeUnit)
    }
}