/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import org.jetbrains.letsPlot.commons.values.Bitmap

interface RGBEncoder {
    fun toDataUrl(bitmap: Bitmap): String

    companion object {
        // Encoding in JS will fail - its missing deflate implementation
        val DEFAULT: RGBEncoder = object : RGBEncoder {
            override fun toDataUrl(bitmap: Bitmap): String {
                return Png.encodeDataImage(bitmap)
            }
        }
    }
}
