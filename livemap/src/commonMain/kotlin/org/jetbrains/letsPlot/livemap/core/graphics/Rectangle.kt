/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.graphics

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d

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
