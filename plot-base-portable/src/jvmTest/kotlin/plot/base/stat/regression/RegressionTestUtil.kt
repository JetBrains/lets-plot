/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

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

        assertField(expRegr, actualRegr, "i: $x se", { it.se }, epsilon)
        assertField(expRegr, actualRegr, "i: $x y", { it.y }, epsilon)
        assertField(expRegr, actualRegr, "i: $x ymin", { it.ymin }, epsilon)
        assertField(expRegr, actualRegr, "i: $x ymax", { it.ymax }, epsilon)
    }
}