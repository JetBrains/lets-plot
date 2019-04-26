package jetbrains.datalore.visualization.plot.gog.core.render.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment
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
