/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas

object SimpleMagickCanvasDemo {
    @OptIn(ExperimentalForeignApi::class)
    fun main() {
        ImageMagick.MagickWandGenesis()
        val width = 50.0
        val height = 50.0
        val canvas = MagickCanvas.create(Vector(width.toInt(), height.toInt()))
        val ctx = canvas.context2d

        ctx.setFillStyle(Color.RED)
        ctx.fillRect(5.0, 5.0, 40.0, 40.0)
        ctx.setFillStyle(Color.ORANGE)
        ctx.fillText("Hello!", 10.0, 20.0)

        ctx.setStrokeStyle(Color.ORANGE)
        ctx.moveTo(0.0, 0.0)
        ctx.lineTo(width, height)
        ctx.moveTo(0.0, height)
        ctx.lineTo(width, 0.0)
        ctx.stroke()

        val str = canvas.dumpPixels()

        // Save the image to a file
        val outputFilename = "simple_demo.bmp"
        if (ImageMagick.MagickWriteImage(canvas.wand, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }

        ImageMagick.MagickWandTerminus()

        println("Simple demo")
        println(str)
    }
}
