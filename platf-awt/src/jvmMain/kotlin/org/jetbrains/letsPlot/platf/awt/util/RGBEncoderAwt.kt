/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.util

import org.jetbrains.letsPlot.commons.encoding.RGBEncoder

class RGBEncoderAwt : RGBEncoder {
    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        return RgbToDataUrl.png(width, height, argbValues)
    }
}