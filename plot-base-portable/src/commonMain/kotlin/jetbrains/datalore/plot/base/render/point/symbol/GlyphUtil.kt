/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.vis.svg.SvgPathData
import jetbrains.datalore.vis.svg.SvgPathDataBuilder

internal object GlyphUtil {
    fun buildPathData(xs: Collection<Double>, ys: Collection<Double>): SvgPathData {
        require(xs.size == ys.size) { "Sizes of X/Y collections must be equal" }

        if (xs.isEmpty()) {
            return SvgPathData.EMPTY
        }

        val builder = SvgPathDataBuilder(true)
            .moveTo(Iterables[xs, 0], Iterables[ys, 0])
            .interpolatePoints(xs, ys, SvgPathDataBuilder.Interpolation.LINEAR)
            .closePath()

        return builder.build()
    }
}
