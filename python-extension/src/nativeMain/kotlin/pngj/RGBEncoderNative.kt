/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package pngj

import jetbrains.datalore.base.encoding.Base64
import jetbrains.datalore.vis.svg.SvgImageElementEx.RGBEncoder
import jetbrains.datalore.vis.svg.SvgUtils
import org.jetbrains.letsPlot.util.pngj.ImageInfo
import org.jetbrains.letsPlot.util.pngj.ImageLineByte
import org.jetbrains.letsPlot.util.pngj.OutputPngStream
import org.jetbrains.letsPlot.util.pngj.PngWriter

class RGBEncoderNative : RGBEncoder {

    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        val outputStream = OutputPngStream()
        val png = PngWriter(
            outputStream, ImageInfo(
                width,
                height,
                bitdepth = 8,
                alpha = true
            )
        )

        val iLine = ImageLineByte(png.imgInfo)

        for (y in 0 .. height) {
            for (x in 0 .. width) {
                iLine.scanline[x] = argbValues[y * width + x].toByte()
            }
            png.writeRow(iLine)
        }
        return SvgUtils.pngDataURI(Base64.encode(outputStream.byteArray))

    }
}
