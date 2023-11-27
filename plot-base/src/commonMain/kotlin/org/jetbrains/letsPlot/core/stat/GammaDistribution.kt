/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.GammaDistribution

const val DEF_GAMMA_ALPHA = 1.0
const val DEF_GAMMA_BETA = 1.0

fun gammaDensity(alpha: Double = DEF_GAMMA_ALPHA, beta: Double = DEF_GAMMA_BETA): (Double) -> Double {
    return GammaDistribution(alpha, beta)::density
}

fun gammaCDF(alpha: Double = DEF_GAMMA_ALPHA, beta: Double = DEF_GAMMA_BETA): (Double) -> Double {
    return GammaDistribution(alpha, beta)::cumulativeProbability
}

fun gammaQuantile(alpha: Double = DEF_GAMMA_ALPHA, beta: Double = DEF_GAMMA_BETA): (Double) -> Double {
    return GammaDistribution(alpha, beta)::inverseCumulativeProbability
}