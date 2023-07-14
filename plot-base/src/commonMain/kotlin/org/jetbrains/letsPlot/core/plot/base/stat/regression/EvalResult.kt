/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

data class EvalResult(
    val y: Double,    // predicted value
    val ymin: Double, // lower pointwise confidence interval around the mean
    val ymax: Double, // upper pointwise confidence interval around the mean
    val se: Double    // standard error
)
