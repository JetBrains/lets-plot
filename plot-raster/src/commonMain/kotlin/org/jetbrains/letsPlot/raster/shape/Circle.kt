/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d


internal class Circle : Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radius: Float by visualProp(0.0f)

    override fun render(ctx: Context2d) {
        if (fillPaint == null && strokePaint == null) {
            return
        }

        fillPaint?.let { applyPaint(it, ctx) }
        strokePaint?.let { applyPaint(it, ctx) }
        ctx.circle(centerX.toDouble(), centerY.toDouble(), radius.toDouble())

        //ctx.beginPath()
        //ctx.arc(centerX.toDouble(), centerY.toDouble(), radius.toDouble(), 0.0, 2* PI)
        //ctx.closePath()

        //ctx.fillAndStroke(fillPaint, strokePaint)
        //fillPaint?.let { ctx.fill(it) }
        //strokePaint?.let { ctx.stroke(it) }
    }

    override val localBounds: DoubleRectangle
        get() = DoubleRectangle.XYWH(
            centerX - radius,
            centerY - radius,
            radius * 2,
            radius * 2
        )
}
