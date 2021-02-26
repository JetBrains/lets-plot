/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import kotlin.math.max


class Text : RenderBox {
    override var origin = DoubleVector.ZERO
    override var dimension = DoubleVector.ZERO

    var text: Collection<String> = emptyList()
        set(value) {
            field = value
            isDirty = true
        }
    var color: Color = Color.WHITE
    var isDirty = true; private set
    var fontSize: Double = 10.0
    var fontFamily = "serif"

    override fun render(ctx: Context2d) {
        ctx.setFont(Context2d.Font(fontSize = fontSize, fontFamily = fontFamily))
        ctx.setTextBaseline(Context2d.TextBaseline.BOTTOM)

        if (isDirty) {
            dimension = calculateDimension(ctx)
            isDirty = false
        }

        ctx.setFillStyle(color)

        var y = fontSize
        for (s in text) {
            ctx.fillText(s, 0.0, y)
            y += fontSize
        }
    }

    fun measureText(ctx: Context2d): DoubleVector {
        if (isDirty) {
            ctx.save()
            ctx.setFont(Context2d.Font(fontSize = fontSize, fontFamily = fontFamily))
            ctx.setTextBaseline(Context2d.TextBaseline.BOTTOM)

            dimension = calculateDimension(ctx)
            isDirty = false
            ctx.restore()
        }

        return dimension
    }

    private fun calculateDimension(ctx: Context2d): DoubleVector {
        var maxWidth = 0.0
        for (s in text) {
            maxWidth = max(maxWidth, ctx.measureText(s))
        }

        return DoubleVector(maxWidth, text.size * fontSize)
    }
}