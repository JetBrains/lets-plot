/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageData
import kotlin.js.ExperimentalWasmJsInterop

@JsFun("(arr, index, value) => { arr[index] = value; }")
private external fun setClampedArrayValue(arr: Uint8ClampedArray, index: Int, value: Int)

internal object BitmapUtil {
    fun fromHTMLCanvasElement(image: HTMLCanvasElement): Bitmap {
        val ctx = image.getContext("2d") as CanvasRenderingContext2D
        val imageData = ctx.getImageData(0.0, 0.0, image.width.toDouble(), image.height.toDouble())
        val src = Uint8ClampedArray(imageData.data.buffer)

        val rgba = ByteArray(src.length)
        for (i in 0 until src.length) {
            rgba[i] = src[i]
        }

        return Bitmap.fromRGBABytes(imageData.width, imageData.height, rgba)
    }

    fun toHTMLCanvasElement(bitmap: Bitmap): HTMLCanvasElement {
        val size = Vector(bitmap.width, bitmap.height)
        val canvas = DomCanvas.createNativeCanvas(size, pixelRatio = 1.0)

        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D

        val data = Uint8ClampedArray(bitmap.width * bitmap.height * 4)

        var i = 0
        bitmap.argbInts.forEach { pixel ->
            val a = (pixel ushr 24) and 0xFF
            val r = (pixel ushr 16) and 0xFF
            val g = (pixel ushr 8) and 0xFF
            val b = pixel and 0xFF

            setClampedArrayValue(data, i++, r)
            setClampedArrayValue(data, i++, g)
            setClampedArrayValue(data, i++, b)
            setClampedArrayValue(data, i++, a)
        }

        val imageData = ImageData(data, bitmap.width, bitmap.height)
        ctx.putImageData(imageData, 0.0, 0.0)

        return canvas
    }
}
