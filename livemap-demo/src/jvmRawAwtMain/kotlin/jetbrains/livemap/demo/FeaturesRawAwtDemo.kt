/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.awt.AwtTimerPeer
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel

class FeaturesRawAwtDemo {
    fun start() {
        val canvasSize = Dimension(800, 600)
        val canvasContainer = JPanel(null).apply {
            size = canvasSize
        }

        JFrame().apply {
            size = canvasSize
            isVisible = true
            add(canvasContainer)
        }

        val canvasControl = AwtCanvasControl(
            root = canvasContainer,
            size = canvasSize.toVector(),
            myPixelRatio = 1.0,
            myEventPeer = AwtEventPeer(canvasContainer, Rectangle(Vector.ZERO, canvasSize.toVector())),
            myTimerPeer = AwtTimerPeer()
        )

        val canvas = canvasControl.createCanvas(canvasSize.toVector())
        canvasControl.addChild(canvas)
        FeaturesDemoModel(canvasSize.toDoubleVector()).show(canvasControl)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FeaturesRawAwtDemo().start()
        }
    }
}

fun Dimension.toVector() = Vector(width, height)
fun Dimension.toDoubleVector() = DoubleVector(width.toDouble(), height.toDouble())