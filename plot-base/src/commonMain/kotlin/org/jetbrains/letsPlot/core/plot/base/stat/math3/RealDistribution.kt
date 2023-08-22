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

/**
 * Base interface for distributions on the reals.
 *
 * @version $Id: RealDistribution.java 1244107 2012-02-14 16:17:55Z erans $
 * @since 3.0
 */
interface RealDistribution {

    /**
     * Use this method to get the numerical value of the mean of this
     * distribution.
     *
     * @return the mean or `Double.NaN` if it is not defined
     */
    val numericalMean: Double

    /**
     * Use this method to get the numerical value of the variance of this
     * distribution.
     *
     * @return the variance (possibly `Double.POSITIVE_INFINITY` as
     * for certain cases in [TDistribution]) or `Double.NaN` if it
     * is not defined
     */
    val numericalVariance: Double

    /**
     * Access the lower bound of the support. This method must return the same
     * value as `inverseCumulativeProbability(0)`. In other words, this
     * method must return
     *
     * `inf {x in R | P(X <= x) > 0}`.
     *
     * @return lower bound of the support (might be
     * `Double.NEGATIVE_INFINITY`)
     */
    val supportLowerBound: Double

    /**
     * Access the upper bound of the support. This method must return the same
     * value as `inverseCumulativeProbability(1)`. In other words, this
     * method must return
     *
     * `inf {x in R | P(X <= x) = 1}`.
     *
     * @return upper bound of the support (might be
     * `Double.POSITIVE_INFINITY`)
     */
    val supportUpperBound: Double

    /**
     * Use this method to get information about whether the lower bound
     * of the support is inclusive or not.
     *
     * @return whether the lower bound of the support is inclusive or not
     */
    val isSupportLowerBoundInclusive: Boolean

    /**
     * Use this method to get information about whether the upper bound
     * of the support is inclusive or not.
     *
     * @return whether the upper bound of the support is inclusive or not
     */
    val isSupportUpperBoundInclusive: Boolean

    /**
     * Use this method to get information about whether the support is connected,
     * i.e. whether all values between the lower and upper bound of the support
     * are included in the support.
     *
     * @return whether the support is connected or not
     */
    val isSupportConnected: Boolean

    /**
     * For a random variable `X` whose values are distributed according
     * to this distribution, this method returns `P(X = x)`. In other
     * words, this method represents the probability mass function (PMF)
     * for the distribution.
     *
     * @param x the point at which the PMF is evaluated
     * @return the value of the probability mass function at point `x`
     */
    fun probability(x: Double): Double

    /**
     * Returns the probability density function (PDF) of this distribution
     * evaluated at the specified point `x`. In general, the PDF is
     * the derivative of the [CDF][.cumulativeProbability].
     * If the derivative does not exist at `x`, then an appropriate
     * replacement should be returned, e.g. `Double.POSITIVE_INFINITY`,
     * `Double.NaN`, or  the limit inferior or limit superior of the
     * difference quotient.
     *
     * @param x the point at which the PDF is evaluated
     * @return the value of the probability density function at point `x`
     */
    fun density(x: Double): Double

    /**
     * For a random variable `X` whose values are distributed according
     * to this distribution, this method returns `P(X <= x)`. In other
     * words, this method represents the (cumulative) distribution function
     * (CDF) for this distribution.
     *
     * @param x the point at which the CDF is evaluated
     * @return the probability that a random variable with this
     * distribution takes a value less than or equal to `x`
     */
    fun cumulativeProbability(x: Double): Double

    /**
     * For a random variable `X` whose values are distributed according
     * to this distribution, this method returns `P(x0 < X <= x1)`.
     *
     * @param x0 the exclusive lower bound
     * @param x1 the inclusive upper bound
     * @return the probability that a random variable with this distribution
     * takes a value between `x0` and `x1`,
     * excluding the lower and including the upper endpoint
     * @throws NumberIsTooLargeException if `x0 > x1`
     */
    fun cumulativeProbability(x0: Double, x1: Double): Double

    /**
     * Computes the quantile function of this distribution. For a random
     * variable `X` distributed according to this distribution, the
     * returned value is
     *
     *  * `inf{x in R | P(X<=x) >= p}` for `0 < p <= 1`,
     *  * `inf{x in R | P(X<=x) > 0}` for `p = 0`.
     *
     *
     * @param p the cumulative probability
     * @return the smallest `p`-quantile of this distribution
     * (largest 0-quantile for `p = 0`)
     * @throws OutOfRangeException if `p < 0` or `p > 1`
     */
    fun inverseCumulativeProbability(p: Double): Double

}
