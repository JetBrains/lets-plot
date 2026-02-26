/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.max

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
            groupCount = aesthetics.dataPoints().mapNotNull { p ->
                p.finiteOrNull(Aes.DODGE_START, Aes.DODGE_END)?.let { (gstart, gend) -> max(gstart, gend) }
            }.maxOrNull()?.toInt()?.let { it + 1 }
        }

        super.buildIntern(root, aesthetics, pos, coord, ctx)
    }

    override fun getLimits(p: DataPointAesthetics, ctx: GeomContext): Pair<Double, Double>? {
        val (x, gstart, gend) = p.finiteOrNull(Aes.X, Aes.DODGE_START, Aes.DODGE_END) ?: return null
        val resolution = ctx.getResolution(Aes.X)
        val xmin = computeDodgedPosition(x, gstart, groupCount ?: 1, dodgeWidth, resolution)
        val xmax = computeDodgedPosition(x, gend, groupCount ?: 1, dodgeWidth, resolution)
        return Pair(xmin, xmax)
    }

    companion object {
        const val HANDLES_GROUPS = BracketGeom.HANDLES_GROUPS

        // See DodgePos::translate()
        private fun computeDodgedPosition(x: Double, group: Double, groupCount: Int, dodgeWidth: Double, dataResolution: Double): Double {
            val median = (groupCount - 1) / 2.0
            val offset = (group - median) * dataResolution * dodgeWidth
            val scaler = 1.0 / groupCount
            return x + offset * scaler
        }
    }
}