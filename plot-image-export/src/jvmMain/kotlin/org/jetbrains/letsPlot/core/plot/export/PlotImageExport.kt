/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.export

import org.jetbrains.letsPlot.awt.canvas.CanvasPane
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.core.util.PlotExportCommon.computeExportParameters
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.roundToInt

object PlotImageExport {
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

        object PNG : Format()
        object TIFF : Format()
        class JPEG(val quality: Double = 0.8) : Format()
    }

    class ImageData(
        val bytes: ByteArray,
        val plotSize: DoubleVector
    )


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
        val (sizingPolicy, scaleFactor) = computeExportParameters(plotSize, targetDPI, unit, scalingFactor)

        val plotFigure = MonolithicCanvas.buildPlotFigureFromRawSpec(
            rawSpec = plotSpec,
            sizingPolicy = sizingPolicy,
            computationMessagesHandler = {}
        )

        val canvasPane = CanvasPane(
            figure = plotFigure,
            pixelDensity = scaleFactor
        )

        val buffer = BufferedImage(
            (plotFigure.bounds().get().width * scaleFactor).roundToInt(),
            (plotFigure.bounds().get().height * scaleFactor).roundToInt(),
            when (format) {
                is Format.PNG -> BufferedImage.TYPE_INT_ARGB
                is Format.TIFF -> BufferedImage.TYPE_INT_ARGB
                is Format.JPEG -> BufferedImage.TYPE_INT_RGB
            }
        )

        val graphics = buffer.createGraphics()

        // TODO: investigate inconsistency in scaling factor.
        // CanvasPane already accepts pixelDensity, which is used to scale the canvas.
        // Yet, when exporting, we apply the scaling factor again - pixelDensity doesn't seem to work as expected.
        graphics.scale(scaleFactor, scaleFactor)

        canvasPane.paint(graphics)

        graphics.dispose()

        val img = ByteArrayOutputStream()
        ImageIO.write(buffer, format.defFileExt, img)

        return ImageData(
            bytes = img.toByteArray(),
            plotSize = DoubleVector(
                x = buffer.width,
                y = buffer.height
            )
        )
    }
}
