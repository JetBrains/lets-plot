/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import org.jetbrains.letsPlot.commons.values.Bitmap

object UnsupportedRGBEncoder : RGBEncoder {
    override fun toDataUrl(bitmap: Bitmap): String {
        throw IllegalStateException("Can't encode RGB data as Data URL: operation is not supported.")
    }
}