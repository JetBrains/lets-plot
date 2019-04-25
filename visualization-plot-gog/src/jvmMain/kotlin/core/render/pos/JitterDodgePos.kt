package jetbrains.datalore.visualization.plot.gog.core.render.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment

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
