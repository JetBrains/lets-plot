/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.pos.BaseDodgePos
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.max
import kotlin.math.roundToInt

class BracketDodgeGeom : BracketGeom() {
    var groupCount: Int? = null
    var dodgeWidth: Double = DEF_DODGE_WIDTH

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        if (groupCount == null) {
            val maxIndex = aesthetics.dataPoints().mapNotNull { p ->
                p.finiteOrNull(Aes.ISTART, Aes.IEND)
            }.maxOfOrNull { (startIndex, endIndex) ->
                max(startIndex, endIndex)
            }
            if (maxIndex != null) {
                groupCount = maxIndex.roundToInt() + 1
            }
        }

        super.buildIntern(root, aesthetics, pos, coord, ctx)
    }

    override fun getLimits(p: DataPointAesthetics, ctx: GeomContext): Pair<Double, Double>? {
        val (x, startIndex, endIndex) = p.finiteOrNull(Aes.X, Aes.ISTART, Aes.IEND) ?: return null
        val resolution = ctx.getResolution(Aes.X)
        if (groupCount == null || !SeriesUtil.isFinite(dodgeWidth)) return null
        val xmin = BaseDodgePos.position(x, startIndex.roundToInt(), x, groupCount!!, dodgeWidth, resolution)
        val xmax = BaseDodgePos.position(x, endIndex.roundToInt(), x, groupCount!!, dodgeWidth, resolution)
        return Pair(xmin, xmax)
    }

    companion object {
        const val HANDLES_GROUPS = BracketGeom.HANDLES_GROUPS
    }
}