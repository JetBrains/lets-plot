/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.canvas

import MagickWand.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas

class MagickCanvas(
    val wand: CPointer<MagickWand>?,
    size: Vector,
    pixelRatio: Double,
) : ScaledCanvas(MagickContext2d(wand), size, pixelRatio) {

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun immidiateSnapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun dumpPixels(): String {
        val w = size.x.toInt()
        val h = size.y.toInt()

        val pixelData = ByteArray(w * h * 4) // RGBA 8-bit per channel
        memScoped {
            val byteBuffer = pixelData.refTo(0) // Pointer to ByteArray
            val success = MagickExportImagePixels(
                wand,
                0, 0, w.toULong(), h.toULong(),
                "RGBA", StorageType.CharPixel, byteBuffer
            )
            require(success == MagickTrue) { "Failed to export pixels" }

            val lines = pixelData.asSequence().windowed(4 * w.toInt(), 4 * h.toInt()).toList()
            val strLines = lines.map { line ->
                line.windowed(4, 4).joinToString { pixel ->
                    pixel.joinToString(separator = "") { it.toHexString() }
                }
            }
            return strLines.joinToString("\n")
        }
    }

    companion object {
        fun create(size: Vector): MagickCanvas {
            val wand = NewMagickWand()
            val background = NewPixelWand()
            PixelSetColor(background, "white")
            MagickNewImage(wand, size.x.toULong(), size.y.toULong(), background)

//            println("fillRect(wand=$wand, x=0.0, y=0.0, w=${size.x}, h=${size.y})")
//            memScoped {
//                val drawingWand: CPointer<DrawingWand>? = NewDrawingWand()
//                val pixelWand = NewPixelWand()
//                require(pixelWand != null) { "Failed to create PixelWand" }
//
//                // Set fill color (default to black, modify as needed)
//                PixelSetColor(pixelWand, "orange")
//
//                // Apply fill color to drawing
//                DrawSetFillColor(drawingWand, pixelWand)
//
//                // Draw the rectangle
//                DrawRectangle(drawingWand, 5.0, 5.0, 45.0, 45.0)
//
//                // Apply drawing to the MagickWand image
//                MagickDrawImage(wand, drawingWand)
//
//                DestroyPixelWand(pixelWand) // Cleanup
//            }
//
//            memScoped {
//                val drawingWand: CPointer<DrawingWand>? = NewDrawingWand()
//                val pixelWand = NewPixelWand()
//                require(pixelWand != null) { "Failed to create PixelWand" }
//
//                // Set fill color (default to black, modify as needed)
//                PixelSetColor(pixelWand, "black")
//
//                // Apply fill color to drawing
//                DrawSetFillColor(drawingWand, pixelWand)
//
//                // Draw the rectangle
//                DrawRectangle(drawingWand, 0.0, 0.0, 5.0, 5.0)
//
//                // Apply drawing to the MagickWand image
//                MagickDrawImage(wand, drawingWand)
//
//                DestroyPixelWand(pixelWand) // Cleanup
//            }

            return MagickCanvas(wand, size, 1.0)
        }
    }
}
