/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d

class Rectangle : RenderBox {
    override val origin get() = rect.origin
    override val dimension get() = rect.dimension

    var rect: DoubleRectangle = DoubleRectangle(0.0, 0.0, 0.0, 0.0)
    var color: Color? = null

    override fun render(ctx: Context2d) {
        color?.let { ctx.setFillStyle(it) }

        ctx.fillRect(
            rect.left,
            rect.top,
            rect.width,
            rect.height
        )
    }

}