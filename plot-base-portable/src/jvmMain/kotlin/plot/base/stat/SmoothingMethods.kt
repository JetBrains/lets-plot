/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.stat.regression.LoessRegression
import jetbrains.datalore.plot.base.stat.regression.RegressionEvaluator
import jetbrains.datalore.plot.base.stat.regression.SimpleRegression

actual object SmoothingMethods {
    actual fun lm(valuesX: List<Double?>, valuesY: List<Double?>, confidenceLevel: Double): RegressionEvaluator {
        return SimpleRegression(valuesX, valuesY, confidenceLevel)
    }

    actual fun loess(valuesX: List<Double?>, valuesY: List<Double?>, confidenceLevel: Double): RegressionEvaluator {
        return LoessRegression(valuesX, valuesY, confidenceLevel)
    }
}