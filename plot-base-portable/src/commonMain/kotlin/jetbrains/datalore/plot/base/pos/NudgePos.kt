/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment

internal class NudgePos(width: Double?, height: Double?) : PositionAdjustment {

    private val myWidth: Double
    private val myHeight: Double

    init {
        myWidth = width ?: DEF_NUDGE_WIDTH
        myHeight = height ?: DEF_NUDGE_HEIGHT
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val x = myWidth * ctx.getUnitResolution(Aes.X)
        val y = myHeight * ctx.getUnitResolution(Aes.Y)
        return v.add(DoubleVector(x, y))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.NUDGE.handlesGroups()
    }

    companion object {
        val DEF_NUDGE_WIDTH = 0.0
        val DEF_NUDGE_HEIGHT = 0.0
    }
}
