/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.math3.NormalDistribution
import kotlin.math.*

class QQStat(private val version: Int? = 1) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val xs = data.getNumeric(TransformVar.X)
        val statY = xs.filter { it?.isFinite() == true }.map { it!! }.sorted()

        val t = (1..statY.size).map { (it - 0.5) / statY.size }
        // TODO: Versions of calculation of quantile function for normal distribution
        val statX = when (version) {
            1 -> {
                val dist = NormalDistribution(0.0, 1.0, 1_000)
                t.map { dist.inverseCumulativeProbability(it) }
            }
            2 -> {
                val dist = NormalDistribution(0.0, 1.0, 10_000)
                t.map { dist.inverseCumulativeProbability(it) }
            }
            3 -> t.map { normInvCDF(it) }
            4 -> t.map { wichuraNormInvCDF(it, 0.0, 1.0) }
            else -> t.map { wichuraNormInvCDF(it, 0.0, 1.0) }
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.Y, statY)
            .build()
    }

    // https://web.archive.org/web/20070505093933/http://home.online.no/~pjacklam/notes/invnorm/
    fun normInvCDF(p: Double): Double {
        // Coefficients in rational approximations.
        val a = listOf(
            -3.969683028665376e+01,
            2.209460984245205e+02,
            -2.759285104469687e+02,
            1.383577518672690e+02,
            -3.066479806614716e+01,
            2.506628277459239e+00,
        )
        val b = listOf(
            -5.447609879822406e+01,
            1.615858368580409e+02,
            -1.556989798598866e+02,
            6.680131188771972e+01,
            -1.328068155288572e+01,
        )
        val c = listOf(
            -7.784894002430293e-03,
            -3.223964580411365e-01,
            -2.400758277161838e+00,
            -2.549732539343734e+00,
            4.374664141464968e+00,
            2.938163982698783e+00,
        )
        val d = listOf(
            7.784695709041462e-03,
            3.224671290700398e-01,
            2.445134137142996e+00,
            3.754408661907416e+00,
        )

        // Define break-points.
        val pLow = 0.02425
        val pHigh = 1.0 - pLow

        // Rational approximation by regions
        return when {
            0.0 < p && p < pLow -> {
                val q = sqrt(-2 * ln(p))
                (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]) / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1)
            }
            p in pLow..pHigh -> {
                val q = p - 0.5
                val r = q.pow(2)
                (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q / (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1)
            }
            pHigh < p && p < 1.0 -> {
                val q = sqrt(-2 * ln(1 - p))
                -(((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]) / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1)
            }
            else -> throw IllegalArgumentException("p should be in [0, 1], but it isn't: $p")
        }
    }

    // http://csg.sph.umich.edu/abecasis/gas_power_calculator/algorithm-as-241-the-percentage-points-of-the-normal-distribution.pdf
    private fun wichuraNormInvCDF(p: Double, mu: Double, sigma: Double): Double {
        if (p < 0.0 || p > 1.0)
            throw IllegalArgumentException("The probality p must be bigger than 0 and smaller than 1")
        if (sigma < 0.0)
            throw IllegalArgumentException("The standard deviation sigma must be positive")

        if (p == 0.0)
            return Double.NEGATIVE_INFINITY
        if (p == 1.0)
            return Double.POSITIVE_INFINITY
        if (sigma == 0.0)
            return mu

        var r: Double
        var value: Double
        val q: Double = p - 0.5

        val a: List<Double> = listOf(
            3.387132872796366608,
            1.3314166789178437745e-2,
            1.9715909503065514427e-3,
            1.3731693765509461125e-4,
            4.5921953931549871457e-4,
            6.7265770927008700853e-4,
            3.3430575583588128105e-4,
            2.5090809287301226727e-3,
        )
        val b: List<Double> = listOf(
            4.2313330701600911252e-1,
            6.871870074920579083e-2,
            5.3941960214247511077e-3,
            2.1213794301586595867e-4,
            3.930789580009271061e-4,
            2.8729085735721942674e-4,
            5.226495278852854561e-3,
        )
        val c: List<Double> = listOf(
            1.42343711074968357734,
            4.6303378461565452959,
            5.7694972214606914055,
            3.64784832476320460504,
            1.27045825245236838258,
            2.4178072517745061177e-1,
            2.27238449892691845833e-2,
            7.7454501427834140764e-4,
        )
        val d: List<Double> = listOf(
            2.05319162663775882187,
            1.6763848301838038494,
            6.8976733498510000455e-1,
            1.4810397642748007459e-1,
            1.51986665636164571966e-2,
            5.475938084995344946e-4,
            1.05075007164441684324e-9,
        )
        val e: List<Double> = listOf(
            6.6579046435011037772,
            5.4637849111641143699,
            1.7848265399172913358,
            2.9656057182850489123e-1,
            2.6532189526576123093e-2,
            1.2426609473880784386e-3,
            2.71155556874348757815e-5,
            2.01033439929228813265e-7,
        )
        val f: List<Double> = listOf(
            5.9983220655588793769e-1,
            1.3692988092273580531e-1,
            1.48753612908506148525e-2,
            7.868691311456132591e-4,
            1.8463183175100546818e-5,
            1.4215117583164458887e-7,
            2.04426310338993978564e-15,
        )

        if (abs(q) <= 0.425) {
            r = 0.180625 - q.pow(2)
            value = q * (((((((r * a[7] + a[6]) * r + a[5]) * r + a[4]) * r + a[3]) * r + a[2]) * r + a[1]) * r + a[0]) /
                    (((((((r * b[6] + b[5]) * r + b[4]) * r + b[3]) * r + b[2]) * r + b[1]) * r + b[0]) * r + 1.0)
        } else {
            r = if (q > 0.0) 1.0 - p else p
            r = sqrt(-ln(r))
            if (r <= 5.0) {
                r -= 1.6
                value = (((((((r * c[7] + c[6]) * r + c[5]) * r + c[4]) * r + c[3]) * r + c[2]) * r + c[1]) * r + c[0]) /
                        (((((((r * d[6] + d[5]) * r + d[4]) * r + d[3]) * r + d[2]) * r + d[1]) * r + d[0]) * r + 1.0)
            } else {
                r -= 5.0
                value = (((((((r * e[7] + e[6]) * r + e[5]) * r + e[4]) * r + e[3]) * r + e[2]) * r + e[1]) * r + e[0]) /
                        (((((((r * f[6] + f[5]) * r + f[4]) * r + f[3]) * r + f[2]) * r + f[1]) * r + f[0]) * r + 1.0)
            }
            if (q < 0.0) value = -value
        }

        return mu + sigma * value
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}