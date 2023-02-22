/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.PositionAdjustment
import kotlin.test.Test

class FillPosTest : PosTest() {
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
            yValues = listOf(6.0, 4.0, 2.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(0.5, 5.0/6.0, 1.0, 0.5, -0.5, -2.0/3.0, -1.0, 2.0/3.0, 1.0, 0.5, 5.0/6.0, 1.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.ALL),
            messageBeginning = "Should work without grouping"
        )
    }

    @Test
    fun testWithoutGroupingWithStackingModeGroups() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(6.0, 4.0, 2.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(1.0, 2.0/3.0, 1.0/3.0, 1.0, -1.0, -1.0/3.0, -2.0/3.0, 1.0/3.0, 2.0/3.0, 1.0, 2.0/3.0, 1.0/3.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.GROUPS),
            messageBeginning = "Should work without grouping"
        )
    }

    @Test
    fun testWithGroupingAndStackingModeAll() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(6.0, 4.0, 2.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 0, 1, 2, 1, 1, 0, 0),
            expectedYOffsets = listOf(0.5, 5.0/6.0, 1.0, 0.5, -0.5, -2.0/3.0, -1.0, 1.0, 5.0/6.0, 1.0, 1.0/3.0, 0.5),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with grouping"
        )
    }

    @Test
    fun testWithGroupingAndStackingModeGroups() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(6.0, 4.0, 2.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 0, 1, 2, 1, 1, 0, 0),
            expectedYOffsets = listOf(0.5, 5.0/6.0, 1.0, 0.5, -0.6, -0.2, -1.0, 1.0, 5.0/6.0, 1.0, 0.4, 0.2),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.GROUPS),
            messageBeginning = "Should work with grouping"
        )
    }

    @Test
    fun testWithNanValues() {
        compareWithExpectedOffsets(
            xValues = listOf(null, 0.0, 0.0, null, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(null, 4.0, 2.0, 3.0, 1.0, 2.0, null, 2.0, 1.0),
            expectedYOffsets = listOf(null, 2.0/3.0, 1.0, null, 1.0/3.0, 1.0, null, 2.0/3.0, 1.0),
            posConstructor = getPositionAdjustmentConstructor(stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with NaN values in data"
        )
    }

    @Test
    fun testVjust() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(6.0, 4.0, 2.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(0.5 * 0.5, 0.5 + 1.0/3.0 * 0.5, 5.0/6.0 + 1.0/6.0 * 0.5,
                0.5 * 0.5, -0.5 * 0.5, -0.5 - 1.0/6.0 * 0.5, -2.0/3.0 - 1.0/3.0 * 0.5, 0.5 + 1.0/6.0 * 0.5, 2.0/3.0 + 1.0/3.0 * 0.5,
                0.5 * 0.5, 0.5 + 1.0/3.0 * 0.5, 5.0/6.0 + 1.0/6.0 * 0.5),
            posConstructor = getPositionAdjustmentConstructor(vjust = 0.5, stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with vjust = 0.5"
        )
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(6.0, 4.0, 2.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(0.0, 0.5, 5.0/6.0, 0.0, 0.0, -0.5, -2.0/3.0, 0.5, 2.0/3.0, 0.0, 0.5, 5.0/6.0),
            posConstructor = getPositionAdjustmentConstructor(vjust = 0.0, stackingMode = StackingMode.ALL),
            messageBeginning = "Should work with vjust = 0.0"
        )
    }

    private fun getPositionAdjustmentConstructor(
        vjust: Double? = null,
        stackingMode: StackingMode = StackablePos.DEF_STACKING_MODE
    ): (Aesthetics) -> PositionAdjustment {
        return { aes -> FillPos(aes, vjust = vjust, stackingMode = stackingMode) }
    }
}