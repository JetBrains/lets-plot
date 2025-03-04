/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.encoding.RGBEncoder
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString

object PlotSvgExportCommon {
    private val LOG = PortableLogging.logger(PlotSvgExportCommon::class)

    /**
     * @param plotSpec Raw specification of a plot.
     * @param plotSize Desired plot size.
     * @param rgbEncoder Platform-specific implementation of SvgImageElementEx.RGBEncoder interface. Needed for `geom_raster()`.
     * @param useCssPixelatedImageRendering true for CSS style "pixelated", false for SVG style "optimizeSpeed". Used for compatibility.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        rgbEncoder: RGBEncoder,
        useCssPixelatedImageRendering: Boolean
    ): String {
        val svgToString = SvgToString(rgbEncoder, useCssPixelatedImageRendering)
        return MonolithicCommon.buildSvgImageFromRawSpecs(
            plotSpec,
            plotSize,
            svgToString
        ) { messages ->
            messages.forEach {
                LOG.info { "[when SVG generating] $it" }
            }
        }
    }
}