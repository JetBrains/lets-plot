/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.stat.regression.LocalPolynomialRegression
import jetbrains.datalore.plot.base.stat.regression.RegressionEvaluator
import jetbrains.datalore.plot.base.stat.regression.SimpleRegression

object SmoothingMethods {
    fun lm(valuesX: List<Double?>, valuesY: List<Double?>, confidenceLevel: Double): RegressionEvaluator {
        return SimpleRegression(valuesX, valuesY, confidenceLevel)
    }

    fun loess(valuesX: List<Double?>, valuesY: List<Double?>, confidenceLevel: Double): RegressionEvaluator {
        return LocalPolynomialRegression(valuesX, valuesY, confidenceLevel)
    }
}