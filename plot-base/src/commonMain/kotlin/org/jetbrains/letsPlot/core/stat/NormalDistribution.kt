/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.NormalDistribution

const val DEF_NORMAL_MEAN = 0.0
const val DEF_NORMAL_STD = 1.0

fun normalDensity(mean: Double = DEF_NORMAL_MEAN, std: Double = DEF_NORMAL_STD): (Double) -> Double {
    return NormalDistribution(mean, std)::density
}

fun normalCDF(mean: Double = DEF_NORMAL_MEAN, std: Double = DEF_NORMAL_STD): (Double) -> Double {
    return NormalDistribution(mean, std)::cumulativeProbability
}

fun normalQuantile(mean: Double = DEF_NORMAL_MEAN, std: Double = DEF_NORMAL_STD): (Double) -> Double {
    return NormalDistribution(mean, std)::inverseCumulativeProbability
}