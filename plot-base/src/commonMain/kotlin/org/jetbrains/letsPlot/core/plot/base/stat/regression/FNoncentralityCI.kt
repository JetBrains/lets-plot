package org.jetbrains.letsPlot.core.plot.base.stat.regression

internal data class NcpConfIntResult(
    val estimate: Double,
    val low: Double,
    val high: Double
)

internal object FNoncentralityCI {

    // Same mapping as confintr source (Smithson p. 38), visible in ci_f_ncp code:
    // f_to_ncp <- function(f, df1, df2) { df1 * f * (df1 + df2 + 1) / df2 }
    fun fToNcp(f: Double, df1: Double, df2: Double): Double {
        if (!f.isFinite() || f < 0.0 || df1 <= 0.0 || df2 <= 0.0) return Double.NaN
        return df1 * f * (df1 + df2 + 1.0) / df2
    }

    /**
     * Two-sided CI for non-centrality parameter lambda of the F distribution.
     * Mirrors confintr::ci_f_ncp logic (test inversion on noncentral F CDF).
     *
     * probs are lower/upper cumulative probs for the CI limits, e.g. (0.025, 0.975).
     */
    fun ciFNoncentrality(
        fStat: Double,
        df1: Double,
        df2: Double,
        probsLow: Double,
        probsHigh: Double,
        absTol: Double = 1e-10
    ): NcpConfIntResult {
        if (!fStat.isFinite() || fStat < 0.0 || df1 <= 0.0 || df2 <= 0.0) {
            return NcpConfIntResult(Double.NaN, Double.NaN, Double.NaN)
        }
        if (!(probsLow in 0.0..1.0) || !(probsHigh in 0.0..1.0) || probsLow > probsHigh) {
            return NcpConfIntResult(Double.NaN, Double.NaN, Double.NaN)
        }

        val estimate = fToNcp(fStat, df1, df2)
        if (!estimate.isFinite()) {
            return NcpConfIntResult(Double.NaN, Double.NaN, Double.NaN)
        }

        // confintr uses iprobs = 1 - probs
        val targetLower = 1.0 - probsLow   // for lower CI root
        val targetUpper = 1.0 - probsHigh  // for upper CI root

        val low = if (probsLow == 0.0) {
            0.0
        } else {
            val fn: (Double) -> Double = { ncp ->
                NoncentralF.cumulativeProbability(fStat, df1, df2, ncp) - targetLower
            }
            // R code searches interval [0, estimate]
            bisectionRootOrNull(fn, 0.0, estimate.coerceAtLeast(0.0), absTol) ?: 0.0
        }

        val high = if (probsHigh == 1.0) {
            Double.POSITIVE_INFINITY
        } else {
            val fn: (Double) -> Double = { ncp ->
                NoncentralF.cumulativeProbability(fStat, df1, df2, ncp) - targetUpper
            }

            // confintr upper heuristic:
            // pmax(4 * estimate, stat * df1 * 4, df1 * 100)
            var upper = maxOf(4.0 * estimate, fStat * df1 * 4.0, df1 * 100.0)

            // expand if needed until sign change
            var root = bisectionRootOrNull(fn, estimate.coerceAtLeast(0.0), upper, absTol)
            var tries = 0
            while (root == null && tries < 20 && upper.isFinite()) {
                upper *= 2.0
                root = bisectionRootOrNull(fn, estimate.coerceAtLeast(0.0), upper, absTol)
                tries++
            }
            root ?: Double.POSITIVE_INFINITY
        }

        return NcpConfIntResult(estimate, low, high)
    }

    /**
     * Monotone root search by bisection. Returns null if no sign change / invalid.
     */
    private fun bisectionRootOrNull(
        f: (Double) -> Double,
        a0: Double,
        b0: Double,
        absTol: Double,
        maxIter: Int = 200
    ): Double? {
        var a = a0
        var b = b0
        if (!a.isFinite() || !b.isFinite() || a > b) return null

        var fa = f(a)
        var fb = f(b)
        if (!fa.isFinite() || !fb.isFinite()) return null

        // exact hits
        if (fa == 0.0) return a
        if (fb == 0.0) return b

        // need sign change
        if (fa * fb > 0.0) return null

        repeat(maxIter) {
            val m = 0.5 * (a + b)
            val fm = f(m)
            if (!fm.isFinite()) return null

            if (fm == 0.0) return m
            if ((b - a) <= absTol * (1.0 + kotlin.math.abs(a) + kotlin.math.abs(b))) {
                return 0.5 * (a + b)
            }

            if (fa * fm <= 0.0) {
                b = m
                fb = fm
            } else {
                a = m
                fa = fm
            }
        }

        return 0.5 * (a + b)
    }
}
