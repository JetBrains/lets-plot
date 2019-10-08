package jetbrains.datalore.visualization.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.base.*

internal class DodgePos(aesthetics: Aesthetics,
                        private val myGroupCount: Int,
                        private val myWidth: Double?) : PositionAdjustment {

    private val myDodgingNeeded: Boolean
    private fun isDodgingNeeded(aesthetics: Aesthetics): Boolean {
        // if for some X there are more then just 1 group, then dodging is needed
        val groupByX = HashMap<Double, Int?>()
        for (i in 0 until aesthetics.dataPointCount()) {
            val p = aesthetics.dataPointAt(i)
            if (p.defined(Aes.X)) {
                val x = p.x()!!
                val group = p.group()
                if (groupByX.containsKey(x)) {
                    if (groupByX[x] != group) {
                        // >1 group for this X
                        return true
                    }
                } else {
                    groupByX[x] = group
                }
            }
        }
        return false
    }

    init {
        myDodgingNeeded = isDodgingNeeded(aesthetics)
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        if (myDodgingNeeded) {
            val dataResolution = ctx.getResolution(Aes.X)
            val width = myWidth ?: p.width()

            if (!SeriesUtil.isFinite(width)) {
                return v
            }

            val slotIndex = p.group()!!
            val median = (myGroupCount - 1) / 2.0
            val xOffset = (slotIndex - median) * dataResolution * width!!
            val xCenter = p.x()!!
            val xScaler = 1.0 / myGroupCount

            val newX = (v.x + xOffset - xCenter) * xScaler + xCenter

            return DoubleVector(newX, v.y)
        }
        return v
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.DODGE.handlesGroups()
    }
}
