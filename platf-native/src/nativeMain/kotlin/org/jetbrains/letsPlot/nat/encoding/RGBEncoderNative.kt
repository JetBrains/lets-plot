/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.encoding.RGBEncoder

class RGBEncoderNative : RGBEncoder {
    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        return Png.encodeDataImage(width, height, argbValues)
    }
}
