package org.jetbrains.letsPlot.commons.image

import org.jetbrains.letsPlot.commons.values.Bitmap

actual fun loadImage(bytes: ByteArray): Bitmap {
    throw UnsupportedOperationException("Not implemented for Native platform")
}