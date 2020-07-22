/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.PlotSvgExport.buildSvgImageFromRawSpecs
import org.apache.batik.transcoder.ErrorHandler
import org.apache.batik.transcoder.TranscoderException
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.transcoder.image.TIFFTranscoder
import java.io.ByteArrayOutputStream
import java.io.StringReader


object PlotImageExport {
    interface Format
    class PNG: Format
    class TIFF: Format
    class JPEG(val quality: Double = 0.8): Format

    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     * @param format Output image format. PNG, TIFF or JPEG (supports quality parameter).
     * @param scaleFactor factor for output image resolution.
     */
    fun buildImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null,
        format: Format = PNG(),
        scaleFactor: Double = 1.0
    ): ByteArray {
        require(scaleFactor == 1.0 || plotSize != null) { "plotSize should be set when scaleFactor != 1.0 ($scaleFactor)" }

        val transcoder = when(format) {
            is TIFF -> TIFFTranscoder()
            is PNG -> PNGTranscoder()
            is JPEG -> {
                JPEGTranscoder().apply {
                    addTranscodingHint(JPEGTranscoder.KEY_QUALITY, format.quality.toFloat())
                }
            }
            else -> error("Unsupported format: $format")
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

        val imageSize = plotSize?.mul(scaleFactor)
        if (imageSize != null) {
            transcoder.apply {
                addTranscodingHint(ImageTranscoder.KEY_WIDTH, imageSize.x.toFloat())
                addTranscodingHint(ImageTranscoder.KEY_HEIGHT, imageSize.y.toFloat())
            }
        }
        // adds only metadata, doesn't affect resolution/presentation
        transcoder.addTranscodingHint(ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, (25.4 / (96.0 * scaleFactor)).toFloat())

        val svg = buildSvgImageFromRawSpecs(plotSpec, plotSize).let(::StringReader)
        val image = ByteArrayOutputStream()

        transcoder.transcode(TranscoderInput(svg), TranscoderOutput(image))
        return image.toByteArray()
    }
}
