/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.*

enum class StackingMode {
    GROUPS, ALL;

    companion object {

        private val ENUM_INFO = EnumInfoFactory.createEnumInfo<StackingMode>()

        fun safeValueOf(v: String): StackingMode {
            return ENUM_INFO.safeValueOf(v) ?: throw IllegalArgumentException(
                "Unsupported stacking mode: '$v'\n" +
                "Use one of: groups, all."
            )
        }
    }
}

abstract class StackablePos : PositionAdjustment {
    internal fun mapIndexToOffset(aes: Aesthetics, vjust: Double, stackingMode: StackingMode): Map<Int, StackOffset> {
        val stackingContext = when (stackingMode) {
            StackingMode.GROUPS -> StackingContext(false)
            StackingMode.ALL -> StackingContext(true)
        }
        val offsetByIndex = HashMap<Int, StackOffset>()
        val indexedDataPoints = aes.dataPoints().asSequence()
            .mapIndexed { i, p -> Pair(i, p) }
            .filter { SeriesUtil.allFinite(it.second.x(), it.second.y()) }
        indexedDataPoints.groupBy { it.second.group() }.forEach { (_, indexedDataPoints) ->
            for ((i, dataPoint) in indexedDataPoints) {
                val x = dataPoint.x()!!
                val y = dataPoint.y()!!
                val offset = stackingContext.getTotalOffset(x, y)
                offsetByIndex[i] = StackOffset(offset - y * (1 - vjust), 0.0)
            }
            stackingContext.computeStackOffset()
        }
        indexedDataPoints.forEach { (i, dataPoint) ->
            val x = dataPoint.x()!!
            val y = dataPoint.y()!!
            offsetByIndex[i] = StackOffset(
                offsetByIndex.getOrElse(i) { StackOffset(0.0, 0.0) }.value,
                abs(stackingContext.getFixedTotalOffset(x, y))
            )
        }
        return offsetByIndex
    }

    internal data class StackOffset(val value: Double, val max: Double)

    private data class GroupOffset(val value: Double, val stack: Double)

    private class StackingContext(private val stackInsideGroups: Boolean) {
        private val positiveOffset = HashMap<Double, GroupOffset>()
        private val negativeOffset = HashMap<Double, GroupOffset>()

        fun getFixedTotalOffset(stackId: Double, offsetValue: Double): Double {
            return if (offsetValue >= 0) {
                positiveOffset.getOrElse(stackId) { GroupOffset(0.0, 0.0) }.stack
            } else {
                negativeOffset.getOrElse(stackId) { GroupOffset(0.0, 0.0) }.stack
            }
        }

        fun getTotalOffset(stackId: Double, offsetValue: Double): Double {
            return if (offsetValue >= 0) {
                val currentOffset = positiveOffset.getOrPut(stackId) { GroupOffset(0.0, 0.0) }
                positiveOffset[stackId] =
                    GroupOffset(getGroupOffset(currentOffset.value, offsetValue), currentOffset.stack)
                getCurrentTotalOffset(currentOffset.stack, currentOffset.value)
            } else {
                val currentOffset = negativeOffset.getOrPut(stackId) { GroupOffset(0.0, 0.0) }
                negativeOffset[stackId] =
                    GroupOffset(-getGroupOffset(-currentOffset.value, -offsetValue), currentOffset.stack)
                getCurrentTotalOffset(currentOffset.stack, currentOffset.value)
            }
        }

        fun computeStackOffset() {
            positiveOffset.forEach { (stackId, offset) ->
                positiveOffset[stackId] = GroupOffset(0.0, offset.stack + offset.value)
            }
            negativeOffset.forEach { (stackId, offset) ->
                negativeOffset[stackId] = GroupOffset(0.0, offset.stack + offset.value)
            }
        }

        private fun getGroupOffset(currentGroupOffset: Double, offsetValue: Double): Double {
            return if (stackInsideGroups)
                currentGroupOffset + offsetValue
            else
                max(currentGroupOffset, offsetValue)
        }

        private fun getCurrentTotalOffset(stackOffset: Double, groupOffset: Double): Double {
            return if (stackInsideGroups)
                stackOffset + groupOffset
            else
                stackOffset
        }
    }

    companion object {
        val DEF_STACKING_MODE = StackingMode.GROUPS
    }
}