/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.batik.mapping.svg

import org.jetbrains.letsPlot.platf.awt.util.RgbToDataUrl
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx

internal class RGBEncoderAwt : SvgImageElementEx.RGBEncoder {
    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        return RgbToDataUrl.png(width, height, argbValues)
    }
}