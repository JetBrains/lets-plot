/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

abstract class RegressionEvaluator protected constructor(
    xs: List<Double?>,
    ys: List<Double?>,
    confidenceLevel: Double
) {
    abstract val canBeComputed: Boolean

    init {
        require(confidenceLevel in 0.01..0.99) { "Confidence level is out of range [0.01-0.99]. CL:$confidenceLevel" }
        require(xs.size == ys.size) { "X/Y must have same size. X:" + xs.size + " Y:" + ys.size }
    }

    abstract fun getEvalX(x: Double): EvalResult

    fun evalX(x: Double): EvalResult {
        require(canBeComputed) { "Regression cannot be computed" }
        return getEvalX(x)
    }
}
