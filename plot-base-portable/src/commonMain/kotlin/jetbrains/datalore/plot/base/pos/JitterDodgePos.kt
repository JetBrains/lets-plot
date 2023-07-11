/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment

class JitterDodgePos(aesthetics: Aesthetics, groupCount: Int, width: Double?, jitterWidth: Double?, jitterHeight: Double?) :
    PositionAdjustment {
    private val myJitterPosHelper: PositionAdjustment
    private val myDodgePosHelper: PositionAdjustment

    init {
        myJitterPosHelper = JitterPos(jitterWidth, jitterHeight)
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
