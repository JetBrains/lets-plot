/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform


import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.common.data.SeriesUtil
import junit.framework.TestCase.failNotEquals
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
internal class EnsureApplicableDomainTest(
    private val transform: ContinuousTransform,
    private val range: ClosedRange<Double>?,
    private val expected: ClosedRange<Double>
) {

    @Test
    fun verify() {
        val actual = Transforms.ensureApplicableDomain(range, transform)
        assertEqualRanges(expected, actual, "${transform::class.simpleName} [$range]")

        // Try to apply
        val lowerTransformed = transform.apply(actual.lowerEnd)
        val upperTransformed = transform.apply(actual.upperEnd)

        assertTrue(SeriesUtil.isFinite(lowerTransformed))
        assertTrue(SeriesUtil.isFinite(upperTransformed))

        // Try to inverse
        val lowerInversed = transform.applyInverse(lowerTransformed)
        val upperInversed = transform.applyInverse(upperTransformed)

        val actualInversed = ClosedRange(lowerInversed!!, upperInversed!!)
        assertEqualRanges(expected, actualInversed, "${transform::class.simpleName} [$range] <inversed>")
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun params(): List<Array<Any?>> {
            return paramsIdentity() + paramsReverse() + paramsSqrt() + paramsLog10()
        }

        private fun paramsIdentity(): List<Array<Any?>> {
            return listOf(
                arrayOf(
                    Transforms.IDENTITY,
                    null,
                    ClosedRange(-0.5, 0.5)
                ),
                arrayOf(
                    Transforms.IDENTITY,
                    ClosedRange(-5.0, -5.0),
                    ClosedRange(-5.5, -4.5)
                ),
                arrayOf(
                    Transforms.IDENTITY,
                    ClosedRange(0.0, 0.0),
                    ClosedRange(-0.5, 0.5)
                ),
            )
        }

        private fun paramsReverse(): List<Array<Any?>> {
            return listOf(
                arrayOf(
                    Transforms.REVERSE,
                    null,
                    ClosedRange(-0.5, 0.5)
                ),
                arrayOf(
                    Transforms.REVERSE,
                    ClosedRange(-5.0, -5.0),
                    ClosedRange(-5.5, -4.5)
                ),
                arrayOf(
                    Transforms.REVERSE,
                    ClosedRange(0.0, 0.0),
                    ClosedRange(-0.5, 0.5)
                ),
            )
        }

        private fun paramsSqrt(): List<Array<Any?>> {
            return listOf(
                arrayOf(
                    Transforms.SQRT,
                    null,
                    ClosedRange(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.SQRT,
                    ClosedRange(-5.0, -5.0),
                    ClosedRange(0.0, 0.5)
                ),
                arrayOf(
                    Transforms.SQRT,
                    ClosedRange(-5.0, 0.0),
                    ClosedRange(0.0, 0.5)
                ),
                arrayOf(
                    Transforms.SQRT,
                    ClosedRange(-5.0, 5.0),
                    ClosedRange(0.0, 5.0)
                ),
                arrayOf(
                    Transforms.SQRT,
                    ClosedRange(0.0, 0.0),
                    ClosedRange(0.0, 0.5)
                ),
                arrayOf(
                    Transforms.SQRT,
                    ClosedRange(10.0, 10.0),
                    ClosedRange(9.5, 10.5)
                ),
            )
        }

        private fun paramsLog10(): List<Array<Any?>> {
            return listOf(
                arrayOf(
                    Transforms.LOG10,
                    null,
                    ClosedRange(0.5, 1.5)
                ),
                arrayOf(
                    Transforms.LOG10,
                    ClosedRange(-5.0, -5.0),
                    ClosedRange(Log10Transform.LOWER_LIM_DOMAIN, 0.5)
                ),
                arrayOf(
                    Transforms.LOG10,
                    ClosedRange(-5.0, 0.0),
                    ClosedRange(Log10Transform.LOWER_LIM_DOMAIN, 0.5)
                ),
                arrayOf(
                    Transforms.LOG10,
                    ClosedRange(-5.0, 5.0),
                    ClosedRange(Log10Transform.LOWER_LIM_DOMAIN, 5.0)
                ),
                arrayOf(
                    Transforms.LOG10,
                    ClosedRange(0.0, 5.0),
                    ClosedRange(Log10Transform.LOWER_LIM_DOMAIN, 5.0)
                ),
                arrayOf(
                    Transforms.LOG10,
                    ClosedRange(0.0, 0.0),
                    ClosedRange(Log10Transform.LOWER_LIM_DOMAIN, 0.5)
                ),
                arrayOf(
                    Transforms.LOG10,
                    ClosedRange(10.0, 10.0),
                    ClosedRange(9.5, 10.5)
                ),
            )
        }


        private fun assertEqualRanges(expected: ClosedRange<Double>, actual: ClosedRange<Double>, message: String) {
            fun almostEqual(v0: Double, v1: Double): Boolean {
                return abs(v0 - v1) <= (v0.absoluteValue + v1.absoluteValue) / 10.0.pow(10)
            }

            if (!(almostEqual(expected.lowerEnd, actual.lowerEnd) && almostEqual(expected.upperEnd, actual.upperEnd))) {
                failNotEquals(message, expected, actual)
            }
        }
    }
}