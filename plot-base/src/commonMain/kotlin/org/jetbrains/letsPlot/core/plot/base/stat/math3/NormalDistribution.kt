/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.math3

import kotlin.jvm.JvmOverloads
import kotlin.math.*

class NormalDistribution
@JvmOverloads constructor(
    private val mean: Double,
    private val standardDeviation: Double
) : AbstractRealDistribution() {
    override val numericalMean: Double = mean
    override val numericalVariance: Double = standardDeviation.pow(2)
    override val supportLowerBound: Double = Double.NEGATIVE_INFINITY
    override val supportUpperBound: Double = Double.POSITIVE_INFINITY
    override val isSupportLowerBoundInclusive: Boolean = false
    override val isSupportUpperBoundInclusive: Boolean = false
    override val isSupportConnected: Boolean = true

    init {
        if (standardDeviation <= 0.0) {
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

    /*
        Based on the paper of G. West, "Better approximations to cumulative normal functions".
        http://www.codeplanet.eu/files/download/accuratecumnorm.pdf
    */
    override fun cumulativeProbability(x: Double): Double {
        val y = (x - mean) / standardDeviation
        if (y < -37.0) return 0.0
        if (y > 37.0) return 1.0

        val a: List<Double> = listOf(
            220.206867912376,
            221.213596169931,
            112.079291497871,
            33.912866078383,
            6.37396220353165,
            0.700383064443688,
            3.52624965998911e-2,
        )
        val b: List<Double> = listOf(
            440.413735824752,
            793.826512519948,
            637.333633378831,
            296.564248779674,
            86.7807322029461,
            16.064177579207,
            1.75566716318264,
            8.83883476483184e-2
        )
        val c = 2.506628274631
        val yAbs = abs(y)
        val exp = E.pow(-yAbs.pow(2) / 2.0)

        val cumNorm = if (yAbs < 7.07106781186547) {
            (exp * ((((((a[6] * yAbs + a[5]) * yAbs + a[4]) * yAbs + a[3]) * yAbs + a[2]) * yAbs + a[1]) * yAbs + a[0])) /
                (((((((b[7] * yAbs + b[6]) * yAbs + b[5]) * yAbs + b[4]) * yAbs + b[3]) * yAbs + b[2]) * yAbs + b[1]) * yAbs + b[0])
        } else {
            exp / ((yAbs + 1.0 / (yAbs + 2.0 / (yAbs + 3.0 / (yAbs + 4.0 / (yAbs + 0.65))))) * c)
        }

        return if (y > 0.0)
            (1 - cumNorm) / standardDeviation
        else
            cumNorm / standardDeviation
    }

    /*
        Based on the paper of M. Wichura, "The Percentage Points of the Normal Distribution".
        http://csg.sph.umich.edu/abecasis/gas_power_calculator/algorithm-as-241-the-percentage-points-of-the-normal-distribution.pdf
    */
    override fun inverseCumulativeProbability(p: Double): Double {
        if (p < 0.0 || p > 1.0) {
            error("OutOfRange [0, 1] - p$p")
        }

        if (p == 0.0) return supportLowerBound
        if (p == 1.0) return supportUpperBound

        var r: Double
        val q: Double = p - 0.5
        var result: Double

        val a: List<Double> = listOf(
            3.387132872796366608,
            1.3314166789178437745e2,
            1.9715909503065514427e3,
            1.3731693765509461125e4,
            4.5921953931549871457e4,
            6.7265770927008700853e4,
            3.3430575583588128105e4,
            2.5090809287301226727e3,
        )
        val b: List<Double> = listOf(
            4.2313330701600911252e1,
            6.871870074920579083e2,
            5.3941960214247511077e3,
            2.1213794301586595867e4,
            3.930789580009271061e4,
            2.8729085735721942674e4,
            5.226495278852854561e3,
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
            result = q * (((((((r * a[7] + a[6]) * r + a[5]) * r + a[4]) * r + a[3]) * r + a[2]) * r + a[1]) * r + a[0]) /
                    (((((((r * b[6] + b[5]) * r + b[4]) * r + b[3]) * r + b[2]) * r + b[1]) * r + b[0]) * r + 1.0)
        } else {
            r = if (q > 0.0) 1.0 - p else p
            r = sqrt(-ln(r))
            if (r <= 5.0) {
                r -= 1.6
                result = (((((((r * c[7] + c[6]) * r + c[5]) * r + c[4]) * r + c[3]) * r + c[2]) * r + c[1]) * r + c[0]) /
                        (((((((r * d[6] + d[5]) * r + d[4]) * r + d[3]) * r + d[2]) * r + d[1]) * r + d[0]) * r + 1.0)
            } else {
                r -= 5.0
                result = (((((((r * e[7] + e[6]) * r + e[5]) * r + e[4]) * r + e[3]) * r + e[2]) * r + e[1]) * r + e[0]) /
                        (((((((r * f[6] + f[5]) * r + f[4]) * r + f[3]) * r + f[2]) * r + f[1]) * r + f[0]) * r + 1.0)
            }
            if (q < 0.0) result = -result
        }

        return mean + standardDeviation * result
    }
}