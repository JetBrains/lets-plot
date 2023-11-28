/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.UniformDistribution

const val DEF_UNIFORM_A = 0.0
const val DEF_UNIFORM_B = 1.0

fun uniformDensity(a: Double = DEF_UNIFORM_A, b: Double = DEF_UNIFORM_B): (Double) -> Double {
    return UniformDistribution(a, b)::density
}

fun uniformCDF(a: Double = DEF_UNIFORM_A, b: Double = DEF_UNIFORM_B): (Double) -> Double {
    return UniformDistribution(a, b)::cumulativeProbability
}

fun uniformQuantile(a: Double = DEF_UNIFORM_A, b: Double = DEF_UNIFORM_B): (Double) -> Double {
    return UniformDistribution(a, b)::inverseCumulativeProbability
}