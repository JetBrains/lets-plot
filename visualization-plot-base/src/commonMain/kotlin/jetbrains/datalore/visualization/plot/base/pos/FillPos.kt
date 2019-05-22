package jetbrains.datalore.visualization.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.GeomContext
import jetbrains.datalore.visualization.plot.base.render.PositionAdjustment
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

internal class FillPos(aes: Aesthetics) : PositionAdjustment {

    private val myStackPosHelper: PositionAdjustment = StackPos.splitPositiveNegative(aes)
    private val myScalerByIndex: Map<Int, Double> = mapIndexToScaler(aes)

    private fun mapIndexToScaler(aes: Aesthetics): Map<Int, Double> {
        val posMaxByBin = HashMap<Double, Double>()
        val negMaxByBin = HashMap<Double, Double>()
        for (i in 0 until aes.dataPointCount()) {
            val dataPoint = aes.dataPointAt(i)
            val x = dataPoint.x()
            if (SeriesUtil.isFinite(x)) {
                if (!posMaxByBin.containsKey(x)) {
                    posMaxByBin[x!!] = 0.0
                    negMaxByBin[x] = 0.0
                }

                val y = dataPoint.y()
                if (SeriesUtil.isFinite(y)) {
                    if (y!! >= 0) {
                        posMaxByBin[x!!] = posMaxByBin[x]!! + y
                    } else {
                        negMaxByBin[x!!] = negMaxByBin[x]!! - y
                    }
                }
            }
        }
        val scalerByIndex = HashMap<Int, Double>()
        // Double max = max(Collections.max(posMaxByBin.values()), Collections.max(negMaxByBin.values()));
        for (i in 0 until aes.dataPointCount()) {
            val dataPoint = aes.dataPointAt(i)
            val x = dataPoint.x()
            val y = dataPoint.y()
            if (posMaxByBin.containsKey(x) && SeriesUtil.isFinite(y)) {
                if (y!! >= 0 && posMaxByBin[x]!! > 0) {
                    scalerByIndex[i] = 1.0 / posMaxByBin[x]!!
                } else if (y < 0 && negMaxByBin[x]!! > 0) {
                    scalerByIndex[i] = 1.0 / negMaxByBin[x]!!
                } else {
                    scalerByIndex[i] = 1.0
                }
            } else {
                scalerByIndex[i] = 1.0
            }
        }
        return scalerByIndex
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val newLoc = myStackPosHelper.translate(v, p, ctx)
        return DoubleVector(newLoc.x, newLoc.y * myScalerByIndex[p.index()]!! * ctx.getUnitResolution(Aes.Y))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.FILL.handlesGroups()
    }
}
