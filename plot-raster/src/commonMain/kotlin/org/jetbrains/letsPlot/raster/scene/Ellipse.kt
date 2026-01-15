/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.PI

internal class Ellipse : Figure() {
    var centerX: Float by variableAttr(0f)
    var centerY: Float by variableAttr(0f)
    var radiusX: Float by variableAttr(0f)
    var radiusY: Float by variableAttr(0f)

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

    override fun calculateLocalBBox(): DoubleRectangle {
        if (radiusX <= 0 || radiusY <= 0) {
            return DoubleRectangle.XYWH(centerX.toDouble(), centerY.toDouble(), 0.0, 0.0)
        }

        return DoubleRectangle.XYWH(
            centerX - radiusX,
            centerY - radiusY,
            radiusX * 2,
            radiusY * 2
        ).inflate(strokeWidth / 2.0)
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(Ellipse::class)

        val CenterXAttrSpec = CLASS.registerVariableAttr(Ellipse::centerX, affectsBBox = true)
        val CenterYAttrSpec = CLASS.registerVariableAttr(Ellipse::centerY, affectsBBox = true)
        val RadiusXAttrSpec = CLASS.registerVariableAttr(Ellipse::radiusX, affectsBBox = true)
        val RadiusYAttrSpec = CLASS.registerVariableAttr(Ellipse::radiusY, affectsBBox = true)
    }
}
