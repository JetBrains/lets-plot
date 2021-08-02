/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.awt.AwtAnimationTimerPeer
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel

class FeaturesRawAwtDemo {
    fun start() {
        val canvasSize = Dimension(800, 600)
        val canvasContainer = JPanel(null).apply {
            size = canvasSize
        }

        val canvasControl = AwtCanvasControl(
            canvasSize.toVector(),
            AwtEventPeer(canvasContainer, Rectangle(Vector.ZERO, canvasSize.toVector())),
            AwtAnimationTimerPeer()
        )

        FeaturesDemoModel(canvasSize.toDoubleVector()).show(canvasControl)

        JFrame().apply {
            size = canvasSize
            isVisible = true
            defaultCloseOperation = EXIT_ON_CLOSE
            add(canvasContainer)
        }
        canvasContainer.add(canvasControl.component())
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