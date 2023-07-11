/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import kotlin.math.PI

class Circle : RenderBox() {
    private var center: DoubleVector = DoubleVector.ZERO

    var strokeColor: Color? by visualProp(null)
    var strokeWidth: Double? by visualProp(null)
    var fillColor: Color? by visualProp(null)

    protected override fun updateState() {
        center = dimension.mul(0.5)
    }

    protected override fun renderInternal(ctx: Context2d) {
        ctx.beginPath()
        ctx.arc(center.x, center.y, dimension.x / 2.0, 0.0, 2 * PI)
        fillColor?.let { ctx.setFillStyle(it) }
        ctx.fill()

        strokeWidth?.let { ctx.setLineWidth(it) }
        strokeColor?.let { ctx.setStrokeStyle(it) }
        ctx.stroke()
    }
}
