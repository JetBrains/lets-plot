/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d


internal class Rectangle : Figure() {
    var x: Float by variableAttr(0f)
    var y: Float by variableAttr(0f)
    var width: Float by variableAttr(0f)
    var height: Float by variableAttr(0f)

    override fun render(ctx: Context2d) {
        fillPaint?.let {
            applyPaint(it, ctx)
            ctx.fillRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }

        strokePaint?.let {
            applyPaint(it, ctx)
            ctx.strokeRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        if (width <= 0 || height <= 0) {
            return DoubleRectangle.XYWH(x.toDouble(), y.toDouble(), 0.0, 0.0)
        }
        return DoubleRectangle.XYWH(x, y, width, height).inflate(strokeWidth / 2.0)
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(Rectangle::class)

        val XAttrSpec = CLASS.registerVariableAttr(Rectangle::x, affectsBBox = true)
        val YAttrSpec = CLASS.registerVariableAttr(Rectangle::y, affectsBBox = true)
        val WidthAttrSpec = CLASS.registerVariableAttr(Rectangle::width, affectsBBox = true)
        val HeightAttrSpec = CLASS.registerVariableAttr(Rectangle::height, affectsBBox = true)
    }
}