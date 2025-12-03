/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.export

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit

@Deprecated("Use PlotImageExport from platf-awt module", ReplaceWith("PlotImageExport", "org.jetbrains.letsPlot.awt.plot.PlotImageExport"))
object PlotImageExport {
    @Deprecated("Use PlotImageExport from platf-awt module", ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotImageExport.Format", "org.jetbrains.letsPlot.awt.plot.PlotImageExport"))
    sealed class Format {
        val defFileExt: String
            get() {
                return when (this) {
                    is PNG -> "png"
                    is TIFF -> "tiff"
                    is JPEG -> "jpg"
                }
            }

        override fun toString(): String {
            return when (this) {
                is PNG -> "PNG"
                is TIFF -> "TIFF"
                is JPEG -> "JPG(quality=${quality})"
            }
        }

        @Deprecated("Use PlotImageExport from platf-awt module", ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotImageExport.Format.PNG", "org.jetbrains.letsPlot.awt.plot.PlotImageExport"))
        object PNG : Format()
        @Deprecated("Use PlotImageExport from platf-awt module", ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotImageExport.Format.TIFF", "org.jetbrains.letsPlot.awt.plot.PlotImageExport"))
        object TIFF : Format()
        @Deprecated("Use PlotImageExport from platf-awt module", ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotImageExport.Format.JPEG", "org.jetbrains.letsPlot.awt.plot.PlotImageExport"))
        class JPEG(val quality: Double = 0.8) : Format()
    }

    @Deprecated("Use PlotImageExport from platf-awt module", ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotImageExport.ImageData", "org.jetbrains.letsPlot.awt.plot.PlotImageExport.ImageData"))
    class ImageData(
        val bytes: ByteArray,
        val plotSize: DoubleVector
    )

    @Deprecated("Use PlotImageExport from platf-awt module", ReplaceWith("org.jetbrains.letsPlot.awt.plot.PlotImageExport.buildImageFromRawSpecs(plotSpec, format, scalingFactor, targetDPI, plotSize, unit)", "org.jetbrains.letsPlot.awt.plot.PlotImageExport"))
    /**
     * @param plotSpec Raw specification of a plot.
     * @param format Output image format. PNG, TIFF, or JPEG (supports quality parameter).
     * @param scalingFactor A scaling factor to apply to the output image. Useful for generating high-DPI images.
     * @param targetDPI Target DPI for the output image. The default is 96.0 DPI, which is standard for most displays.
     * @param plotSize Size of the output image in the specified unit. Defaults to null, which means the plot's pixel size (default or set by `ggsize()`) will be used.
     * @param unit Unit of measurement for width and height. Can be one of IN (inches), CM (centimeters), MM (millimeters), or PX (pixels). The default is IN (inches).
     */
    fun buildImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        format: Format,
        scalingFactor: Number? = null,
        targetDPI: Number? = null,
        plotSize: DoubleVector? = null,
        unit: SizeUnit? = null,
    ): ImageData {
        val img = org.jetbrains.letsPlot.awt.plot.PlotImageExport.buildImageFromRawSpecs(
            plotSpec,
            when (format) {
                is Format.PNG -> org.jetbrains.letsPlot.awt.plot.PlotImageExport.Format.PNG
                is Format.TIFF -> org.jetbrains.letsPlot.awt.plot.PlotImageExport.Format.TIFF
                is Format.JPEG -> org.jetbrains.letsPlot.awt.plot.PlotImageExport.Format.JPEG(format.quality)
            },
            scalingFactor,
            targetDPI,
            plotSize,
            unit
        )

        return ImageData(
            bytes = img.bytes,
            plotSize = img.plotSize
        )
    }
}
