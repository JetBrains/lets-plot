/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import kotlin.math.pow

abstract class RegressionEvaluator protected constructor(
    xs: List<Double?>,
    ys: List<Double?>,
    confidenceLevel: Double
) {
    abstract val canBeComputed: Boolean
    abstract val degreesOfFreedom: Double

    protected val n: Int
    protected val meanX: Double
    protected val sumXX: Double

    init {
        require(confidenceLevel in 0.01..0.99) { "Confidence level is out of range [0.01-0.99]. CL:$confidenceLevel" }
        require(xs.size == ys.size) { "X/Y must have same size. X:" + xs.size + " Y:" + ys.size }

        val (xVals, yVals) = allFinite(xs, ys)
        n = xVals.size
        meanX = xVals.average()
        sumXX = xVals.sumOf { (it - meanX).pow(2) }
    }

    fun evalX(x: Double): EvalResult {
        require(canBeComputed) { "Regression cannot be computed" }
        return evaluateX(x)
    }

    // Here we suppose that regression can be computed - thanks to evalX() method
    protected abstract fun evaluateX(x: Double): EvalResult
}
