/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.TDistribution

const val DEF_T_DEGREES_OF_FREEDOM = 1.0

fun tDensity(df: Double = DEF_T_DEGREES_OF_FREEDOM): (Double) -> Double {
    return TDistribution(df)::density
}

fun tCDF(df: Double = DEF_T_DEGREES_OF_FREEDOM): (Double) -> Double {
    return TDistribution(df)::cumulativeProbability
}

fun tQuantile(df: Double = DEF_T_DEGREES_OF_FREEDOM): (Double) -> Double {
    return TDistribution(df)::inverseCumulativeProbability
}