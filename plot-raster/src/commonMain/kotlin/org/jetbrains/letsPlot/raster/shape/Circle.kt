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

        fillPaint
            ?.let { applyPaint(it, ctx) }
            ?: ctx.setFillStyle(null)

        strokePaint
            ?.let { applyPaint(it, ctx) }
            ?: ctx.setStrokeStyle(null)

        ctx.drawCircle(centerX.toDouble(), centerY.toDouble(), radius.toDouble())
    }

    override val localBounds: DoubleRectangle
        get() = DoubleRectangle.XYWH(
            centerX - radius,
            centerY - radius,
            radius * 2,
            radius * 2
        )
}
