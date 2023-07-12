/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class MeanTest(
    private val testData: TestData
    ) {

    @Test
    fun testMean() {
        assertEquals(testData.expected, SeriesUtil.mean(testData.values, testData.default))
    }

    data class TestData(
        val values: List<Double?>,
        val expected: Double?,
        val default: Double? = null
    )


    companion object {
        private fun testMean(values: List<Number?>, expected: Number?, default: Number? = null): Array<Any?> {
            return arrayOf(TestData(values.map { it?.toDouble() }, expected?.toDouble(), default?.toDouble()))
        }

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            return listOf<Array<Any?>>(
                testMean(
                    values = emptyList(),
                    expected = null
                ),

                testMean (
                    values = emptyList(),
                    default = 1.0,
                    expected = 1.0,
                ),

                testMean(
                    values = listOf(1, 2, 3, 4),
                    expected = 2.5
                ),

                testMean(
                    values = MutableList(size = 20) { 3.0 },
                    expected = 3.0
                ),

                testMean(
                    values = (1_000_000_000..1_000_100_000).map { it }.toList(),
                    expected = 1.000050000000016E9
                ),

                testMean(
                    values = (0..1000).map { it }.toList(),
                    expected = 500.00000000000017
                ),

                testMean (
                    values = listOf(null, 1.0, 2.0),
                    expected = 1.5,
                ),

                testMean (
                    values = listOf(Double.NaN, 1.0, 2.0),
                    expected = 1.5,
                ),

                testMean (
                    values = listOf(Double.NEGATIVE_INFINITY, 1.0),
                    expected = Double.NEGATIVE_INFINITY,
                ),

                testMean (
                    values = listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
                    default = 1.0,
                    expected = 1.0,
                ),
            )
        }
    }
}
