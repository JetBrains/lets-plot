/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class FillPos(aes: Aesthetics, vjust: Double?) : PositionAdjustment {

    private val myStackPosHelper: PositionAdjustment = StackPos(aes, vjust)
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
        return DoubleVector(newLoc.x, newLoc.y * myScalerByIndex.getValue(p.index()))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.FILL.handlesGroups()
    }
}
