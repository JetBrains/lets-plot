/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svgMapper.awt.RGBEncoderAwt
import jetbrains.datalore.vis.svgToString.SvgToString

object PlotSvgExport {
    private val JVM_SVG_STR_MAPPER =
        SvgToString(RGBEncoderAwt())   // Supports data-frame --> rgb image transform (geom_raster)

    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null
    ): String {
        return PlotSvgExportPortable.buildSvgImageFromRawSpecs(plotSpec, plotSize, JVM_SVG_STR_MAPPER)
    }
}