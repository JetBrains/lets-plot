/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.GammaDistribution

const val DEF_EXP_LAMBDA = 1.0

fun expDensity(lambda: Double = DEF_EXP_LAMBDA): (Double) -> Double {
    return GammaDistribution(1.0, lambda)::density
}

fun expCDF(lambda: Double = DEF_EXP_LAMBDA): (Double) -> Double {
    return GammaDistribution(1.0, lambda)::cumulativeProbability
}

fun expQuantile(lambda: Double = DEF_EXP_LAMBDA): (Double) -> Double {
    return GammaDistribution(1.0, lambda)::inverseCumulativeProbability
}