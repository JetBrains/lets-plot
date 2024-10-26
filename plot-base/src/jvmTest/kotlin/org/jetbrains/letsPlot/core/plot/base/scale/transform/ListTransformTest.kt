/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform


import junit.framework.TestCase.failNotEquals
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertSame

@RunWith(Parameterized::class)
internal class ListTransformTest(
    private val transform: ContinuousTransform,
    private val expectedSame: Boolean,
    private val input: List<Double?>,
    private val expected: List<Double?>,
) {

    @Test
    fun verify() {
        val message = "${transform::class.simpleName}"
        val actual = transform.apply(input)
        if (expectedSame) {
            assertSame(input, actual, message)
        } else {
            assertEqualLists(expected, actual, message)
        }
    }

    companion object {
        private val realNumbers = listOf(
            -1.0, 0.0, 1.0,
            Double.MIN_VALUE, Double.MAX_VALUE,
            -Double.MIN_VALUE, -Double.MAX_VALUE,
        )
        private val naValues = listOf(
            null, Double.NaN,
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
        )
        private val input: List<Double?> = realNumbers + naValues

        @JvmStatic
        @Parameterized.Parameters
        fun params(): List<Array<Any>> {
            return listOf(
                arrayOf(
                    Transforms.IDENTITY, true,
                    input,
                    emptyList<Double?>()
                ),
                arrayOf(
                    Transforms.REVERSE, false,
                    input,
                    input.map { it?.let { -it } }
                ),
                arrayOf(
                    Transforms.SQRT, false,
                    input,
                    input.map { it?.let { sqrt(it) } }
                ),
                arrayOf(
                    Transforms.LOG10, false,
                    input,
                    trimInfinity(input.map { it?.let { log10(it) } }) { log10(it) }
                ),
                arrayOf(
                    Transforms.LOG2, false,
                    input,
                    trimInfinity(input.map { it?.let { log2(it) } }) { log2(it) }
                ),
                arrayOf(
                    Transforms.SYMLOG, false,
                    input,
                    input.map { it?.let(SymlogTransform::transformFun) }
                ),
            )

        }

        private fun trimInfinity(l: List<Double?>, transformFun: (Double) -> Double): List<Double?> {
            return l.map {
                it?.let {
                    if (it.isNaN()) {
                        Double.NaN
                    } else {
                        val lowerLim = LogTransform.calcLowerLimTransformed { v -> transformFun(v) }
                        val upperLim = LogTransform.calcUpperLimTransformed { v -> transformFun(v) }
                        min(max(lowerLim, it), upperLim)
                    }
                }
            }
        }

        private fun assertEqualLists(expected: List<Double?>, actual: List<Double?>, message: String) {
            fun almostEqual(v0: Double?, v1: Double?): Boolean {
                if (v0 == v1) return true
                if (v0 == null || v1 == null) return false
                if (v0.isNaN() && v1.isNaN()) return true
                if (!(v0.isFinite() && v1.isFinite())) return false
                return abs(v0 - v1) <= (v0.absoluteValue + v1.absoluteValue) / 10.0.pow(10)
            }

            if (!expected.zip(actual).all { almostEqual(it.first, it.second) }) {
                failNotEquals(message, expected, actual)
            }
        }
    }
}