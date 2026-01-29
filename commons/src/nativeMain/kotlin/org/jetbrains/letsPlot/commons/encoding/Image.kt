@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package org.jetbrains.letsPlot.commons.encoding

import kotlinx.cinterop.*
import stb_image.stbi_failure_reason
import stb_image.stbi_image_free
import stb_image.stbi_load
import kotlin.experimental.ExperimentalNativeApi

fun readImage(path: String) {
    memScoped {
        val width = alloc<IntVar>()
        val height = alloc<IntVar>()
        val channels = alloc<IntVar>()

        // Returns a pointer to the pixel array (R, G, B, A...)
        val imagePtr = stbi_load(path, width.ptr, height.ptr, channels.ptr, 0)

        if (imagePtr != null) {
            println("Platform: ${Platform.osFamily}")
            println("Image: ${width.value}x${height.value}, Channels: ${channels.value}")

            // Do work with imagePtr[index]...

            stbi_image_free(imagePtr)
        } else {
            println("Failed to load: ${stbi_failure_reason()?.toKString()}")
        }
    }
}