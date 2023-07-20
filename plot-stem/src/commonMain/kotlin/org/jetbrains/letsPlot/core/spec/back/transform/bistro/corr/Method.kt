/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr

import kotlin.math.pow
import kotlin.math.sqrt

internal object Method {
    fun correlationPearson(xs: List<Double>, ys: List<Double>): Double {
        require(xs.size == ys.size) { "Two series must have the same size." }
        if (xs.isEmpty()) {
            return Double.NaN
        }

        val mx = xs.average()
        val my = ys.average()

        var cov = 0.0
        var d2x = 0.0
        var d2y = 0.0

        for (i in xs.indices) {
            val dx = xs[i] - mx
            val dy = ys[i] - my

            cov += dx * dy
            d2x += dx.pow(2)
            d2y += dy.pow(2)
        }

        // Correlation is not defined for sequences with zero variation.
        if (d2x == 0.0 || d2y == 0.0) {
            return Double.NaN
        }

        return cov.div(sqrt(d2x * d2y))
    }
}
