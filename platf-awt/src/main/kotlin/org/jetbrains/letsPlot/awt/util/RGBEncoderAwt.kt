/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.util

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.encoding.RGBEncoder
import org.jetbrains.letsPlot.commons.values.Bitmap

class RGBEncoderAwt : RGBEncoder {
    override fun toDataUrl(bitmap: Bitmap): String {
        return Png.encodeDataImage(bitmap)
    }
}