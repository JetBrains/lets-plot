/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Bitmap

object MagickUtil {
    private var countAllocations = false
    private val refCounter: MutableMap<String, Int> = mutableMapOf()
    private val tags = mutableMapOf<String, String>()
    private val refLog = mutableListOf<String>()
    private const val MEMORY_LOG_ENABLED = false

    fun startCountAllocations() {
        countAllocations = true
        refCounter.clear()
        refLog.clear()
    }

    fun stopCountAllocations(): Pair<Map<String, Int>, List<String>> {
        countAllocations = false
        val refCounterWithTags = mutableMapOf<String, Int>()
        for ((address, count) in refCounter) {
            val tag = tags[address]
            val key = if (tag != null) "$tag($address)" else address
            refCounterWithTags[key] = count
        }
        return Pair(refCounterWithTags.toMap(), refLog.toList()).also {
            refCounter.clear()
            refLog.clear()
        }
    }

    private fun incRefCount(str: String, ptr: CPointer<*>, tag: String? = null) {
        if (countAllocations) {
            refLog += "$str: $ptr" + if (tag != null) " [$tag]" else ""

            val address = ptr.toString()
            refCounter[address] = refCounter.getOrElse(address) { 0 } + 1
            if (tag != null) {
                tags[ptr.toString()] = tag
            }
        }
    }

    private fun decRefCount(str: String, ptr: CPointer<*>) {
        if (countAllocations) {
            refLog += "$str: $ptr"

            val address = ptr.toString()
            when (val counter = refCounter[address]) {
                null -> println("Warning: Reference count for pointer $ptr is not tracked")
                1 ->  {
                    refCounter.remove(address)
                    val tag = tags.remove(address)
                }
                else -> refCounter[address] = counter - 1
            }
        }
    }

    private fun log(message: () -> String) {
        if (MEMORY_LOG_ENABLED) {
            println(message())
        }
    }

    fun getException(wand: CPointer<ImageMagick.MagickWand>): String {
        val errPtr = ImageMagick.MagickGetException(wand, null)
        val errMsg = errPtr?.toKString() ?: "Unknown error"

        if (errPtr != null) {
            ImageMagick.MagickRelinquishMemory(errPtr)
        }
        return errMsg
    }

    fun newPixelWand(tag: String? = null): CPointer<ImageMagick.PixelWand> {
        val pixelWand = ImageMagick.NewPixelWand() ?: throw RuntimeException("Failed to create new PixelWand")
        incRefCount("newPixelWand()", pixelWand, tag)
        log { "newPixelWand(): $pixelWand" }
        return pixelWand
    }

    fun destroyPixelWand(pixelWand: CPointer<ImageMagick.PixelWand>) {
        decRefCount("destroyPixelWand()", pixelWand)
        log { "destroyPixelWand(): $pixelWand" }
        ImageMagick.DestroyPixelWand(pixelWand)
    }

    fun newMagickWand(tag: String? = null): CPointer<ImageMagick.MagickWand> {
        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create new MagickWand")
        incRefCount("newMagickWand()", magickWand, tag)
        log { "newMagickWand(): $magickWand" }
        return magickWand
    }

    fun cloneMagickWand(magickWand: CPointer<ImageMagick.MagickWand>, tag: String? = null): CPointer<ImageMagick.MagickWand> {
        val clonedWand = ImageMagick.CloneMagickWand(magickWand) ?: throw RuntimeException("Failed to clone MagickWand")
        incRefCount("cloneMagickWand()", clonedWand, tag)
        log { "cloneMagickWand(): $clonedWand" }
        return clonedWand
    }

    fun destroyMagickWand(magickWand: CPointer<ImageMagick.MagickWand>) {
        decRefCount("destroyMagickWand()", magickWand)
        log { "destroyMagickWand(): $magickWand" }
        ImageMagick.DestroyMagickWand(magickWand)
    }

    fun newDrawingWand(tag: String? = null): CPointer<ImageMagick.DrawingWand> {
        val drawingWand = ImageMagick.NewDrawingWand() ?: throw RuntimeException("Failed to create new DrawingWand")
        incRefCount("newDrawingWand()", drawingWand, tag)
        log { "newDrawingWand(): $drawingWand" }
        return drawingWand
    }

    fun destroyDrawingWand(drawingWand: CPointer<ImageMagick.DrawingWand>) {
        decRefCount("destroyDrawingWand()", drawingWand)
        log { "destroyDrawingWand(): $drawingWand" }
        ImageMagick.DestroyDrawingWand(drawingWand)
    }

    fun fromBitmap(bitmap: Bitmap): CPointer<ImageMagick.MagickWand> {
        val w = bitmap.width
        val h = bitmap.height
        val rgba = bitmap.rgbaBytes()
        val img = newMagickWand("fromBitmap.img")
        val backgroundPixel = newPixelWand("fromBitmap.backgroundPixel")
        ImageMagick.PixelSetColor(backgroundPixel, "transparent")

        memScoped {
            val status = ImageMagick.MagickNewImage(
                img,
                w.toULong(),
                h.toULong(),
                backgroundPixel
            )
            if (status == ImageMagick.MagickFalse) {
                val err = ImageMagick.MagickGetException(img, null)
                ImageMagick.DestroyMagickWand(img)
                throw RuntimeException("Failed to create new image: $err")
            }

            ImageMagick.MagickSetImageAlphaChannel(img, ImageMagick.AlphaChannelOption.SetAlphaChannel)

            // Set pixels
            ImageMagick.MagickImportImagePixels(
                img, 0, 0, w.convert(), h.convert(),
                "RGBA",
                ImageMagick.StorageType.CharPixel,
                rgba.refTo(0)
            )
            destroyPixelWand(backgroundPixel)

            return img
        }
    }

    fun toBitmap(wand: CPointer<ImageMagick.MagickWand>): Bitmap {
        val width = ImageMagick.MagickGetImageWidth(wand).toInt()
        val height = ImageMagick.MagickGetImageHeight(wand).toInt()
        val pixels = ByteArray(width * height * 4) // RGBA
        val success = ImageMagick.MagickExportImagePixels(
            wand, 0, 0, width.toULong(), height.toULong(),
            "RGBA",
            ImageMagick.StorageType.CharPixel,
            pixels.refTo(0)
        )
        if (success == ImageMagick.MagickFalse) error("Failed to export pixels")

        return Bitmap.fromRGBABytes(w = width, h = height, rgba = pixels)
    }


    // ImageMagick Severity Thresholds
    // 0..299 = Undefined / Success
    // 300..399 = Warnings (Ignore these)
    // 400..699 = Errors (Crash on these)
    // 700+     = Fatal Errors
    private const val ERROR_THRESHOLD: UInt = 1u//ImageMagick.ExceptionType.ErrorException

    /**
     * Checks if a pointer is null (allocation failure) or has internal errors.
     */
    fun CPointer<ImageMagick.MagickWand>?.checkError(message: String = "MagickWand Error") {
        if (this == null) throw RuntimeException("$message: Pointer is NULL (Allocation failed)")

        memScoped {
            val severity = alloc<ImageMagick.ExceptionTypeVar>()
            val descriptionPtr = ImageMagick.MagickGetException(this@checkError, severity.ptr)
            handleError(descriptionPtr, severity.value, message)
        }
    }

    fun CPointer<ImageMagick.DrawingWand>?.checkError(message: String = "DrawingWand Error") {
        if (this == null) throw RuntimeException("$message: Pointer is NULL (Allocation failed)")

        memScoped {
            val severity = alloc<ImageMagick.ExceptionTypeVar>()
            val descriptionPtr = ImageMagick.DrawGetException(this@checkError, severity.ptr)
            handleError(descriptionPtr, severity.value, message)
        }
    }

    fun CPointer<ImageMagick.PixelWand>?.checkError(message: String = "PixelWand Error") {
        if (this == null) throw RuntimeException("$message: Pointer is NULL (Allocation failed)")

        memScoped {
            val severity = alloc<ImageMagick.ExceptionTypeVar>()
            val descriptionPtr = ImageMagick.PixelGetException(this@checkError, severity.ptr)
            handleError(descriptionPtr, severity.value, message)
        }
    }

    /**
     * Shared logic to process the C-String description and throw Exception
     */
    private fun handleError(descriptionPtr: CPointer<ByteVar>?, severity: UInt, contextMessage: String) {
        if (severity >= ERROR_THRESHOLD) {
            val details = descriptionPtr?.toKString() ?: "Unknown Error"

            // IMPORTANT: ImageMagick allocates memory for the description. We must free it.
            if (descriptionPtr != null) {
                ImageMagick.MagickRelinquishMemory(descriptionPtr)
            }

            // Crash explicitly so we see the stack trace
            throw RuntimeException("$contextMessage. Severity: $severity. Details: $details")
        }

        // If it was just a warning, strictly clean up memory and continue
        if (descriptionPtr != null) {
            ImageMagick.MagickRelinquishMemory(descriptionPtr)
        }
    }
}
