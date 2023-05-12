/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svgMapper.awt.RGBEncoderAwt
import jetbrains.datalore.vis.svgToString.SvgToString

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
        // Supports data-frame --> rgb image transform (geom_raster)
        val jvmSvgStrMapper = SvgToString(RGBEncoderAwt(), useCssPixelatedImageRendering)
        return PlotSvgExportPortable.buildSvgImageFromRawSpecs(plotSpec, plotSize, jvmSvgStrMapper)
    }
}