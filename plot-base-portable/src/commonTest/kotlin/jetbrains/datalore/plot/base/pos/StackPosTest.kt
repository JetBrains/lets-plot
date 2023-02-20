/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.PositionAdjustment
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
    fun testWithoutGrouping() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedYOffsets = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            posConstructor = getPositionAdjustmentConstructor(),
            messageBeginning = "Should work without grouping"
        )
    }

    @Test
    fun testWithGrouping() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 0, 1, 2, 1, 1, 0, 0),
            expectedYOffsets = listOf(3.0, 5.0, 6.0, 3.0, -3.0, -1.0, -5.0, 6.0, 5.0, 5.0, 2.0, 1.0),
            posConstructor = getPositionAdjustmentConstructor(),
            messageBeginning = "Should work with grouping"
        )
    }

    @Test
    fun testWithNanValues() {
        compareWithExpectedOffsets(
            xValues = listOf(null, 0.0, 0.0, null, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(null, 2.0, 1.0, 3.0, 1.0, 2.0, null, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 1, 2, 0, 1, 2),
            expectedYOffsets = listOf(null, 2.0, 3.0, null, 1.0, 3.0, null, 2.0, 3.0),
            posConstructor = getPositionAdjustmentConstructor(),
            messageBeginning = "Should work with NaN values in data"
        )
    }

    @Test
    fun testVjust() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 1, 2, 1, 2, 0, 1, 2),
            expectedYOffsets = listOf(3.0 * 0.5, 3.0 + 2.0 * 0.5, 5.0 + 1.0 * 0.5,
                3.0 * 0.5, -3.0 * 0.5, -3.0 - 1.0 * 0.5, -4.0 - 2.0 * 0.5, 3.0 + 1.0 * 0.5, 4.0 + 2.0 * 0.5,
                3.0 * 0.5, 3.0 + 2.0 * 0.5, 5.0 + 1.0 * 0.5),
            posConstructor = getPositionAdjustmentConstructor(vjust = 0.5),
            messageBeginning = "Should work with vjust = 0.5"
        )
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 1, 2, 1, 2, 0, 1, 2),
            expectedYOffsets = listOf(0.0, 3.0, 5.0, 0.0, 0.0, -3.0, -4.0, 3.0, 4.0, 0.0, 3.0, 5.0),
            posConstructor = getPositionAdjustmentConstructor(vjust = 0.0),
            messageBeginning = "Should work with vjust = 0.0"
        )
    }

    private fun getPositionAdjustmentConstructor(
        vjust: Double? = null
    ): (Aesthetics) -> PositionAdjustment {
        return { aes -> StackPos(aes, vjust = vjust) }
    }
}