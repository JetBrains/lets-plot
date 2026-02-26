package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.math3.Beta
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

internal object NoncentralF {

    /**
     * CDF of the non-central F distribution via Poisson mixture of central F distributions.
     *
     * P(F <= x) = sum_{j>=0} w_j * I_z(df1/2 + j, df2/2),
     * where z = df1*x / (df1*x + df2), and w_j ~ Poisson(lambda/2).
     *
     * This is the standard representation and is the key ingredient needed to reproduce
     * confintr::ci_f_ncp / ci_rsquared logic.
     */
    fun cumulativeProbability(
        x: Double,
        df1: Double,
        df2: Double,
        ncp: Double,
        eps: Double = 1e-12,
        maxTerms: Int = 100000
    ): Double {
        if (x.isNaN() || df1 <= 0.0 || df2 <= 0.0 || ncp < 0.0) return Double.NaN
        if (x <= 0.0) return 0.0
        if (!x.isFinite()) return 1.0

        val z = (df1 * x) / (df1 * x + df2)
        if (!z.isFinite()) return Double.NaN
        if (z <= 0.0) return 0.0
        if (z >= 1.0) return 1.0

        val a0 = df1 / 2.0
        val b = df2 / 2.0
        val mu = ncp / 2.0

        // Central F case
        if (mu == 0.0) {
            return Beta.regularizedBeta(z, a0, b).coerceIn(0.0, 1.0)
        }

        // Poisson weights recurrence from j = 0
        // w0 = exp(-mu), w_{j+1} = w_j * mu / (j+1)
        var w = exp(-mu)
        if (w == 0.0) {
            // For large mu, forward start underflows. Fallback to a center-start summation.
            return cumulativeProbabilityCenterSummation(x, df1, df2, ncp, eps, maxTerms)
        }

        var sum = 0.0
        var weightSum = 0.0
        var j = 0

        while (j < maxTerms) {
            val a = a0 + j
            val termCdf = Beta.regularizedBeta(z, a, b)
            val term = w * termCdf

            sum += term
            weightSum += w

            // stop when remaining probability mass is tiny and terms are tiny
            if (w < eps && (1.0 - weightSum) < 10 * eps) {
                break
            }

            j += 1
            w *= mu / j.toDouble()

            if (!w.isFinite()) return Double.NaN
            if (w == 0.0 && (1.0 - weightSum) < 1e-8) break
        }

        // Numerical guard
        return sum.coerceIn(0.0, 1.0)
    }

    /**
     * More stable fallback for large ncp when exp(-mu) underflows.
     * Start near the Poisson mode and sum both directions with recursive weights.
     */
    private fun cumulativeProbabilityCenterSummation(
        x: Double,
        df1: Double,
        df2: Double,
        ncp: Double,
        eps: Double,
        maxTerms: Int
    ): Double {
        val z = (df1 * x) / (df1 * x + df2)
        val a0 = df1 / 2.0
        val b = df2 / 2.0
        val mu = ncp / 2.0

        val m = kotlin.math.floor(mu).toInt().coerceAtLeast(0)

        // log w_m = -mu + m ln(mu) - ln(m!)
        var logFact = 0.0
        for (k in 2..m) logFact += ln(k.toDouble())
        var wM = exp(-mu + if (m == 0) 0.0 else m * ln(mu) - logFact)

        if (!wM.isFinite() || wM == 0.0) {
            // If still underflowed, we are in an extreme numeric regime.
            // Best effort fallback: return NaN.
            return Double.NaN
        }

        var sum = 0.0
        var weightAccum = 0.0

        // Center term
        run {
            val cdfM = Beta.regularizedBeta(z, a0 + m, b)
            sum += wM * cdfM
            weightAccum += wM
        }

        // Upward
        var wUp = wM
        var j = m
        var upSteps = 0
        while (upSteps < maxTerms) {
            j += 1
            wUp *= mu / j.toDouble()
            if (!wUp.isFinite() || wUp <= 0.0) break

            val cdf = Beta.regularizedBeta(z, a0 + j, b)
            sum += wUp * cdf
            weightAccum += wUp

            upSteps++
            if (wUp < eps) break
        }

        // Downward
        var wDown = wM
        j = m
        var downSteps = 0
        while (j > 0 && downSteps < maxTerms) {
            // w_{j-1} = w_j * j / mu
            wDown *= j.toDouble() / mu
            j -= 1
            if (!wDown.isFinite() || wDown <= 0.0) break

            val cdf = Beta.regularizedBeta(z, a0 + j, b)
            sum += wDown * cdf
            weightAccum += wDown

            downSteps++
            if (wDown < eps) break
        }

        // Not all Poisson mass may be covered in extreme cases; still clamp.
        return sum.coerceIn(0.0, 1.0)
    }
}
