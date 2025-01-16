/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.export

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.awt.plot.PlotSvgExport.buildSvgImageFromRawSpecs
import org.jetbrains.letsPlot.core.util.PlotSvgHelper.fetchPlotSizeFromSvg
import org.apache.batik.transcoder.ErrorHandler
import org.apache.batik.transcoder.TranscoderException
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.transcoder.image.TIFFTranscoder
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.StringReader

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
     * @param format Output image format. PNG, TIFF or JPEG (supports quality parameter).
     * @param scalingFactor Factor for output image resolution.
     * @param targetDPI A resolution value to put in the output image metadata. NaN - leave the metadata empty.
     */
    fun buildImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        format: Format,
        scalingFactor: Double,
        targetDPI: Double
    ): ImageData {
        require(scalingFactor >= .1) { "scaling factor is too small: $scalingFactor, must be in range [0.1, 10.0]" }
        require(scalingFactor <= 10.0) { "scaling factor is too large: $scalingFactor, must be in range [0.1, 10.0]" }

        val transcoder = when (format) {
            is Format.TIFF -> TIFFTranscoder()
            is Format.PNG -> PNGTranscoder()
            is Format.JPEG -> {
                JPEGTranscoder().apply {
                    addTranscodingHint(JPEGTranscoder.KEY_QUALITY, format.quality.toFloat())
                }
            }
        }
        transcoder.errorHandler = object : ErrorHandler {
            override fun warning(ex: TranscoderException?) {
            }

            override fun error(ex: TranscoderException?) {
                ex?.let { throw it } ?: error("PlotImageExport: empty transcoder exception")
            }

            override fun fatalError(ex: TranscoderException?) {
                ex?.let { throw it } ?: error("PlotImageExport: empty transcoder exception")
            }
        }

        val svg = buildSvgImageFromRawSpecs(plotSpec, useCssPixelatedImageRendering = false) // Batik transcoder supports SVG style, not CSS

        val plotSize = fetchPlotSizeFromSvg(svg)

        val imageSize = plotSize.mul(scalingFactor)
        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, imageSize.x.toFloat())
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, imageSize.y.toFloat())

/*
        val dpi = ceil(scalingFactor * 72).toInt()
        val millimeterPerDot = 25.4 / dpi
        transcoder.addTranscodingHint(
            ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER,
            millimeterPerDot.toFloat()
        )
*/
        if (targetDPI.isFinite()) {
            val millimeterPerDot = 25.4 / targetDPI
            transcoder.addTranscodingHint(
                ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER,
                millimeterPerDot.toFloat()
            )
        }

        transcoder.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white)

        val image = ByteArrayOutputStream()
        transcoder.transcode(TranscoderInput(StringReader(svg)), TranscoderOutput(image))
        return ImageData(image.toByteArray(), plotSize)
    }
}
