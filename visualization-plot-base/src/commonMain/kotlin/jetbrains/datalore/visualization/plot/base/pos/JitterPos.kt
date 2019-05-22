package jetbrains.datalore.visualization.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.GeomContext
import jetbrains.datalore.visualization.plot.base.render.PositionAdjustment
import kotlin.random.Random

internal class JitterPos(width: Double?, height: Double?) : PositionAdjustment {

    //uniform distribution
    private val myWidth: Double
    private val myHeight: Double

    init {
        myWidth = width ?: DEF_JITTER_WIDTH
        myHeight = height ?: DEF_JITTER_HEIGHT
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val x = (2 * Random.nextDouble() - 1) * myWidth * ctx.getResolution(Aes.X)
        val y = (2 * Random.nextDouble() - 1) * myHeight * ctx.getResolution(Aes.Y)
        return v.add(DoubleVector(x, y))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.JITTER.handlesGroups()
    }

    companion object {

        val DEF_JITTER_WIDTH = 0.4
        val DEF_JITTER_HEIGHT = 0.4
    }
}
