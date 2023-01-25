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
import kotlin.math.*

internal class StackPos(aes: Aesthetics, vjust: Double?) : PositionAdjustment {

    private val myOffsetByIndex: Map<Int, Double> = mapIndexToOffset(aes, vjust ?: DEF_VJUST, DEF_STACK_SIZE_CALCULATOR)

    private fun mapIndexToOffset(aes: Aesthetics, vjust: Double, calculator: StackSizeCalculator): Map<Int, Double> {
        val offsetByIndex = HashMap<Int, Double>()
        val negPosBaseByBin = HashMap<Double, Pair<Double, Double>>()
        aes.dataPoints().asSequence()
            .mapIndexed { i, p -> Pair(i, p) }
            .filter { SeriesUtil.allFinite(it.second.x(), it.second.y()) }
            .groupBy { it.second.group() }
            .forEach { (_, indexedDataPoints) ->
                val negPosBaseByGroupBin = HashMap<Double, Pair<Double, Double>>()
                for ((i, dataPoint) in indexedDataPoints) {
                    val x = dataPoint.x()!!
                    if (!negPosBaseByGroupBin.containsKey(x)) {
                        negPosBaseByGroupBin[x] = Pair(0.0, 0.0)
                    }
                    if (!negPosBaseByBin.containsKey(x)) {
                        negPosBaseByBin[x] = Pair(0.0, 0.0)
                    }

                    val y = dataPoint.y()!!
                    val groupPair = negPosBaseByGroupBin[x]!!
                    val pair = negPosBaseByBin[x]!!
                    val offset = if (y >= 0) {
                        negPosBaseByGroupBin[x] = Pair(groupPair.first, calculator.reduceGroupSize(groupPair.second, y))
                        calculator.calculateStackSize(pair.second, groupPair.second)
                    } else {
                        negPosBaseByGroupBin[x] = Pair(calculator.reduceGroupSize(groupPair.first, y), groupPair.second)
                        calculator.calculateStackSize(pair.first, groupPair.first)
                    }
                    offsetByIndex[i] = offset - y * (1 - vjust)
                }
                for ((x, groupPair) in negPosBaseByGroupBin) {
                    val pair = negPosBaseByBin[x]!!
                    negPosBaseByBin[x] = Pair(pair.first + groupPair.first, pair.second + groupPair.second)
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

    class StackSizeCalculator(
        private val stackInsideGroups: Boolean,
        private val groupSizeReducer: (Double, Double) -> Double
    ) {
        internal fun reduceGroupSize(currentGroupSize: Double, newElementSize: Double): Double {
            return if (stackInsideGroups)
                currentGroupSize + newElementSize
            else
                groupSizeReducer(currentGroupSize, newElementSize)
        }

        internal fun calculateStackSize(stackSize: Double, groupSize: Double): Double {
            return if (stackInsideGroups)
                stackSize + groupSize
            else
                stackSize
        }
    }

    companion object {
        private const val DEF_VJUST = 1.0

        // Default calculator: all points will be stacked one above another
        val DEF_STACK_SIZE_CALCULATOR = StackSizeCalculator(true) { groupSize, elementSize ->
            groupSize + elementSize
        }
        // "Max" calculator: inside groups points aren't stacked, but each next group will be stacked over sum of maximum values of the previous groups
        val MAX_STACK_SIZE_CALCULATOR = StackSizeCalculator(false) { groupSize, elementSize ->
            if (groupSize >= 0 && elementSize >= 0)
                max(groupSize, elementSize)
            else
                min(groupSize, elementSize)
        }
    }
}