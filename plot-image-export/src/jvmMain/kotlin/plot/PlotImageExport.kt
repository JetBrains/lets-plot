/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.PlotSizeHelper.fetchPlotSizeFromSvg
import jetbrains.datalore.plot.PlotSvgExport.buildSvgImageFromRawSpecs
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
import kotlin.math.ceil


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
        val plotSize: DoubleVector,
        val DPI: Int
    )



    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param format Output image format. PNG, TIFF or JPEG (supports quality parameter).
     * @param scaleFactor factor for output image resolution.
     */
    fun buildImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        format: Format,
        scaleFactor: Double
    ): ImageData {
        require(scaleFactor >= .1) { "scale factor is too small: $scaleFactor, must be in range [0.1, 10.0]" }
        require(scaleFactor < 10.0) { "scale factor is too large: $scaleFactor, must be in range [0.1, 10.0]" }

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

        val svg = buildSvgImageFromRawSpecs(plotSpec)

        val plotSize = fetchPlotSizeFromSvg(svg)

        val imageSize = plotSize.mul(scaleFactor)
        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, imageSize.x.toFloat())
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, imageSize.y.toFloat())

        // adds only metadata, doesn't affect resolution/presentation
        val dpi = ceil(scaleFactor * 72).toInt()
        val millimeterPerDot = 25.4 / dpi
        transcoder.addTranscodingHint(
            ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER,
            millimeterPerDot.toFloat()
        )

        transcoder.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white)

        val image = ByteArrayOutputStream()
        transcoder.transcode(TranscoderInput(StringReader(svg)), TranscoderOutput(image))
        return ImageData(image.toByteArray(), plotSize, dpi)
    }
}
