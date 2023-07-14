/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment

internal class NudgePos(width: Double?, height: Double?) : PositionAdjustment {

    private val nudgeDist: DoubleVector = DoubleVector(
        width ?: DEF_NUDGE_WIDTH,
        height ?: DEF_NUDGE_HEIGHT
    )

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        return v.add(nudgeDist)
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.NUDGE.handlesGroups()
    }

    companion object {
        const val DEF_NUDGE_WIDTH = 0.0
        const val DEF_NUDGE_HEIGHT = 0.0
    }
}
