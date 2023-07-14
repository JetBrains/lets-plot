/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder

internal object GlyphUtil {
    fun buildPathData(xs: List<Double>, ys: List<Double>): SvgPathData {
        require(xs.size == ys.size) { "Sizes of X/Y collections must be equal" }

        if (xs.isEmpty()) {
            return SvgPathData.EMPTY
        }

        val builder = SvgPathDataBuilder(true)
            .moveTo(xs[0], ys[0])
            .interpolatePoints(xs, ys, SvgPathDataBuilder.Interpolation.LINEAR)
            .closePath()

        return builder.build()
    }
}
