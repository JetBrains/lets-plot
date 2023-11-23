/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.GammaDistribution

const val DEF_CHI2_K = 1.0

fun chi2Density(k: Double = DEF_CHI2_K): (Double) -> Double {
    return GammaDistribution(k / 2.0, 0.5)::density
}

fun chi2CDF(k: Double = DEF_CHI2_K): (Double) -> Double {
    return GammaDistribution(k / 2.0, 0.5)::cumulativeProbability
}

fun chi2Quantile(k: Double = DEF_CHI2_K): (Double) -> Double {
    return GammaDistribution(k / 2.0, 0.5)::inverseCumulativeProbability
}