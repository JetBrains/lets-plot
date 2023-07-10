/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector

class SingleCanvasControl(private val myCanvasControl: CanvasControl) {
    val canvas: Canvas

    val context: Context2d
        get() = canvas.context2d

    val size: Vector
        get() = myCanvasControl.size

    init {
        canvas = myCanvasControl.createCanvas(myCanvasControl.size)
        myCanvasControl.addChild(canvas)
    }

    fun createCanvas(): Canvas {
        return myCanvasControl.createCanvas(myCanvasControl.size)
    }

    fun dispose() {
        myCanvasControl.removeChild(canvas)
    }
}
