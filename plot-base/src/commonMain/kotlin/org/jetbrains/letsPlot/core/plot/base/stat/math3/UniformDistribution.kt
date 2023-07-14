/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.math3

import kotlin.jvm.JvmOverloads
import kotlin.math.*

class UniformDistribution
@JvmOverloads constructor(
    private val a: Double,
    private val b: Double
) : AbstractRealDistribution() {
    override val numericalMean: Double = (a + b) / 2.0
    override val numericalVariance: Double = (b - a).pow(2) / 12.0
    override val supportLowerBound: Double = Double.NEGATIVE_INFINITY
    override val supportUpperBound: Double = Double.POSITIVE_INFINITY
    override val isSupportLowerBoundInclusive: Boolean = false
    override val isSupportUpperBoundInclusive: Boolean = false
    override val isSupportConnected: Boolean = true

    init {
        if (b < a) {
            error("Not a segment [a, b]=[$a, $b]")
        }
    }

    override fun probability(x: Double): Double {
        return 0.0
    }

    override fun density(x: Double): Double {
        return if (x in a..b)
            1.0 / (b - a)
        else
            0.0
    }

    override fun cumulativeProbability(x: Double): Double {
        return when {
            x < a -> 0.0
            x > b -> 1.0
            else -> (x - a) / (b - a)
        }
    }

    override fun inverseCumulativeProbability(p: Double): Double {
        if (p < 0.0 || p > 1.0) {
            error("OutOfRange [0, 1] - p$p")
        }

        if (p == 0.0) return supportLowerBound
        if (p == 1.0) return supportUpperBound

        return a + p * (b - a)
    }
}