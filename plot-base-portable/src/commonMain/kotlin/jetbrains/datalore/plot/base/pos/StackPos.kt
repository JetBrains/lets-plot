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
import kotlin.math.max

class StackPos(aes: Aesthetics, vjust: Double?, offsetState: OffsetState? = null) : PositionAdjustment {

    private val myOffsetByIndex: Map<Int, Double> = mapIndexToOffset(aes, vjust ?: DEF_VJUST, offsetState ?: OffsetState.DEF_OFFSET_STATE)

    private fun mapIndexToOffset(aes: Aesthetics, vjust: Double, offsetState: OffsetState): Map<Int, Double> {
        val offsetByIndex = HashMap<Int, Double>()
        offsetState.clear()
        aes.dataPoints().asSequence()
            .mapIndexed { i, p -> Pair(i, p) }
            .filter { SeriesUtil.allFinite(it.second.x(), it.second.y()) }
            .groupBy { it.second.group() }
            .forEach { (_, indexedDataPoints) ->
                for ((i, dataPoint) in indexedDataPoints) {
                    val x = dataPoint.x()!!
                    val y = dataPoint.y()!!
                    val offset = offsetState.append(x, y)
                    offsetByIndex[i] = offset - y * (1 - vjust)
                }
                offsetState.update()
            }
        return offsetByIndex
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        return v.add(DoubleVector(0.0, myOffsetByIndex[p.index()]!!))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.STACK.handlesGroups()
    }

    class OffsetState private constructor(
        private val stackInsideGroups: Boolean,
        private val positiveGroupOffsetReducer: (Double, Double) -> Double
    ) {
        private val positiveOffset = HashMap<Double, Double>()
        private val negativeOffset = HashMap<Double, Double>()
        private val positiveGroupOffset = HashMap<Double, Double>()
        private val negativeGroupOffset = HashMap<Double, Double>()

        fun append(stackId: Double, offsetValue: Double): Double {
            initOffsetContainers(stackId)
            return if (offsetValue >= 0) {
                val currentPositiveOffset = positiveOffset[stackId]!!
                val currentPositiveGroupOffset = positiveGroupOffset[stackId]!!
                positiveGroupOffset[stackId] = reduceGroupOffset(currentPositiveGroupOffset, offsetValue)
                calculateTotalOffset(currentPositiveOffset, currentPositiveGroupOffset)
            } else {
                val currentNegativeOffset = negativeOffset[stackId]!!
                val currentNegativeGroupOffset = negativeGroupOffset[stackId]!!
                negativeGroupOffset[stackId] = -reduceGroupOffset(-currentNegativeGroupOffset, -offsetValue)
                calculateTotalOffset(currentNegativeOffset, currentNegativeGroupOffset)
            }
        }

        fun clear() {
            positiveOffset.clear()
            negativeOffset.clear()
            positiveGroupOffset.clear()
            negativeGroupOffset.clear()
        }

        fun update() {
            for (stackId in positiveGroupOffset.keys) {
                positiveOffset[stackId] = positiveOffset[stackId]!! + positiveGroupOffset[stackId]!!
                positiveGroupOffset[stackId] = 0.0
                negativeOffset[stackId] = negativeOffset[stackId]!! + negativeGroupOffset[stackId]!!
                negativeGroupOffset[stackId] = 0.0
            }
        }

        private fun initOffsetContainers(stackId: Double) {
            if (!positiveOffset.containsKey(stackId)) {
                positiveOffset[stackId] = 0.0
                negativeOffset[stackId] = 0.0
            }
            if (!positiveGroupOffset.containsKey(stackId)) {
                positiveGroupOffset[stackId] = 0.0
                negativeGroupOffset[stackId] = 0.0
            }
        }

        private fun reduceGroupOffset(currentGroupOffset: Double, offsetValue: Double): Double {
            return if (stackInsideGroups)
                currentGroupOffset + offsetValue
            else
                positiveGroupOffsetReducer(currentGroupOffset, offsetValue)
        }

        private fun calculateTotalOffset(stackOffset: Double, groupOffset: Double): Double {
            return if (stackInsideGroups)
                stackOffset + groupOffset
            else
                stackOffset
        }

        companion object {
            // Default: all points will be stacked one above another
            val DEF_OFFSET_STATE = OffsetState(true) { currentGroupOffset, offsetValue -> currentGroupOffset + offsetValue }

            // Inside groups points aren't stacked, but each next group will be stacked over sum of maximum values of the previous groups
            val MAX_OFFSET_STATE = OffsetState(false) { currentGroupOffset, offsetValue -> max(currentGroupOffset, offsetValue) }
        }
    }

    companion object {
        private const val DEF_VJUST = 1.0
    }
}