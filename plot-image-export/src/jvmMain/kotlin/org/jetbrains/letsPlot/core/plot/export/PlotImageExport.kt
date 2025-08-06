/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.export

import org.jetbrains.letsPlot.awt.canvas.CanvasPane
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.roundToInt

object PlotImageExport {
    enum class Unit {
        IN, CM, MM, PX;
    }

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
     * @param scalingFactor A scaling factor to apply to the output image. Useful for generating high-DPI images. The default is 1.0 (no scaling).
     * @param targetDPI Target DPI for the output image. The default is 96.0 DPI, which is standard for most displays.
     * @param width Width of the output image in the specified unit. Defaults to null, which means the plot's pixel size (default or set by `ggsize()`) will be used.
     * @param height Height of the output image in the specified unit. Defaults to null, which means the plot's pixel size (default or set by `ggsize()`) will be used.
     * @param unit Unit of measurement for width and height. Can be one of IN (inches), CM (centimeters), MM (millimeters), or PX (pixels). The default is PX (pixels).
     */
    fun buildImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        format: Format,
        scalingFactor: Double = 1.0,
        targetDPI: Number = 96,
        width: Number? = null,
        height: Number? = null,
        unit: Unit = Unit.PX,
    ): ImageData {
        val sizingPolicy = when {
            width == null || height == null -> SizingPolicy.keepFigureDefaultSize()
            else -> {
                val w = width.toDouble()
                val h = height.toDouble()

                // Build the plot in logical pixels (always 96 DPI) and then render it scaled.
                // Otherwise, the plot will be rendered incorrectly, i.e., with too many tick labels and small font sizes.
                val (logicalWidth, logicalHeight) = when (unit) {
                    Unit.CM -> (w * 96 / 2.54) to (h * 96 / 2.54)
                    Unit.IN -> (w * 96) to (h * 96)
                    Unit.MM -> (w * 96 / 25.4) to (h * 96 / 25.4)
                    Unit.PX -> w to h
                }

                SizingPolicy.fixed(
                    width = logicalWidth,
                    height = logicalHeight
                )
            }
        }

        val scaleFactor = when {
            targetDPI.toDouble() > 0 -> targetDPI.toDouble() / 96.0 * scalingFactor
            else -> scalingFactor
        }

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
