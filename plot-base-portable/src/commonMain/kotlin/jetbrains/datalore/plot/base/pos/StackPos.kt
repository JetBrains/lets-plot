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
import jetbrains.datalore.plot.common.util.MutableDouble

internal class StackPos(aes: Aesthetics, vjust: Double?) : PositionAdjustment {

    private val myOffsetByIndex: Map<Int, Double> = mapIndexToOffset(aes, vjust ?: DEF_VJUST)

    private fun mapIndexToOffset(aes: Aesthetics, vjust: Double): Map<Int, Double> {
        val offsetByIndex = HashMap<Int, Double>()
        val negPosBaseByBin = HashMap<Double, Pair<MutableDouble, MutableDouble>>()
        for (i in 0 until aes.dataPointCount()) {
            val dataPoint = aes.dataPointAt(i)
            val x = dataPoint.x()
            if (SeriesUtil.isFinite(x)) {
                if (!negPosBaseByBin.containsKey(x)) {
                    negPosBaseByBin[x!!] = Pair(
                        MutableDouble(0.0),
                        MutableDouble(0.0)
                    )
                }

                val y = dataPoint.y()
                if (SeriesUtil.isFinite(y)) {
                    val pair = negPosBaseByBin[x]!!
                    val offset = if (y!! >= 0) {
                        pair.second.getAndAdd(y)
                    } else {
                        pair.first.getAndAdd(y)
                    }
                    offsetByIndex[i] = offset - y * (1 - vjust)
                }
            }
        }
        return offsetByIndex
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        return v.add(DoubleVector(0.0, myOffsetByIndex[p.index()]!!))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.STACK.handlesGroups()
    }

    companion object {
        private const val DEF_VJUST = 1.0
    }
}
