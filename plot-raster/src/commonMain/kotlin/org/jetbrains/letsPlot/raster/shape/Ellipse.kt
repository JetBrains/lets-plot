/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.PI

internal class Ellipse : Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radiusX: Float by visualProp(0.0f)
    var radiusY: Float by visualProp(0.0f)

    override fun render(ctx: Context2d) {
        if (fillPaint == null && strokePaint == null) {
            return
        }

        ctx.beginPath()
        ctx.ellipse(centerX.toDouble(), centerY.toDouble(), radiusX.toDouble(), radiusY.toDouble(), 0.0, 0.0, 2 * PI, false)
        ctx.closePath()

        fillPaint?.let { ctx.fill(it) }
        strokePaint?.let { ctx.stroke(it) }
    }

    override val bBoxLocal: DoubleRectangle
        get() {
            return DoubleRectangle.XYWH(
                centerX - radiusX,
                centerY - radiusY,
                radiusX * 2,
                radiusY * 2
            )
        }

    override val bBoxGlobal: DoubleRectangle
        get() {
            if (radiusX <= 0 || radiusY <= 0) {
                return DoubleRectangle.XYWH(centerX.toDouble(), centerY.toDouble(), 0.0, 0.0)
            }

            return ctm.transform(bBoxLocal.inflate(strokeWidth / 2.0))
        }
}
