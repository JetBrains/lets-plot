/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg

import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.encoding.RGBEncoder
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.setAlphaAt
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.setBlueAt
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.setGreenAt
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.setRedAt
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageData

class RGBEncoderDom : RGBEncoder {

    override fun toDataUrl(bitmap: Bitmap): String {
        val canvas: HTMLCanvasElement? = document.createElement("canvas") as HTMLCanvasElement?
        @Suppress("FoldInitializerAndIfToElvis")
        if (canvas == null) {
            throw IllegalStateException("Canvas is not supported.")
        }

        canvas.width = bitmap.width
        canvas.height = bitmap.height

        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val imageData = context.createImageData(bitmap.width.toDouble(), bitmap.height.toDouble())
        val dataArray = imageData.data

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                setRgb(x, y, bitmap.argbInts[y * bitmap.width + x], imageData, dataArray)
            }
        }

        context.putImageData(imageData, 0.0, 0.0)
        return canvas.toDataURL("image/png")
    }

    private fun setRgb(x: Int, y: Int, argbValue: Int, imageData: ImageData, imageDataArray: Uint8ClampedArray) {
        imageData.setAlphaAt(imageDataArray, (argbValue shr 24) and 0xff, x, y)
        imageData.setRedAt(imageDataArray, (argbValue shr 16) and 0xff, x, y)
        imageData.setGreenAt(imageDataArray, (argbValue shr 8) and 0xff, x, y)
        imageData.setBlueAt(imageDataArray, argbValue and 0xff, x, y)
    }
}