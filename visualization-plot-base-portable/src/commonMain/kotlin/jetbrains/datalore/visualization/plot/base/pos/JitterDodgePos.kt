package jetbrains.datalore.visualization.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.GeomContext
import jetbrains.datalore.visualization.plot.base.PositionAdjustment

class JitterDodgePos(aesthetics: Aesthetics, groupCount: Int, width: Double?, jitterWidth: Double?, jitterHeight: Double?) : PositionAdjustment {
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
