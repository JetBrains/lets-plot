package jetbrains.datalore.visualization.plot.gog.core.render.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment

internal class NudgePos(width: Double?, height: Double?) : PositionAdjustment {

    private val myWidth: Double
    private val myHeight: Double

    init {
        myWidth = width ?: DEF_NUDGE_WIDTH
        myHeight = height ?: DEF_NUDGE_HEIGHT
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        //ToDo: getResolution needs to be changed later. In R, the points move in another way when data is continuous.
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
