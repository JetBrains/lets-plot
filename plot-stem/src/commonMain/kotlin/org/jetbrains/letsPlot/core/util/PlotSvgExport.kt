package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString

object PlotSvgExport {
    private val LOG = PortableLogging.logger(PlotSvgExport::class)

    /**
     * @param plotSpec Raw specification of a plot.
     * @param plotSize Desired plot size.
     * @param sizeUnit Size unit for the plot size. The default is pixels (PX). null for auto-detect.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        sizeUnit: SizeUnit? = SizeUnit.PX,
    ): String {
        val svgToString = SvgToString()
        return MonolithicCommon.buildSvgImageFromRawSpecs(plotSpec, plotSize, sizeUnit, svgToString) { messages ->
            messages.forEach {
                LOG.info { "[when SVG generating] $it" }
            }
        }
    }
}