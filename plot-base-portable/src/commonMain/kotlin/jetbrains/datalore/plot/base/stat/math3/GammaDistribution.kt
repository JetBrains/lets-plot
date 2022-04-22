/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.math3

import kotlin.jvm.JvmOverloads
import kotlin.math.*

class GammaDistribution
@JvmOverloads constructor(
    private val alpha: Double,
    private val beta: Double
) : AbstractRealDistribution() {
    private val epsilon = 10e-15

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
            Gamma.regularizedGammaP(alpha, beta * t, epsilon)
        }
        val h = sqrt(epsilon) * x

        return (gamma(x + h) - gamma(x - h)) / (2.0 * h)
    }

    override fun cumulativeProbability(x: Double): Double {
        return Gamma.regularizedGammaP(alpha, beta * x, epsilon)
    }
}