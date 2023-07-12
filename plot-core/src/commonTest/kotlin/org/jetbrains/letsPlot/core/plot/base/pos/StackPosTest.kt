/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import kotlin.test.Test

class StackPosTest : PosTest() {
    @Test
    fun testEmptyDataset() {
        compareWithExpectedOffsets(
            xValues = listOf(),
            yValues = listOf(),
            expectedYOffsets = listOf(),
            posConstructor = getPositionAdjustmentConstructor(),
            messageBeginning = "Should work with empty dataset"
        )
    }

    @Test
    fun testWithoutGroupingWithStackingModeAll() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(3.0, 5.0, 6.0, 3.0, -3.0, -4.0, -6.0, 4.0, 6.0, 3.0, 5.0, 6.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.ALL),
            messageBeginning = "Should work without grouping"
        )
    }

    @Test
    fun testWithoutGroupingWithStackingModeGroups() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.GROUPS),
            messageBeginning = "Should work without grouping"
        )
    }

    @Test
    fun testWithGroupingAndStackingModeAll() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 0, 1, 2, 1, 1, 0, 0),
            expectedYOffsets = listOf(3.0, 5.0, 6.0, 3.0, -3.0, -4.0, -6.0, 6.0, 5.0, 6.0, 2.0, 3.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with grouping"
        )
    }

    @Test
    fun testWithGroupingAndStackingModeGroups() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 0, 1, 2, 1, 1, 0, 0),
            expectedYOffsets = listOf(3.0, 5.0, 6.0, 3.0, -3.0, -1.0, -5.0, 6.0, 5.0, 5.0, 2.0, 1.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.GROUPS),
            messageBeginning = "Should work with grouping"
        )
    }

    @Test
    fun testWithNanValues() {
        compareWithExpectedOffsets(
            xValues = listOf(null, 0.0, 0.0, null, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(null, 2.0, 1.0, 3.0, 1.0, 2.0, null, 2.0, 1.0),
            expectedYOffsets = listOf(null, 2.0, 3.0, null, 1.0, 3.0, null, 2.0, 3.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with NaN values in data"
        )
    }

    @Test
    fun testVjust() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(3.0 * 0.5, 3.0 + 2.0 * 0.5, 5.0 + 1.0 * 0.5,
                3.0 * 0.5, -3.0 * 0.5, -3.0 - 1.0 * 0.5, -4.0 - 2.0 * 0.5, 3.0 + 1.0 * 0.5, 4.0 + 2.0 * 0.5,
                3.0 * 0.5, 3.0 + 2.0 * 0.5, 5.0 + 1.0 * 0.5),
            posConstructor = getPositionAdjustmentConstructor(vjust = 0.5, stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with vjust = 0.5"
        )
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(0.0, 3.0, 5.0, 0.0, 0.0, -3.0, -4.0, 3.0, 4.0, 0.0, 3.0, 5.0),
            posConstructor = getPositionAdjustmentConstructor(vjust = 0.0, stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with vjust = 0.0"
        )
    }

    private fun getPositionAdjustmentConstructor(
        vjust: Double? = null,
        stackingMode: StackingMode = StackablePos.DEF_STACKING_MODE
    ): (Aesthetics) -> PositionAdjustment {
        return { aes -> StackPos(aes, vjust = vjust, stackingMode = stackingMode) }
    }
}