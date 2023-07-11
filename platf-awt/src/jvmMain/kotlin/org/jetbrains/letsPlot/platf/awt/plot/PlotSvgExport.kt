/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.PlotSvgExportPortable
import org.jetbrains.letsPlot.platf.awt.util.RgbToDataUrl
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx

object PlotSvgExport {
    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     * @param useCssPixelatedImageRendering true for CSS style "pixelated", false for SVG style "optimizeSpeed". Used for compatibility.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        useCssPixelatedImageRendering: Boolean = true
    ): String {
        return PlotSvgExportPortable.buildSvgImageFromRawSpecs(
            plotSpec = plotSpec,
            plotSize = plotSize,
            rgbEncoder = RGBEncoderAwt(),
            useCssPixelatedImageRendering
        )
    }
}

// ToDo: This is AWT-based and will fail on Android.
private class RGBEncoderAwt : SvgImageElementEx.RGBEncoder {
    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        return RgbToDataUrl.png(width, height, argbValues)
    }
}