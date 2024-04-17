/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.WorldPoint

class CurveRenderer : PathRenderer() {
    override fun drawPath(points: List<WorldPoint>, ctx: Context2d) {
        if (points.size < 3) {
            // linear
            super.drawPath(points, ctx)
        } else {
            ctx.drawBezierCurve(points)
        }
    }
}