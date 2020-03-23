/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.math3

import jetbrains.datalore.base.gcommon.base.Preconditions
import kotlin.math.pow

class ForsythePolynomialGenerator(private val knots: DoubleArray) {
    private var ps: ArrayList<PolynomialFunction>

    init {
        Preconditions.checkArgument(
            knots.isNotEmpty(),
            "The knots list must not be empty"
        )

        val xMean = knots.sum().div(knots.size)

        ps = arrayListOf(
            PolynomialFunction(doubleArrayOf(1.0)),
            PolynomialFunction(doubleArrayOf(-xMean, 1.0))
        )
    }

    private fun alphaBeta(i: Int): Pair<Double, Double> {
        if (i != ps.size)
            error("Alpha must be calculated sequentially.")

        val p = ps.last()
        val pp = ps[ps.size - 2]
        var sxp = 0.0
        var sp2 = 0.0
        var spp2 = 0.0

        for (x in knots) {
            val pv2 = p.value(x).pow(2)
            val ppv2 = pp.value(x).pow(2)
            sxp += x * pv2
            sp2 += pv2
            spp2 += ppv2
        }

        return Pair(sxp / sp2, sp2 / spp2)
    }

    fun getPolynomial(n: Int): PolynomialFunction {
        if (n < 0)
            error("Degree of Forsythe polynomial must not be negative")

        if (n >= knots.size)
            error("Degree of Forsythe polynomial must not exceed knots.size - 1")

        if (n >= ps.size) {
            val sz = ps.size

            for (k in sz..n + 1) {
                val (a, b) = alphaBeta(k)
                val pprev = ps.last()
                val pprevprev = ps[ps.size - 2]
                val p = X * pprev - a * pprev - b * pprevprev
                ps.add(p)
            }
        }

        return ps[n]
    }


    companion object {
        val X = PolynomialFunction(doubleArrayOf(0.0, 1.0))
    }

}

