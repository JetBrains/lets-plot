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
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.ln


/**
 * Implementation of Student's t-distribution.
 *
 * @see "[&apos;Student's t-distribution
 * @see "[Student's t-distribution
 * @version $Id: TDistribution.java 1244107 2012-02-14 16:17:55Z erans $
](mathworld.wolfram.com/Studentst-Distribution.html)](https://en.wikipedia.org/wiki/Student&apos;s_t-distribution) */
class TDistribution
/**
 * Create a t distribution using the given degrees of freedom and the
 * specified inverse cumulative probability absolute accuracy.
 *
 * @param degreesOfFreedom Degrees of freedom.
 * @param inverseCumAccuracy the maximum absolute error in inverse
 * cumulative probability estimates
 * (defaults to [.DEFAULT_INVERSE_ABSOLUTE_ACCURACY]).
 * @throws NotStrictlyPositiveException if `degreesOfFreedom <= 0`
 * @since 2.1
 */
@JvmOverloads constructor(
    /** The degrees of freedom.  */
    /**
     * Access the degrees of freedom.
     *
     * @return the degrees of freedom.
     */
    private val degreesOfFreedom: Double,
    /** Inverse cumulative probability accuracy.  */
    /** {@inheritDoc}  */
    override val solverAbsoluteAccuracy: Double = DEFAULT_INVERSE_ABSOLUTE_ACCURACY
) : AbstractRealDistribution() {

    /**
     * {@inheritDoc}
     *
     * For degrees of freedom parameter `df`, the mean is
     *
     *  * if `df > 1` then `0`,
     *  * else undefined (`Double.NaN`).
     *
     */
    override val numericalMean: Double
        get() {
            val df = degreesOfFreedom

            return if (df > 1) {
                0.0
            } else Double.NaN

        }

    /**
     * {@inheritDoc}
     *
     * For degrees of freedom parameter `df`, the variance is
     *
     *  * if `df > 2` then `df / (df - 2)`,
     *  * if `1 < df <= 2` then positive infinity
     * (`Double.POSITIVE_INFINITY`),
     *  * else undefined (`Double.NaN`).
     *
     */
    override val numericalVariance: Double
        get() {
            val df = degreesOfFreedom

            if (df > 2) {
                return df / (df - 2)
            }

            return if (df > 1 && df <= 2) {
                Double.POSITIVE_INFINITY
            } else Double.NaN

        }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always negative infinity no matter the
     * parameters.
     *
     * @return lower bound of the support (always
     * `Double.NEGATIVE_INFINITY`)
     */
    override val supportLowerBound: Double
        get() = Double.NEGATIVE_INFINITY

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is always positive infinity no matter the
     * parameters.
     *
     * @return upper bound of the support (always
     * `Double.POSITIVE_INFINITY`)
     */
    override val supportUpperBound: Double
        get() = Double.POSITIVE_INFINITY

    /** {@inheritDoc}  */
    override val isSupportLowerBoundInclusive: Boolean
        get() = false

    /** {@inheritDoc}  */
    override val isSupportUpperBoundInclusive: Boolean
        get() = false

    /**
     * {@inheritDoc}
     *
     * The support of this distribution is connected.
     *
     * @return `true`
     */
    override val isSupportConnected: Boolean
        get() = true

    init {
        if (degreesOfFreedom <= 0) {
            error("NotStrictlyPositive - DEGREES_OF_FREEDOM: $degreesOfFreedom")
        }
    }

    /**
     * {@inheritDoc}
     *
     * For this distribution `P(X = x)` always evaluates to 0.
     *
     * @return 0
     */
    override fun probability(x: Double): Double {
        return 0.0
    }

    /** {@inheritDoc}  */
    override fun density(x: Double): Double {
        val n = degreesOfFreedom
        val nPlus1Over2 = (n + 1) / 2
        return exp(
            Gamma.logGamma(nPlus1Over2) -
                    0.5 * (ln(PI) + ln(n)) -
                    Gamma.logGamma(n / 2) -
                    nPlus1Over2 * ln(1 + x * x / n)
        )
    }

    /** {@inheritDoc}  */
    override fun cumulativeProbability(x: Double): Double {
        val ret: Double
        if (x == 0.0) {
            ret = 0.5
        } else {
            val t = Beta.regularizedBeta(
                degreesOfFreedom / (degreesOfFreedom + x * x),
                0.5 * degreesOfFreedom,
                0.5
            )
            if (x < 0.0) {
                ret = 0.5 * t
            } else {
                ret = 1.0 - 0.5 * t
            }
        }

        return ret
    }

    companion object {
        /**
         * Default inverse cumulative probability accuracy.
         * @since 2.1
         */
        const val DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9
        /** Serializable version identifier  */
        private val serialVersionUID = -5852615386664158222L
    }
}
/**
 * Create a t distribution using the given degrees of freedom.
 *
 * @param degreesOfFreedom Degrees of freedom.
 * @throws NotStrictlyPositiveException if `degreesOfFreedom <= 0`
 */
