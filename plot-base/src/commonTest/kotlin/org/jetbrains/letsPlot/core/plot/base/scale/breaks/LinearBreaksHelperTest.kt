/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import jetbrains.datalore.base.assertion.assertArrayEquals
import kotlin.math.sign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LinearBreaksHelperTest {

    @Test
    fun breaksNormalRange() {
        assertBreaks(
            DOMAINS,
            EXPECTED_BREAKS,
            ERROR_TOLERANCE
        )
    }

    @Test
    fun breaksLargeNumbers() {
        val factor = 1e5
        assertBreaks(
            multiply(
                DOMAINS,
                factor
            ),
            multiply(
                EXPECTED_BREAKS,
                factor
            ),
            ERROR_TOLERANCE
        )
    }

    @Test
    fun breaksSmallNumbers() {
        val factor = 1e-5
        assertBreaks(
            multiply(
                DOMAINS,
                factor
            ),
            multiply(
                EXPECTED_BREAKS,
                factor
            ),
            ERROR_TOLERANCE
        )
    }

    @Test
    fun negativeZero1() {
        val breaks =
            computeBreaks(0.0, 10.0, 10)
        print(breaks)
        assertTrue(sign(breaks[0]) >= 0)
    }

    @Test
    fun beyondPrecision() {
        val breaks = computeBreaks(
            1.0,
            1.0 + 1E-13,
            5
        )
        assertEquals(1, breaks.size)
        assertEquals(1.0, breaks[0])
    }

    companion object {

        private val DOMAINS = arrayOf(
            doubleArrayOf(0.0, 100.0),
            doubleArrayOf(50.0, 100.0),
            doubleArrayOf(49.5, 100.5),
            doubleArrayOf(90.0, 100.0),
            doubleArrayOf(-20.0, 20.0),
            doubleArrayOf(100.0, 0.0)
        )
        private val EXPECTED_BREAKS = arrayOf(
            doubleArrayOf(0.0, 20.0, 40.0, 60.0, 80.0, 100.0),
            doubleArrayOf(50.0, 60.0, 70.0, 80.0, 90.0, 100.0),
            doubleArrayOf(50.0, 60.0, 70.0, 80.0, 90.0, 100.0),
            doubleArrayOf(90.0, 92.0, 94.0, 96.0, 98.0, 100.0),
            doubleArrayOf(-20.0, -10.0, 0.0, 10.0, 20.0),
            doubleArrayOf(100.0, 80.0, 60.0, 40.0, 20.0, 0.0)
        )

        private const val ERROR_TOLERANCE = 1e-10

        private fun multiply(values2d: Array<DoubleArray>, factor: Double): Array<DoubleArray> {
            return values2d.map { values ->
                multiply(
                    values,
                    factor
                )
            }.toTypedArray()
        }

        private fun assertBreaks(
            scaleDomains: Array<DoubleArray>,
            expectedBreaks: Array<DoubleArray>,
            errorTolerance: Double
        ) {
            val targetBreakCount = 5
            for ((i, domain) in scaleDomains.withIndex()) {
                val expectedBreaks_i = expectedBreaks[i]
                val breaks_i = computeBreaks(
                    domain[0],
                    domain[1],
                    targetBreakCount
                )

                print(domain)
                print(expectedBreaks_i)
                print(breaks_i)

                assertArrayEquals(
                    expectedBreaks_i.toTypedArray(),
                    asPrimitiveDoubles(
                        breaks_i
                    ).toTypedArray(),
                    errorTolerance
                )
            }
        }

        private fun computeBreaks(domainStart: Double, domainEnd: Double, targetBreakCount: Int): Array<Double> {
            val helper = LinearBreaksHelper(domainStart, domainEnd, targetBreakCount)
            return helper.breaks.toTypedArray()
        }

        private fun multiply(values: DoubleArray, factor: Double): DoubleArray {
            val result = DoubleArray(values.size)
            var i = 0
            for (value in values) {
                result[i++] = value * factor
            }
            return result
        }

        private fun print(@Suppress("UNUSED_PARAMETER") values: Array<Double>) {
            //    System.out.println(listOf(values));
        }

        private fun print(@Suppress("UNUSED_PARAMETER") values: DoubleArray) {
            //    StringBuffer sb = new StringBuffer("[");
            //    boolean first = true;
            //    for (double value : values) {
            //      if (first) {
            //        first = false;
            //      } else {
            //        sb.append(", ");
            //      }
            //      sb.append(value);
            //    }
            //    sb.append(']');
            //    System.out.println(sb);
        }

        private fun asPrimitiveDoubles(values: Array<Double>): DoubleArray {
            val result = DoubleArray(values.size)
            var i = 0
            for (value in values) {
                result[i++] = value
            }
            return result
        }
    }

}
