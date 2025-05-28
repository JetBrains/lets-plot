/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas


internal class Image : Element() {
    var preserveRatio: Boolean by visualProp(false)
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)
    var img: Canvas.Snapshot? by visualProp(null)

    override fun render(canvas: Canvas) {
        img?.let {
            if (preserveRatio) {
                canvas.context2d.drawImage(it, x.toDouble(), y.toDouble())
            } else {
                //canvas.drawImageRect(it, Rect.makeXYWH(x, y, width, height))
                println("Image rendering with preserveRatio=false is not supported in raster backend.")
            }
        }
    }

    override val localBounds: DoubleRectangle
        get() = DoubleRectangle.XYWH(x, y, width, height)

}
