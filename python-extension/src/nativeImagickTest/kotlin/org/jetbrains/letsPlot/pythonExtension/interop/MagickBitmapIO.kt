/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package kotlin.org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.ImageComparer
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil

object MagickBitmapIO : ImageComparer.BitmapIO {
    override fun write(bitmap: Bitmap, filePath: String) {
        val img = MagickUtil.fromBitmap(bitmap)
        if (ImageMagick.MagickWriteImage(img, filePath) == ImageMagick.MagickFalse) {
            val error = getMagickError(img)
            ImageMagick.DestroyMagickWand(img)
            error("Failed to write image to $filePath: $error")
        }
        ImageMagick.DestroyMagickWand(img)
    }

    override fun read(filePath: String): Bitmap {
        val img = ImageMagick.NewMagickWand() ?: error("Failed to create new MagickWand")
        if (ImageMagick.MagickReadImage(img, filePath) == ImageMagick.MagickFalse) {
            val error = getMagickError(img)
            ImageMagick.DestroyMagickWand(img)
            error("Failed to read image from $filePath: $error")
        }
        val bitmap = MagickUtil.toBitmap(img)
        ImageMagick.DestroyMagickWand(img)
        return bitmap
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getMagickError(wand: CPointer<ImageMagick.MagickWand>?): String {
        require(wand != null) { "MagickWand is null" }

        return memScoped {
            val severity = alloc<ImageMagick.ExceptionTypeVar>()
            val messagePtr = ImageMagick.MagickGetException(wand, severity.ptr)

            if (messagePtr != null) {
                val errorMessage = messagePtr.toKString()
                ImageMagick.MagickRelinquishMemory(messagePtr)
                "ImageMagick Error: $errorMessage"
            } else {
                "Unknown ImageMagick error"
            }
        }
    }
}