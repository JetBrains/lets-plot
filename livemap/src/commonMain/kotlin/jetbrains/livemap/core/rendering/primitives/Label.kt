/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.rendering.Alignment

class Label(override var origin: DoubleVector, private var text: Text) : RenderBox {
    private var frame: Frame? = null
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
        if (text.isDirty) {
            dimension = text.measureText(ctx) + DoubleVector(2 * padding, 2 * padding)

            rectangle.apply {
                rect = DoubleRectangle(DoubleVector.ZERO, this@Label.dimension)
                color = background
            }

            origin = alignment.calculatePosition(origin, dimension)

            text.origin = DoubleVector(padding, padding)
            frame = Frame.create(origin, rectangle, text)
        }

        frame?.render(ctx)
    }
}

operator fun DoubleVector.minus(doubleVector: DoubleVector): DoubleVector {
    return subtract(doubleVector)
}

operator fun DoubleVector.plus(doubleVector: DoubleVector): DoubleVector {
    return add(doubleVector)
}
