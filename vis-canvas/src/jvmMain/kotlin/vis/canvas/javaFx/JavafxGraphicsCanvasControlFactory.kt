/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.javaFx

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.GraphicsCanvasControl
import jetbrains.datalore.vis.canvas.GraphicsCanvasControlFactory

class JavafxGraphicsCanvasControlFactory(private val myPixelRatio: Double) :
    GraphicsCanvasControlFactory {

    override fun create(size: Vector, repaint: Runnable): GraphicsCanvasControl {
        return JavafxGraphicsCanvasControl(size, repaint, myPixelRatio)
    }
}
