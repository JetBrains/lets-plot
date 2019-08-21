package jetbrains.datalore.visualization.plot.base.stat.regression

import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

internal class SimpleRegressionTest {

    private fun data(
        n: Int,
        yRange: ClosedFloatingPointRange<Double>,
        seed: Int = 1
    ): Pair<List<Double>, List<Double>> {
        return Pair(
            List(n) { it.toDouble() }, // xs
            Random(seed).run { List(n) { nextDouble(yRange.start, yRange.endInclusive) } } // ys
        )
    }

    @Test
    fun simple() {
        val n = 100_000
        val (xs, ys) = data(n, yRange = 0.000_000_001.rangeTo(10_000.0))
        val confidenceLevel = 0.95

        val simpleRegression = SimpleRegression(xs, ys, confidenceLevel)
        val math3LinearRegression = LinearRegression(xs, ys, confidenceLevel)

        var i = 0.0
        while (i < n) {
            assertRegression(math3LinearRegression, simpleRegression, i, epsilon = 0.000_000_000_1)
            i += 0.1
        }
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
        assertTrue(abs(expectedDouble - actualDouble) < epsilon, "$name\n\texp: $expectedDouble\n\tact: $actualDouble")
    }

    private fun assertRegression(
        expectedEval: RegressionEvaluator,
        actualEval: RegressionEvaluator,
        x: Double,
        epsilon: Double
    ) {
        val expRegr = expectedEval.evalX(x)
        val actualRegr = actualEval.evalX(x)

        assertField(expRegr, actualRegr, "se", { it.se }, epsilon)
        assertField(expRegr, actualRegr, "y", { it.y }, epsilon)
        assertField(expRegr, actualRegr, "ymin", { it.ymin }, epsilon)
        assertField(expRegr, actualRegr, "ymax", { it.ymax }, epsilon)
    }
}