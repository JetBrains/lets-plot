/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.PI

class Arc : RenderBox() {
    private var center: DoubleVector = DoubleVector.ZERO
    var strokeColor: Color? by visualProp(null)
    var strokeWidth: Double? by visualProp(null)
    var angle: Double  by visualProp(PI / 2)
    var startAngle: Double  by visualProp(0.0)

    protected override fun updateState() {
        center = dimension.mul(0.5)
    }

    protected override fun renderInternal(ctx: Context2d) {
        ctx.beginPath()
        ctx.arc(center.x, center.y, dimension.x / 2.0, startAngle, startAngle + angle)

        strokeWidth?.let { ctx.setLineWidth(it) }
        strokeColor?.let { ctx.setStrokeStyle(it) }
        ctx.stroke()
    }
}