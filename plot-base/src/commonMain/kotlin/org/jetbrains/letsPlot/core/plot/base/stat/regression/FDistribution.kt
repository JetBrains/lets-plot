/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.math3.Beta
import kotlin.math.exp
import kotlin.math.ln

/**
 * Implementation of Fisher-Snedecor F-distribution.
 *
 * @param numeratorDegreesOfFreedom numerator degrees of freedom (df1), must be > 0
 * @param denominatorDegreesOfFreedom denominator degrees of freedom (df2), must be > 0
 */
internal class FDistribution(
    private val numeratorDegreesOfFreedom: Double,
    private val denominatorDegreesOfFreedom: Double
) {

    init {
        if (numeratorDegreesOfFreedom <= 0.0) {
            error("NotStrictlyPositive - NUMERATOR_DEGREES_OF_FREEDOM: $numeratorDegreesOfFreedom")
        }
        if (denominatorDegreesOfFreedom <= 0.0) {
            error("NotStrictlyPositive - DENOMINATOR_DEGREES_OF_FREEDOM: $denominatorDegreesOfFreedom")
        }
    }

    fun density(x: Double): Double {
        if (x < 0.0) return 0.0
        if (x == 0.0) {
            // density at 0 can be finite/infinite depending on df1; returning exact handling is optional
            return when {
                numeratorDegreesOfFreedom < 2.0 -> Double.POSITIVE_INFINITY
                numeratorDegreesOfFreedom == 2.0 -> 1.0
                else -> 0.0
            }
        }

        val d1 = numeratorDegreesOfFreedom
        val d2 = denominatorDegreesOfFreedom

        // log-density for numerical stability:
        // f(x) = (d1/d2)^(d1/2) * x^(d1/2 - 1) / B(d1/2, d2/2) * (1 + d1*x/d2)^(-(d1+d2)/2)
        val nhalf = d1 / 2.0
        val mhalf = d2 / 2.0

        val logNumerator =
            nhalf * ln(d1 / d2) +
                    (nhalf - 1.0) * ln(x) -
                    Beta.logBeta(nhalf, mhalf)

        val logDenominatorPart = ((d1 + d2) / 2.0) * ln(1.0 + (d1 / d2) * x)

        return exp(logNumerator - logDenominatorPart)
    }

    fun cumulativeProbability(x: Double): Double {
        if (x <= 0.0) return 0.0
        if (x.isNaN()) return Double.NaN

        val d1 = numeratorDegreesOfFreedom
        val d2 = denominatorDegreesOfFreedom

        // CDF relation:
        // F(x; d1, d2) = I_{ d1*x / (d1*x + d2) }(d1/2, d2/2)
        val z = (d1 * x) / (d1 * x + d2)

        return Beta.regularizedBeta(z, d1 / 2.0, d2 / 2.0)
    }

    fun inverseCumulativeProbability(p: Double, absAccuracy: Double = 1e-9): Double {
        if (p.isNaN() || p < 0.0 || p > 1.0) {
            error("OutOfRange - p: $p")
        }
        if (p == 0.0) return 0.0
        if (p == 1.0) return Double.POSITIVE_INFINITY

        var lo = 0.0
        var hi = 1.0

        // Expand upper bound until CDF(hi) >= p
        while (true) {
            val cdfHi = cumulativeProbability(hi)
            if (!cdfHi.isFinite() || cdfHi >= p) break

            hi *= 2.0
            if (!hi.isFinite() || hi > 1e12) {
                // F quantiles can be huge for extreme probs / dfs; return best effort bound
                break
            }
        }

        // Binary search
        repeat(200) {
            val mid = (lo + hi) / 2.0
            val cdfMid = cumulativeProbability(mid)

            if (!cdfMid.isFinite()) {
                hi = mid
            } else if (cdfMid < p) {
                lo = mid
            } else {
                hi = mid
            }

            if ((hi - lo) <= absAccuracy * (1.0 + lo + hi)) {
                return (lo + hi) / 2.0
            }
        }

        return (lo + hi) / 2.0
    }

}