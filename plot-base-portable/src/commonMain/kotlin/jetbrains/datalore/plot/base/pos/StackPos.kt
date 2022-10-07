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
import jetbrains.datalore.plot.common.util.MutableDouble
import jetbrains.datalore.plot.common.data.SeriesUtil

internal abstract class StackPos(
    aes: Aesthetics,
    protected val myVJust: Double?
) : PositionAdjustment {

    private val myOffsetByIndex: Map<Int, Double>

    init {
        myOffsetByIndex = mapIndexToOffset(aes)
    }

    protected abstract fun mapIndexToOffset(aes: Aesthetics): Map<Int, Double>

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        return v.add(DoubleVector(0.0, myOffsetByIndex[p.index()]!!))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.STACK.handlesGroups()
    }

    private class SplitPositiveNegative internal constructor(aes: Aesthetics, vjust: Double?) : StackPos(aes, vjust) {

        override fun mapIndexToOffset(aes: Aesthetics): Map<Int, Double> {
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
                        offsetByIndex[i] = if (myVJust != null) {
                            offset - y * if (y >= 0) (1 - myVJust) else myVJust
                        } else {
                            offset
                        }
                    }
                }
            }

            return offsetByIndex
        }
    }

    private class SumPositiveNegative internal constructor(aes: Aesthetics) : StackPos(aes, null) {

        override fun mapIndexToOffset(aes: Aesthetics): Map<Int, Double> {
            val offsetByIndex = HashMap<Int, Double>()
            val baseByBin = HashMap<Double, MutableDouble>()
            for (i in 0 until aes.dataPointCount()) {
                val dataPointAes = aes.dataPointAt(i)
                val x = dataPointAes.x()!!
                if (SeriesUtil.isFinite(x)) {
                    if (!baseByBin.containsKey(x)) {
                        baseByBin[x] = MutableDouble(0.0)
                    }

                    val y = dataPointAes.y()!!
                    if (SeriesUtil.isFinite(y)) {
                        val base = baseByBin[x]!!
                        val offset = base.getAndAdd(y)
                        offsetByIndex[i] = offset
                    }
                }
            }

            return offsetByIndex
        }
    }

    companion object {
        fun splitPositiveNegative(aes: Aesthetics, vjust: Double?): PositionAdjustment {
            return SplitPositiveNegative(aes, vjust)
        }

        fun sumPositiveNegative(aes: Aesthetics): PositionAdjustment {
            return SumPositiveNegative(aes)
        }
    }
}
