/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.test.Test

class StackPosTest {
    private val doubleComparisonPrecision = 1e-9

    @Test
    fun emptyDataset() {
        compareWithExpectedOffsets(
            xValues = listOf(),
            yValues = listOf(),
            expectedOffsets = listOf(),
            messageBeginning = "Should work with empty dataset"
        )
    }

    @Test
    fun testWithoutGroupingWithOffsetSum() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedOffsets = listOf(3.0, 5.0, 6.0, 3.0, -3.0, -4.0, -6.0, 4.0, 6.0, 3.0, 5.0, 6.0),
            messageBeginning = "Should work without grouping"
        )
    }

    @Test
    fun testWithoutGroupingWithOffsetMax() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedOffsets = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            stackingContext = StackPos.StackingContext(false),
            messageBeginning = "Should work without grouping"
        )
    }

    @Test
    fun testWithGroupingAndOffsetSum() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 0, 1, 2, 1, 1, 0, 0),
            expectedOffsets = listOf(3.0, 5.0, 6.0, 3.0, -3.0, -4.0, -6.0, 6.0, 5.0, 6.0, 2.0, 3.0),
            messageBeginning = "Should work with grouping"
        )
    }

    @Test
    fun testWithGroupingAndOffsetMax() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            groupValues = listOf(0, 1, 2, 0, 0, 0, 1, 2, 1, 1, 0, 0),
            expectedOffsets = listOf(3.0, 5.0, 6.0, 3.0, -3.0, -1.0, -5.0, 6.0, 5.0, 5.0, 2.0, 1.0),
            stackingContext = StackPos.StackingContext(false),
            messageBeginning = "Should work with grouping"
        )
    }

    @Test
    fun testWithNanValues() {
        compareWithExpectedOffsets(
            xValues = listOf(null, 0.0, 0.0, null, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(null, 2.0, 1.0, 3.0, 1.0, 2.0, null, 2.0, 1.0),
            expectedOffsets = listOf(null, 2.0, 3.0, null, 1.0, 3.0, null, 2.0, 3.0),
            messageBeginning = "Should work with NaN values in data"
        )
    }

    @Test
    fun testVjust() {
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedOffsets = listOf(3.0 * 0.5, 3.0 + 2.0 * 0.5, 5.0 + 1.0 * 0.5,
                3.0 * 0.5, -3.0 * 0.5, -3.0 - 1.0 * 0.5, -4.0 - 2.0 * 0.5, 3.0 + 1.0 * 0.5, 4.0 + 2.0 * 0.5,
                3.0 * 0.5, 3.0 + 2.0 * 0.5, 5.0 + 1.0 * 0.5),
            vjust = 0.5,
            messageBeginning = "Should work with vjust = 0.5"
        )
        compareWithExpectedOffsets(
            xValues = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            yValues = listOf(3.0, 2.0, 1.0, 3.0, -3.0, -1.0, -2.0, 1.0, 2.0, 3.0, 2.0, 1.0),
            expectedOffsets = listOf(0.0, 3.0, 5.0, 0.0, 0.0, -3.0, -4.0, 3.0, 4.0, 0.0, 3.0, 5.0),
            vjust = 0.0,
            messageBeginning = "Should work with vjust = 0.0"
        )
    }

    private fun compareWithExpectedOffsets(
        xValues: List<Double?>,
        yValues: List<Double?>,
        groupValues: List<Int>? = null,
        expectedOffsets: List<Double?>,
        vjust: Double? = null,
        stackingContext: StackPos.StackingContext = StackPos.StackingContext(),
        messageBeginning: String
    ) {
        val aes = buildAesthetics(xValues, yValues, groupValues)
        val pos = StackPos(aes, vjust = vjust, stackingContext = stackingContext)
        val ctx = getBogusContext()
        for (i in 0 until aes.dataPointCount()) {
            val p = aes.dataPointAt(i)
            if (!SeriesUtil.allFinite(p.x(), p.y())) continue
            val stackedPoint = pos.translate(DoubleVector(p.x()!!, p.y()!!), p, ctx)
            assertEquals(
                expectedOffsets[i],
                stackedPoint.y,
                doubleComparisonPrecision,
                "$messageBeginning at $i-th point $stackedPoint."
            )
        }
    }

    private fun buildAesthetics(
        xValues: List<Double?>,
        yValues: List<Double?>,
        groupValues: List<Int>?
    ): Aesthetics {
        val builder = AestheticsBuilder(xValues.size)
            .x(AestheticsBuilder.list(xValues))
            .y(AestheticsBuilder.list(yValues))
        if (groupValues != null) builder.group(AestheticsBuilder.list(groupValues))
        return builder.build()
    }

    private fun getBogusContext(): GeomContext {
        return object : GeomContext {
            override val flipped: Boolean
                get() = throw IllegalStateException("Not available in a bogus geom context")
            override val targetCollector: GeomTargetCollector
                get() = throw IllegalStateException("Not available in a bogus geom context")
            override val annotations: Annotations?
                get() = throw IllegalStateException("Not available in a bogus geom context")

            override fun getResolution(aes: Aes<Double>): Double {
                throw IllegalStateException("Not available in a bogus geom context")
            }

            override fun getAesBounds(): DoubleRectangle {
                throw IllegalStateException("Not available in a bogus geom context")
            }

            override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
                throw IllegalStateException("Not available in a bogus geom context")
            }

            override fun isMappedAes(aes: Aes<*>): Boolean {
                throw IllegalStateException("Not available in a bogus geom context")
            }

            override fun estimateTextSize(
                text: String,
                family: String,
                size: Double,
                isBold: Boolean,
                isItalic: Boolean
            ): DoubleVector {
                throw IllegalStateException("Not available in a bogus geom context")
            }
        }
    }
}