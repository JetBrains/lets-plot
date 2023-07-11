/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment

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
