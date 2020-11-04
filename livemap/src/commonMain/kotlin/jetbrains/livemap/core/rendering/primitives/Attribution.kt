/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.rendering.Alignment
import kotlin.math.max

class Attribution(override var origin: DoubleVector, private val texts: List<Text>) : RenderBox {
    override var dimension = DoubleVector.ZERO
    private val rectangle: Rectangle = Rectangle()
    private val alignment = Alignment()
    var padding: Double = 0.0
    var background: Color = Color.TRANSPARENT
    var horizontalAlignment
        set(value) {
            alignment.horizontal = value
        }
        get() = alignment.horizontal

    var verticalAlignment
        set(value) {
            alignment.vertical = value
        }
        get() = alignment.vertical

    override fun render(ctx: Context2d) {
        if (isDirty()) {
            texts.forEach {
                val dim = if (it.isDirty) it.measureText(ctx) else it.dimension

                it.origin = DoubleVector(
                    dimension.x + padding,
                    padding
                )

                dimension = DoubleVector(
                    x = dimension.x + dim.x,
                    y = max(dimension.y, dim.y)
                )
            }

            dimension = dimension.add(DoubleVector(padding * 2, padding * 2))

            origin = alignment.calculatePosition(origin, dimension)

            rectangle.apply {
                rect = DoubleRectangle(this@Attribution.origin, this@Attribution.dimension)
                color = background
            }

            texts.forEach {
                it.origin += origin
            }
        }

        ctx.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        rectangle.render(ctx)
        texts.forEach {
            renderPrimitive(ctx, it)
        }
    }

    private fun renderPrimitive(ctx: Context2d, primitive: RenderBox) {
        ctx.save()
        val origin = primitive.origin
        ctx.setTransform(1.0, 0.0, 0.0, 1.0, origin.x, origin.y)
        primitive.render(ctx)
        ctx.restore()
    }

    private fun isDirty(): Boolean {
        return texts.any { it.isDirty }
    }
}