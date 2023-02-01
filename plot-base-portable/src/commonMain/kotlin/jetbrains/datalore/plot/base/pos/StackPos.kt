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

internal class StackPos(aes: Aesthetics, vjust: Double?, stackingContext: StackingContext = StackingContext.summable()) : PositionAdjustment {

    private val myOffsetByIndex: Map<Int, Double> = mapIndexToOffset(aes, vjust ?: DEF_VJUST, stackingContext)

    private fun mapIndexToOffset(aes: Aesthetics, vjust: Double, stackingContext: StackingContext): Map<Int, Double> {
        val offsetByIndex = HashMap<Int, Double>()
        aes.dataPoints().asSequence()
            .mapIndexed { i, p -> Pair(i, p) }
            .filter { SeriesUtil.allFinite(it.second.x(), it.second.y()) }
            .groupBy { it.second.group() }
            .forEach { (_, indexedDataPoints) ->
                for ((i, dataPoint) in indexedDataPoints) {
                    val x = dataPoint.x()!!
                    val y = dataPoint.y()!!
                    val offset = stackingContext.getTotalOffset(x, y)
                    offsetByIndex[i] = offset - y * (1 - vjust)
                }
                stackingContext.computeStackOffset()
            }
        return offsetByIndex
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        return v.add(DoubleVector(0.0, myOffsetByIndex[p.index()]!!))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.STACK.handlesGroups()
    }

    internal data class Offset(val stack: Double, val group: Double)

    internal class StackingContext private constructor(
        private val stackInsideGroups: Boolean,
        private val positiveGroupOffsetReducer: (Double, Double) -> Double
    ) {
        private val positiveOffset = HashMap<Double, Offset>()
        private val negativeOffset = HashMap<Double, Offset>()

        fun getTotalOffset(stackId: Double, offsetValue: Double): Double {
            return if (offsetValue >= 0) {
                val currentOffset = positiveOffset.getOrPut(stackId) { Offset(0.0, 0.0) }
                positiveOffset[stackId] = Offset(currentOffset.stack, getGroupOffset(currentOffset.group, offsetValue))
                getCurrentTotalOffset(currentOffset.stack, currentOffset.group)
            } else {
                val currentOffset = negativeOffset.getOrPut(stackId) { Offset(0.0, 0.0) }
                negativeOffset[stackId] = Offset(currentOffset.stack, -getGroupOffset(-currentOffset.group, -offsetValue))
                getCurrentTotalOffset(currentOffset.stack, currentOffset.group)
            }
        }

        fun computeStackOffset() {
            positiveOffset.forEach { (stackId, offset) ->
                positiveOffset[stackId] = Offset(offset.stack + offset.group, 0.0)
            }
            negativeOffset.forEach { (stackId, offset) ->
                negativeOffset[stackId] = Offset(offset.stack + offset.group, 0.0)
            }
        }

        private fun getGroupOffset(currentGroupOffset: Double, offsetValue: Double): Double {
            return if (stackInsideGroups)
                currentGroupOffset + offsetValue
            else
                positiveGroupOffsetReducer(currentGroupOffset, offsetValue)
        }

        private fun getCurrentTotalOffset(stackOffset: Double, groupOffset: Double): Double {
            return if (stackInsideGroups)
                stackOffset + groupOffset
            else
                stackOffset
        }

        companion object {
            // Default: all points will be stacked one above another
            fun summable(): StackingContext = StackingContext(true) { currentGroupOffset, offsetValue -> currentGroupOffset + offsetValue }

            // Inside groups points aren't stacked, but each next group will be stacked over sum of maximum values of the previous groups
            fun spannableToMax(): StackingContext = StackingContext(false) { currentGroupOffset, offsetValue -> max(currentGroupOffset, offsetValue) }
        }
    }

    companion object {
        private const val DEF_VJUST = 1.0
    }
}