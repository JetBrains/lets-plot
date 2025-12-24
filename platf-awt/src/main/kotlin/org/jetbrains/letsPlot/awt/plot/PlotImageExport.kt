/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.core.util.PlotExportCommon.computeExportParameters
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

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

        val plotFigure = PlotCanvasFigure()
        plotFigure.update(
            processedSpec = MonolithicCommon.processRawSpecs(plotSpec = plotSpec, frontendOnly = false),
            sizingPolicy = sizingPolicy,
            computationMessagesHandler = {}
        )

        val awtCanvasPeer = AwtCanvasPeer(scaleFactor)
        plotFigure.mapToCanvas(awtCanvasPeer)

        val canvas = awtCanvasPeer.createCanvas(plotFigure.size)

        // Note: the scale is already applied in AwtCanvas constructor
        //canvas.context2d.scale(scaleFactor, scaleFactor)

        plotFigure.paint(canvas.context2d)

        val outputStream = ByteArrayOutputStream()

        if (format.defFileExt == "jpg") {
            // JPEG does not support transparency. We need to fill the background with white color.
            val rgbBufferedImage = BufferedImage(canvas.image.width, canvas.image.height, BufferedImage.TYPE_INT_RGB)
            val g = rgbBufferedImage.createGraphics()
            g.drawImage(canvas.image, 0, 0, Color.WHITE, null)
            g.dispose()
            ImageIO.write(rgbBufferedImage, format.defFileExt, outputStream)
        } else {
            ImageIO.write(canvas.image, format.defFileExt, outputStream)
        }

        return ImageData(
            bytes = outputStream.toByteArray(),
            plotSize = DoubleVector(
                x = plotFigure.size.x.toDouble(),
                y = plotFigure.size.y.toDouble()
            )
        )
    }
}
