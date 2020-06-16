/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.math3

import kotlin.math.pow
import kotlin.math.sqrt


fun mean(xs: DoubleArray) = xs.average()

fun correlationPearson(xs: DoubleArray, ys: DoubleArray): Double {
    require(xs.size == ys.size) { "Two series must have the same size." }
    require(xs.size > 0) { "Can't correlate empty sequences." }

    val mx = mean(xs)
    val my = mean(ys)

    var cov = 0.0
    var d2x = 0.0
    var d2y = 0.0

    for (i in 0 until xs.size) {
        val dx = xs[i] - mx
        val dy = ys[i] - my

        cov += dx * dy
        d2x += dx.pow(2)
        d2y += dy.pow(2)
    }

    require(d2x != 0.0 && d2y != 0.0) { "Correlation is not defined for sequences with zero variation." }

    return cov.div(sqrt(d2x * d2y))
}
