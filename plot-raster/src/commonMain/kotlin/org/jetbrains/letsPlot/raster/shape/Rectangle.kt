/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas


internal class Rectangle : Figure() {
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)
    private val rect: DoubleRectangle by computedProp(Rectangle::x, Rectangle::y, Rectangle::width, Rectangle::height) {
        DoubleRectangle.XYWH(x, y, width, height)
    }

    override fun render(canvas: Canvas) {
        fillPaint?.let {
            applyPaint(it, canvas)
            canvas.context2d.fillRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }

        strokePaint?.let {
            applyPaint(it, canvas)
            canvas.context2d.strokeRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    override val localBounds: DoubleRectangle
        get() = DoubleRectangle.XYWH(x, y, width, height)
}