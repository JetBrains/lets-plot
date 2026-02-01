package org.jetbrains.letsPlot.commons.image

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Bitmap
import platform.posix.memcpy
import stb_image.stbi_failure_reason

@OptIn(ExperimentalForeignApi::class)
actual fun loadImage(bytes: ByteArray): Bitmap = memScoped {
    require(bytes.isNotEmpty()) { "Input image bytes are empty" }

    val w = alloc<IntVar>()
    val h = alloc<IntVar>()
    val comp = alloc<IntVar>()  // channels in file (out)

    // Pin the byte array so stb can safely read it
    val pinned = bytes.pin()
    try {
        val data: CPointer<UByteVar>? =
            stb_image.stbi_load_from_memory(
                pinned.addressOf(0).reinterpret(), // const stbi_uc*
                bytes.size,                        // len
                w.ptr,                             // int* x
                h.ptr,                             // int* y
                comp.ptr,                          // int* channels_in_file
                4                                  // req_comp (force RGBA)
            )

        if (data == null) {
            val reason = stbi_failure_reason()?.toKString() ?: "unknown"
            error("stbi_load_from_memory failed: $reason")
        }

        try {
            val width = w.value
            val height = h.value
            require(width > 0 && height > 0) { "Invalid image size: ${width}x${height}" }

            val size = width * height * 4
            val out = ByteArray(size)

            // Copy stb-owned memory into Kotlin ByteArray
            // data points to UByte pixels in RGBA order.
            out.usePinned { outPinned ->
                memcpy(outPinned.addressOf(0), data, size.convert())
            }

            Bitmap.fromRGBABytes(width, height, out)
        } finally {
            stb_image.stbi_image_free(data) // always free stb's buffer
        }
    } finally {
        pinned.unpin()
    }
}