/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.awt

import jetbrains.datalore.base.awt.toPngDataUri
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx.RGBEncoder

import java.awt.image.BufferedImage
import java.io.IOException

class RGBEncoderAwt : RGBEncoder {

    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        for (y in 0 until height) {
            for (x in 0 until width) {
                bufferedImage.setRGB(x, y, argbValues[y * width + x])
            }
        }

        try {
            return bufferedImage.toPngDataUri()
        } catch (e: IOException) {
            throw IllegalArgumentException("Can't build image $width X $height", e)
        }

    }
}
