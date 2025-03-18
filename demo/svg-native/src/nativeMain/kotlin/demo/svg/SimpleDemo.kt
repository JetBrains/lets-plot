/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.nat.canvas.MagickCanvas

object SimpleDemo {
    @OptIn(ExperimentalForeignApi::class)
    fun main() {
        val width = 50.0
        val height = 50.0
        val canvas = MagickCanvas.create(Vector(width.toInt(), height.toInt()))
        val ctx = canvas.context2d

        val innerWidth = width / 2.0
        val innerHeight = height / 2.0
        ctx.setFillStyle(Color.RED)
        ctx.fillRect((width - innerWidth / 2.0), (height - innerHeight) / 2.0, innerWidth, innerHeight)

        ctx.setStrokeStyle(Color.ORANGE)
        ctx.moveTo(0.0, 0.0)
        ctx.lineTo(width, height)
        ctx.moveTo(0.0, height)
        ctx.lineTo(width, 0.0)
        ctx.stroke()


        val str = canvas.dumpPixels()
        println("Simple demo")
        println(str)
    }
}
