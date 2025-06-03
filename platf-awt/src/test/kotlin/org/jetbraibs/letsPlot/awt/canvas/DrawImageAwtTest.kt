/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbraibs.letsPlot.awt.canvas

import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvas
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtContext2d
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Color
import org.junit.Test

class DrawImageAwtTest {
    private val w = 100.0
    private val h = 100.0

    private val strokeColor = "#000000"
    private val fillColor = "#000000"
    private val filledStrokeColor = "#000080"
    private val strokedFillColor = "#FFC000"

    private fun createCanvas(width: Number = w, height: Number = h, pixelDensity: Double = 1.0): Pair<AwtCanvas, AwtContext2d> {
        val canvas = AwtCanvas.create(pixelDensity = pixelDensity, size = Vector(width.toInt(), height.toInt()))
        val context2d = canvas.context2d as AwtContext2d
        return canvas to context2d
    }


    @Test
    fun drawImage_WithImageDataUrl() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.setFillStyle(Color.BLACK)
        // draw star
        tempCtx.beginPath()
        tempCtx.moveTo(50.0, 10.0)
        tempCtx.lineTo(61.8, 35.4)
        tempCtx.lineTo(90.0, 35.4)
        tempCtx.lineTo(67.1, 57.6)
        tempCtx.lineTo(79.5, 82.0)
        tempCtx.lineTo(50.0, 65.0)
        tempCtx.lineTo(20.5, 82.0)
        tempCtx.lineTo(32.9, 57.6)
        tempCtx.lineTo(10.0, 35.4)
        tempCtx.lineTo(38.2, 35.4)
        tempCtx.closePath()
        tempCtx.fill()

        val imageDataUrl = tempCanvas.immidiateSnapshot().toDataUrl()

        println("ImageDaataURL:\n$imageDataUrl")

        val snapshot = AwtCanvasControl(Vector(100, 100), AwtAnimationTimerPeer({_ ->}), MouseEventPeer()).immediateSnapshot(imageDataUrl)

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot)

    }
}