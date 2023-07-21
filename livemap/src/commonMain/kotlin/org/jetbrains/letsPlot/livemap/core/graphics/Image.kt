/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.graphics

import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot
import org.jetbrains.letsPlot.core.canvas.Context2d

class Image : RenderBox() {
    var snapshot: Snapshot? by visualProp(null)

    protected override fun renderInternal(ctx: Context2d) {
        snapshot?.let { ctx.drawImage(it, 0.0, 0.0, dimension.x, dimension.y) }
    }
}
