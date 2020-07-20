/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.TranscodingHints
import org.apache.batik.transcoder.image.PNGTranscoder
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.lang.Exception


object PlotPngExport {
    /**
     * @param plotSpec Raw specification of a plot or GGBunch.
     * @param plotSize Desired plot size. Has no effect on GGBunch.
     */
    fun buildPngImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector? = null
    ): ByteArray {

        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(plotSpec, plotSize)
        val svgInput = TranscoderInput(StringReader(svg))

        val ostream = ByteArrayOutputStream()
        val output = TranscoderOutput(ostream)

        val transcoder = PNGTranscoder()
        plotSize?.let {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, it.x.toFloat())
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, it.y.toFloat())
        }

        transcoder.transcode(svgInput, output)
        return ostream.toByteArray()
    }
}