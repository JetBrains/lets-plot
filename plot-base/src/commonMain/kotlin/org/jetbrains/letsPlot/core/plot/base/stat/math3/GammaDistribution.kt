/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.math3

import kotlin.jvm.JvmOverloads
import kotlin.math.pow
import kotlin.math.sqrt

internal class GammaDistribution
@JvmOverloads constructor(
    private val alpha: Double,
    private val beta: Double,
    private val gammaEpsilon: Double = DEFAULT_GAMMA_EPSILON,
    override val solverAbsoluteAccuracy: Double = DEFAULT_INVERSE_ABSOLUTE_ACCURACY
) : AbstractRealDistribution() {

    override val numericalMean: Double = alpha / beta
    override val numericalVariance: Double = alpha / beta.pow(2)
    override val supportLowerBound: Double = 0.0
    override val supportUpperBound: Double = Double.POSITIVE_INFINITY
    override val isSupportLowerBoundInclusive: Boolean = false
    override val isSupportUpperBoundInclusive: Boolean = false
    override val isSupportConnected: Boolean = true

    init {
        if (alpha <= 0.0) {
            error("NotStrictlyPositive - alpha: $alpha")
        }
        if (beta <= 0.0) {
            error("NotStrictlyPositive - beta: $beta")
        }
    }

    override fun probability(x: Double): Double {
        return 0.0
    }

    override fun density(x: Double): Double {
        val gamma: (Double) -> Double = { t ->
            Gamma.regularizedGammaP(alpha, beta * t, gammaEpsilon)
        }
        val h = sqrt(gammaEpsilon) * x

        return (gamma(x + h) - gamma(x - h)) / (2.0 * h)
    }

    override fun cumulativeProbability(x: Double): Double {
        if (x <= 0.0) return 0.0
        if (x > 37.0) return 1.0
        return Gamma.regularizedGammaP(alpha, beta * x, gammaEpsilon)
    }

    companion object {
        const val DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9
        const val DEFAULT_GAMMA_EPSILON = 1e-14
    }
}