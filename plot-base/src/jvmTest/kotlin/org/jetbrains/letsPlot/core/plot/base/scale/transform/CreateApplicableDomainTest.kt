/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform


import junit.framework.TestCase.failNotEquals
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
internal class CreateApplicableDomainTest(
    private val transform: ContinuousTransform,
    private val value: Double,
    private val expected: DoubleSpan
) {

    @Test
    fun verify() {
        val actual = transform.createApplicableDomain(value)
        assertEqualRanges(expected, actual, "${transform::class.simpleName} [$value]")

        // Try to apply
        val lowerTransformed = transform.apply(actual.lowerEnd)
        val upperTransformed = transform.apply(actual.upperEnd)

        assertTrue(SeriesUtil.isFinite(lowerTransformed))
        assertTrue(SeriesUtil.isFinite(upperTransformed))

        // Try to inverse
        val lowerInversed = transform.applyInverse(lowerTransformed)
        val upperInversed = transform.applyInverse(upperTransformed)

        val actualInversed = DoubleSpan(lowerInversed!!, upperInversed!!)
        assertEqualRanges(expected, actualInversed, "${transform::class.simpleName} [$value] <inversed>")
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun params(): List<Array<Any>> {
            return paramsIdentity() + paramsReverse() + paramsSqrt() + paramsLog10() + paramsLog2()
        }

        private fun paramsIdentity(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    Transforms.IDENTITY,
                    0.0,
                    DoubleSpan(-0.5, 0.5)
                ),
                arrayOf(
                    Transforms.IDENTITY,
                    -5.0,
                    DoubleSpan(-5.5, -4.5)
                ),
                arrayOf(
                    Transforms.IDENTITY,
                    Double.NaN,
                    DoubleSpan(-0.5, 0.5)
                ),
            )
        }

        private fun paramsReverse(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    Transforms.REVERSE,
                    0.0,
                    DoubleSpan(-0.5, 0.5)
                ),
                arrayOf(
                    Transforms.REVERSE,
                    -5.0,
                    DoubleSpan(-5.5, -4.5)
                ),
                arrayOf(
                    Transforms.REVERSE,
                    Double.NaN,
                    DoubleSpan(-0.5, 0.5)
                ),
            )
        }

        private fun paramsSqrt(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    Transforms.SQRT,
                    0.0,
                    DoubleSpan(0.0, 0.5)
                ),
                arrayOf(
                    Transforms.SQRT,
                    -5.0,
                    DoubleSpan(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.SQRT,
                    Double.NaN,
                    DoubleSpan(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.SQRT,
                    0.3,
                    DoubleSpan(0.0, 0.8)
                ),
                arrayOf(
                    Transforms.SQRT,
                    0.7,
                    DoubleSpan(0.2, 1.2)
                ),
            )
        }

        private fun paramsLog10(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    Transforms.LOG10,
                    0.0,
                    DoubleSpan(LogTransform.calcLowerLimDomain(10.0), 0.5)
                ),
                arrayOf(
                    Transforms.LOG10,
                    -5.0,
                    DoubleSpan(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.LOG10,
                    Double.NaN,
                    DoubleSpan(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.LOG10,
                    0.3,
                    DoubleSpan(0.15, 0.8)
                ),
                arrayOf(
                    Transforms.LOG10,
                    10.0.pow(20),
                    DoubleSpan(1.0E20, 1.0E20)
                ),
                arrayOf(
                    Transforms.LOG10,
                    10.0.pow(-20),
                    DoubleSpan(5.0E-21, 0.5)
                ),
            )
        }

        private fun paramsLog2(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    Transforms.LOG2,
                    0.0,
                    DoubleSpan(LogTransform.calcLowerLimDomain(2.0), 0.5)
                ),
                arrayOf(
                    Transforms.LOG2,
                    -5.0,
                    DoubleSpan(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.LOG2,
                    Double.NaN,
                    DoubleSpan(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.LOG2,
                    0.3,
                    DoubleSpan(0.15, 0.8)
                ),
                arrayOf(
                    Transforms.LOG2,
                    2.0.pow(50),
                    DoubleSpan(1.1258999068426235E15, 1.1258999068426235E15)
                ),
                arrayOf(
                    Transforms.LOG2,
                    2.0.pow(-50),
                    DoubleSpan(4.440892098500626E-16, 0.5)
                ),
            )
        }

        private fun assertEqualRanges(expected: DoubleSpan, actual: DoubleSpan, message: String) {
            fun almostEqual(v0: Double, v1: Double): Boolean {
                return abs(v0 - v1) <= (v0.absoluteValue + v1.absoluteValue) / 10.0.pow(10)
            }

            if (!(almostEqual(expected.lowerEnd, actual.lowerEnd) && almostEqual(expected.upperEnd, actual.upperEnd))) {
                failNotEquals(message, expected, actual)
            }
        }
    }
}