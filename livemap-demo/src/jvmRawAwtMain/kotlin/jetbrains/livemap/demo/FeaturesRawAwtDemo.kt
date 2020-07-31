/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel

class FeaturesRawAwtDemo {
    fun start() {
        val dim = Vector(800, 600)
        val panel = JPanel(null)
        val canvasControl = AwtCanvasControl(panel, dim, 1.0, AwtEventPeer(panel, Rectangle(Vector.ZERO, dim)))

        val canvas = canvasControl.createCanvas(dim)

        FeaturesDemoModel(dim.toDoubleVector()).show(canvasControl)

        canvasControl.addChild(canvas)

        val frame = JFrame()
        frame.add(panel)
        frame.size = Dimension(dim.x, dim.y)

        frame.isVisible = true
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FeaturesRawAwtDemo().start()
        }
    }
}