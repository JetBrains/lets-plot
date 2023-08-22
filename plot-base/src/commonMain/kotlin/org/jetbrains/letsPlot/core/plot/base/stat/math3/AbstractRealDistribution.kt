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

import kotlin.math.sqrt

/**
 * Base class for probability distributions on the reals.
 * Default implementations are provided for some of the methods
 * that do not vary from distribution to distribution.
 *
 * @version $Id: AbstractRealDistribution.java 1244107 2012-02-14 16:17:55Z erans $
 * @since 3.0
 */
abstract class AbstractRealDistribution
/** Default constructor.  */
protected constructor() : RealDistribution {

    /** Solver absolute accuracy for inverse cumulative computation  */
    /**
     * Returns the solver absolute accuracy for inverse cumulative computation.
     * You can override this method in order to use a Brent solver with an
     * absolute accuracy different from the default.
     *
     * @return the maximum absolute error in inverse cumulative probability estimates
     */
    protected open val solverAbsoluteAccuracy =
        SOLVER_DEFAULT_ABSOLUTE_ACCURACY

    /**
     * {@inheritDoc}
     *
     * The default implementation uses the identity
     *
     * `P(x0 < X <= x1) = P(X <= x1) - P(X <= x0)`
     */
    override fun cumulativeProbability(x0: Double, x1: Double): Double {
        if (x0 > x1) {
            error("NumberIsTooLarge - x0:$x0, x1:$x1")
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0)
    }

    /**
     * {@inheritDoc}
     *
     * The default implementation returns
     *
     *  * [.getSupportLowerBound] for `p = 0`,
     *  * [.getSupportUpperBound] for `p = 1`.
     *
     */
    override fun inverseCumulativeProbability(p: Double): Double {
        /*
         * IMPLEMENTATION NOTES
         * --------------------
         * Where applicable, use is made of the one-sided Chebyshev inequality
         * to bracket the root. This inequality states that
         * P(X - mu >= k * sig) <= 1 / (1 + k^2),
         * mu: mean, sig: standard deviation. Equivalently
         * 1 - P(X < mu + k * sig) <= 1 / (1 + k^2),
         * F(mu + k * sig) >= k^2 / (1 + k^2).
         *
         * For k = sqrt(p / (1 - p)), we find
         * F(mu + k * sig) >= p,
         * and (mu + k * sig) is an upper-bound for the root.
         *
         * Then, introducing Y = -X, mean(Y) = -mu, sd(Y) = sig, and
         * P(Y >= -mu + k * sig) <= 1 / (1 + k^2),
         * P(-X >= -mu + k * sig) <= 1 / (1 + k^2),
         * P(X <= mu - k * sig) <= 1 / (1 + k^2),
         * F(mu - k * sig) <= 1 / (1 + k^2).
         *
         * For k = sqrt((1 - p) / p), we find
         * F(mu - k * sig) <= p,
         * and (mu - k * sig) is a lower-bound for the root.
         *
         * In cases where the Chebyshev inequality does not apply, geometric
         * progressions 1, 2, 4, ... and -1, -2, -4, ... are used to bracket
         * the root.
         */
        if (p < 0.0 || p > 1.0) {
            error("OutOfRange [0, 1] - p$p")
        }

        var lowerBound = supportLowerBound
        if (p == 0.0) {
            return lowerBound
        }

        var upperBound = supportUpperBound
        if (p == 1.0) {
            return upperBound
        }

        val mu = numericalMean
        val sig = sqrt(numericalVariance)
        val chebyshevApplies: Boolean
        chebyshevApplies = !(mu.isInfinite() || mu.isNaN() || sig.isInfinite() || sig.isNaN())

        if (lowerBound == Double.NEGATIVE_INFINITY) {
            if (chebyshevApplies) {
                lowerBound = mu - sig * sqrt((1.0 - p) / p)
            } else {
                lowerBound = -1.0
                while (cumulativeProbability(lowerBound) >= p) {
                    lowerBound *= 2.0
                }
            }
        }

        if (upperBound == Double.POSITIVE_INFINITY) {
            if (chebyshevApplies) {
                upperBound = mu + sig * sqrt(p / (1.0 - p))
            } else {
                upperBound = 1.0
                while (cumulativeProbability(upperBound) < p) {
                    upperBound *= 2.0
                }
            }
        }

        val toSolve = object : UnivariateFunction {
            override fun value(x: Double): Double {
                return cumulativeProbability(x) - p
            }
        }

        val x = UnivariateSolverUtils.solve(
            toSolve,
            lowerBound,
            upperBound,
            solverAbsoluteAccuracy
        )

        if (!isSupportConnected) {
            /* Test for plateau. */
            val dx = solverAbsoluteAccuracy
            if (x - dx >= supportLowerBound) {
                val px = cumulativeProbability(x)
                if (cumulativeProbability(x - dx) == px) {
                    upperBound = x
                    while (upperBound - lowerBound > dx) {
                        val midPoint = 0.5 * (lowerBound + upperBound)
                        if (cumulativeProbability(midPoint) < px) {
                            lowerBound = midPoint
                        } else {
                            upperBound = midPoint
                        }
                    }
                    return upperBound
                }
            }
        }
        return x
    }

    companion object {
        /** Default accuracy.  */
        val SOLVER_DEFAULT_ABSOLUTE_ACCURACY = 1e-6

        /** Serializable version identifier  */
        private const val serialVersionUID = -38038050983108802L
    }
}

