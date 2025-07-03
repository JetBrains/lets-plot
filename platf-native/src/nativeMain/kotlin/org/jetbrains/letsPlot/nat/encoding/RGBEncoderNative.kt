/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.encoding.RGBEncoder
import org.jetbrains.letsPlot.commons.values.Bitmap

class RGBEncoderNative : RGBEncoder {
    override fun toDataUrl(bitmap: Bitmap) = Png.encodeDataImage(bitmap)
}
