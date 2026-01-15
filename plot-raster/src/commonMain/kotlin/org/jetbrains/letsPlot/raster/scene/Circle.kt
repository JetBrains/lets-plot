/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d

internal class Circle : Figure() {
    var centerX: Float by variableAttr(0f)
    var centerY: Float by variableAttr(0f)
    var radius: Float by variableAttr(0f)

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

    override fun calculateLocalBBox(): DoubleRectangle {
        if (radius <= 0) {
            return DoubleRectangle.XYWH(centerX.toDouble(), centerY.toDouble(), 0.0, 0.0)
        }

        return DoubleRectangle.XYWH(
            centerX - radius,
            centerY - radius,
            radius * 2,
            radius * 2
        ).inflate(strokeWidth / 2.0)
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(Circle::class)

        val CenterXAttrSpec = CLASS.registerVariableAttr(Circle::centerX, affectsBBox = true)
        val CenterYAttrSpec = CLASS.registerVariableAttr(Circle::centerY, affectsBBox = true)
        val RadiusAttrSpec = CLASS.registerVariableAttr(Circle::radius, affectsBBox = true)
    }
}
