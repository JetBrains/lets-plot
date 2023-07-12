/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.regression.EvalResult
import org.jetbrains.letsPlot.core.plot.base.stat.regression.RegressionEvaluator
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertTrue

internal object RegressionTestUtil {

    fun data(
        n: Int,
        yRange: ClosedFloatingPointRange<Double>,
        seed: Int = 1
    ): Pair<List<Double>, List<Double>> {
        return Pair(
            List(n) { it.toDouble() }, // xs
            Random(seed).run { List(n) { nextDouble(yRange.start, yRange.endInclusive) } } // ys
        )
    }

    private fun assertField(
        expected: EvalResult,
        actual: EvalResult,
        name: String,
        selector: (EvalResult) -> Double,
        epsilon: Double
    ) {
        val expectedDouble = selector(expected)
        val actualDouble = selector(actual)
        assertTrue(
            abs(expectedDouble - actualDouble) < epsilon, "$name\n\texp: $expectedDouble\n\tact: $actualDouble")
    }

    fun assertRegression(
        expectedEval: RegressionEvaluator,
        actualEval: RegressionEvaluator,
        x: Double,
        epsilon: Double
    ) {
        val expRegr = expectedEval.evalX(x)
        val actualRegr = actualEval.evalX(x)

        assertField(expRegr, actualRegr, "x: $x se", { it.se }, epsilon)
        assertField(expRegr, actualRegr, "x: $x y", { it.y }, epsilon)
        assertField(expRegr, actualRegr, "x: $x ymin", { it.ymin }, epsilon)
        assertField(expRegr, actualRegr, "x: $x ymax", { it.ymax }, epsilon)
    }

    fun logRegression(inX: List<Double>, inY: List<Double>, regression: RegressionEvaluator) {

        val inXLog = inX.joinToString(transform = Double::toString)
        val inYLog = inY.joinToString(transform = Double::toString)

        val step = (inX.maxOrNull()!! - inX.minOrNull()!!) / inX.size * 2

        val log = generateSequence(inX.minOrNull()!!) { it + step }
            .takeWhile { it < inX.maxOrNull()!! }
            .map { Pair( it, regression.evalX(it)) }

        println("val inX = listOf($inXLog)")
        println("val inY = listOf($inYLog)")
        println("val actX = listOf(${log.joinToString { it.first.toString() }})")
        println("val actY = listOf(${log.joinToString { it.second.toString() }})")
    }
}