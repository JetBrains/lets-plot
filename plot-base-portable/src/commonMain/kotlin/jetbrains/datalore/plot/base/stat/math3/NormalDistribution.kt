/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.math3

import kotlin.jvm.JvmOverloads
import kotlin.math.*

class NormalDistribution
@JvmOverloads constructor(
    private val mean: Double,
    private val standardDeviation: Double,
    private val accuracy: Int = 1_000
) : AbstractRealDistribution() {
    // TODO: Refactor
    private val cumProbValuesCache: MutableMap<Double, Double> = mutableMapOf()
    // TODO: Refactor
    private val step: Double = 6.0 * standardDeviation / accuracy
    // TODO: Refactor
    override val numericalMean: Double = mean
    override val numericalVariance: Double = standardDeviation.pow(2)
    override val supportLowerBound: Double = Double.NEGATIVE_INFINITY
    override val supportUpperBound: Double = Double.POSITIVE_INFINITY
    override val isSupportLowerBoundInclusive: Boolean = false
    override val isSupportUpperBoundInclusive: Boolean = false
    override val isSupportConnected: Boolean = true

    init {
        if (standardDeviation <= 0) {
            error("NotStrictlyPositive - STANDARD_DEVIATION: $standardDeviation")
        }
    }

    override fun probability(x: Double): Double {
        return 0.0
    }

    override fun density(x: Double): Double {
        return 1.0 / (standardDeviation * sqrt(2.0 * PI)) * E.pow(
            -0.5 * ((x - mean) / standardDeviation).pow(2)
        )
    }

    // TODO: Refactor, optimize, fix errors
    // TODO: `n` in calculateIntegral() should be a parameter
    override fun cumulativeProbability(x: Double): Double {
        // TODO: Change to the better one method
        val calculateIntegral: (Double, Double, Int, (Double) -> Double) -> Double = { a, b, n, f ->
            var sum = 0.0
            for (k in 0..n)
                sum += f(a + k * (b - a) / n)
            (b - a) / n * sum
        }
        val transformedDensity: (Double) -> (Double) -> Double = { a ->
            { t ->
                if (t == 0.0)
                    0.0
                else
                    density(a - (1.0 - t) / t) / t.pow(2)
            }
        }

        // TODO: Refactor
        if (!cumProbValuesCache.any()) {
            for (k in -3..3) {
                val xk = mean + k * standardDeviation
                cumProbValuesCache[xk] = calculateIntegral(0.0, 1.0, accuracy, transformedDensity(xk))
            }
        }
        val xNearest: Double = cumProbValuesCache.keys.minByOrNull { abs(x - it) }!!
        val cumProbValueNearest: Double = cumProbValuesCache[xNearest]!!
        val n: Int = round(abs(x - xNearest) / step).toInt()

        return when {
            n < 1 -> cumProbValueNearest
            x > xNearest -> cumProbValueNearest + calculateIntegral(xNearest, x, n) { density(it) }
            x < xNearest -> cumProbValueNearest - calculateIntegral(x, xNearest, n) { density(it) }
            else -> throw IllegalArgumentException("x should be finite, but it isn't: $x")
        }
    }
}