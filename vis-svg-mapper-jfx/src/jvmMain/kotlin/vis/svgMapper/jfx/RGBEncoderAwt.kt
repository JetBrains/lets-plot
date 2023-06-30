/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import org.jetbrains.letsPlot.awt.util.RGBtoDataUrl
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx

internal class RGBEncoderAwt : SvgImageElementEx.RGBEncoder {
    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        return RGBtoDataUrl.png(width, height, argbValues)
    }
}