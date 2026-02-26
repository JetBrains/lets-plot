/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

data class R2ConfIntResult(
    val level: Double,
    val low: Double,
    val high: Double
)

internal object RsquaredCI {

    // confintr::f_to_r2
    // f / (f + df2 / df1)
    fun fToR2(f: Double, df1: Double, df2: Double): Double {
        if (!f.isFinite() || f < 0.0 || df1 <= 0.0 || df2 <= 0.0) return Double.NaN
        return (f / (f + df2 / df1)).coerceIn(0.0, 1.0)
    }

    // confintr::ncp_to_r2
    // ncp / (ncp + df1 + df2 + 1)
    fun ncpToR2(ncp: Double, df1: Double, df2: Double): Double {
        if (ncp.isNaN() || ncp < 0.0 || df1 <= 0.0 || df2 <= 0.0) return Double.NaN
        if (ncp == Double.POSITIVE_INFINITY) return 1.0
        return (ncp / (ncp + df1 + df2 + 1.0)).coerceIn(0.0, 1.0)
    }

    /**
     * CI for population R^2 using the same method as confintr::ci_rsquared():
     * test inversion for noncentral F NCP, then map NCP -> R^2.
     */
    fun ciRsquaredLikeConfintr(
        fStat: Double,
        df1: Double,
        df2: Double,
        confidenceLevel: Double
    ): R2ConfIntResult {
        val level = confidenceLevel
        if (!fStat.isFinite() || fStat < 0.0 || df1 <= 0.0 || df2 <= 0.0 || level <= 0.0 || level >= 1.0) {
            return R2ConfIntResult(level, Double.NaN, Double.NaN)
        }

        val alpha = 1.0 - level
        val probsLow = alpha / 2.0
        val probsHigh = 1.0 - alpha / 2.0

        val ncpCi = FNoncentralityCI.ciFNoncentrality(
            fStat = fStat,
            df1 = df1,
            df2 = df2,
            probsLow = probsLow,
            probsHigh = probsHigh
        )

        val low = ncpToR2(ncpCi.low, df1, df2)
        val high = ncpToR2(ncpCi.high, df1, df2)

        return R2ConfIntResult(level, low, high)
    }

    /**
     * Convenience wrapper from your regression summary fields.
     * eqSize includes intercept, so p = eqSize - 1 = df1.
     */
    fun ciRsquaredLikeConfintrFromR2(
        n: Int,
        eqSize: Int,
        r2: Double,
        confidenceLevel: Double
    ): R2ConfIntResult {
        if (n <= 0 || eqSize <= 0 || !r2.isFinite()) {
            return R2ConfIntResult(confidenceLevel, Double.NaN, Double.NaN)
        }

        val df1 = (eqSize - 1).toDouble()
        val df2 = n - eqSize.toDouble() // same as n - p - 1

        if (df1 <= 0.0 || df2 <= 0.0) {
            return R2ConfIntResult(confidenceLevel, Double.NaN, Double.NaN)
        }

        val r2c = r2.coerceIn(0.0, 1.0)

        // Convert observed R² -> observed F (same relationship used in confintr helpers)
        val fStat = when {
            r2c == 0.0 -> 0.0
            r2c == 1.0 -> Double.POSITIVE_INFINITY
            else -> (r2c / (1.0 - r2c)) * (df2 / df1)
        }

        if (fStat == Double.POSITIVE_INFINITY) {
            return R2ConfIntResult(confidenceLevel, 1.0, 1.0)
        }

        return ciRsquaredLikeConfintr(
            fStat = fStat,
            df1 = df1,
            df2 = df2,
            confidenceLevel = confidenceLevel
        )
    }
}
