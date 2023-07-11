/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.vis.canvas.Context2d

class Rectangle : RenderBox() {
    var color: Color? by visualProp(null)

    protected override fun renderInternal(ctx: Context2d) {
        color?.let(ctx::setFillStyle)

        ctx.fillRect(
            origin.x,
            origin.y,
            dimension.x,
            dimension.y
        )
    }
}
