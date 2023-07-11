/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.Context2d
import kotlin.math.max

class Frame : RenderBox() {
    var children: List<RenderBox> by visualProp(emptyList())

    override var dimension
        get() = calculateDimension()
        set(_) {
            throw IllegalStateException("Frame calculates size automatically")
        }

    protected override fun renderInternal(ctx: Context2d) {
        children.forEach { primitive ->
            ctx.save()
            val origin = primitive.origin
            ctx.translate(origin.x, origin.y)
            primitive.render(ctx)
            ctx.restore()
        }
    }

    private fun calculateDimension(): DoubleVector {
        var right = getRight(children[0])
        var bottom = getBottom(children[0])

        for (renderBox in children) {
            right = max(right, getRight(renderBox))
            bottom = max(bottom, getBottom(renderBox))
        }

        return DoubleVector(right, bottom)
    }

    private fun getRight(renderObject: RenderBox): Double {
        return renderObject.origin.x + children[0].dimension.x
    }

    private fun getBottom(renderObject: RenderBox): Double {
        return renderObject.origin.y + children[0].dimension.y
    }
}
