/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment

class JitterDodgePos(
    aesthetics: Aesthetics,
    groupCount: Int,
    width: Double?,
    jitterWidth: Double?,
    jitterHeight: Double?,
    seed: Long? = null
) :
    PositionAdjustment {
    private val myJitterPosHelper: PositionAdjustment
    private val myDodgePosHelper: PositionAdjustment

    init {
        myJitterPosHelper = JitterPos(jitterWidth, jitterHeight, seed)
        myDodgePosHelper = DodgePos(aesthetics, groupCount, width)
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val afterJitter = myJitterPosHelper.translate(v, p, ctx)
        return myDodgePosHelper.translate(afterJitter, p, ctx)
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.JITTER_DODGE.handlesGroups()
    }
}
