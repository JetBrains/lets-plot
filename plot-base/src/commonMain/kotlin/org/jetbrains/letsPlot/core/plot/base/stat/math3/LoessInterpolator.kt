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

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Implements the <a href="http://en.wikipedia.org/wiki/Local_regression">
 * Local Regression Algorithm</a> (also Loess, Lowess) for interpolation of
 * real univariate functions.
 * <p/>
 * For reference, see
 * <a href="http://www.math.tau.ac.il/~yekutiel/MA seminar/Cleveland 1979.pdf">
 * William S. Cleveland - Robust Locally Weighted Regression and Smoothing
 * Scatterplots</a>
 * <p/>
 * This class implements both the loess method and serves as an interpolation
 * adapter to it, allowing one to build a spline on the obtained loess fit.
 *
 * @version $Id: LoessInterpolator.java 1244107 2012-02-14 16:17:55Z erans $
 * @since 2.0
 */

/**
 * Implements the <a href="http://en.wikipedia.org/wiki/Local_regression">
 * Local Regression Algorithm</a> (also Loess, Lowess) for interpolation of
 * real univariate functions.
 * <p/>
 * For reference, see
 * <a href="http://www.math.tau.ac.il/~yekutiel/MA seminar/Cleveland 1979.pdf">
 * William S. Cleveland - Robust Locally Weighted Regression and Smoothing
 * Scatterplots</a>
 * <p/>
 * This class implements both the loess method and serves as an interpolation
 * adapter to it, allowing one to build a spline on the obtained loess fit.
 *
 * @version $Id: LoessInterpolator.java 1244107 2012-02-14 16:17:55Z erans $
 * @since 2.0
 */

class LoessInterpolator(
    private val bandwidth: Double = DEFAULT_BANDWIDTH,
    private val robustnessIters: Int = DEFAULT_ROBUSTNESS_ITERS,
    private val accuracy: Double = DEFAULT_ACCURACY
) {

    /**
     * Construct a new {@link LoessInterpolator}
     * with given bandwidth, number of robustness iterations and accuracy.
     *
     * @param bandwidth The bandwidth parameter: when computing the loess fit at
     * a particular point, this fraction of source points closest
     * to the current point is taken into account for computing
     * a least-squares regression.
     * A sensible value is usually 0.25 to 0.5.
     * A sensible value is usually 0.25 to 0.5, the default value is
     * {@link #DEFAULT_BANDWIDTH}.
     * @param robustnessIters The number of robustness iterations parameter:
     * this many robustness iterations are done.
     * A sensible value is usually 0 (just the initial fit without any
     * robustness iterations) to 4, the default value is
     * {@link #DEFAULT_ROBUSTNESS_ITERS}.
     * @param accuracy If the median residual at a certain robustness iteration
     * is less than this amount, no more iterations are done.
     *
     * @throws OutOfRangeException if bandwidth does not lie in the interval [0,1].
     * @throws NotPositiveException if {@code robustnessIters} is negative.
     * @see #LoessInterpolator(double, int)
     * @since 2.1
     */

    init {
        if (bandwidth <= 0 || bandwidth > 1)
            error("Out of range of bandwidth value: $bandwidth should be > 0 and <= 1")

        if (robustnessIters < 0)
            error("Not positive Robutness iterationa: $robustnessIters")
    }

    /**
     * Compute an interpolating function by performing a loess fit
     * on the data at the original abscissae and then building a cubic spline
     * with a
     * [org.apache.commons.math3.analysis.interpolation.SplineInterpolator]
     * on the resulting fit.
     *
     * @param xval the arguments for the interpolation points
     * @param yval the values for the interpolation points
     * @return A cubic spline built upon a loess fit to the data at the original abscissae
     * @throws org.apache.commons.math3.exception.NonMonotonicSequenceException
     * if `xval` not sorted in strictly increasing order.
     * @throws DimensionMismatchException if `xval` and `yval` have
     * different sizes.
     * @throws NoDataException if `xval` or `yval` has zero size.
     * @throws org.apache.commons.math3.exception.NotFiniteNumberException if
     * any of the arguments and values are not finite real numbers.
     * @throws NumberIsTooSmallException if the bandwidth is too small to
     * accomodate the size of the input data (i.e. the bandwidth must be
     * larger than 2/n).
     */
    fun interpolate(xval: DoubleArray, yval: DoubleArray): PolynomialSplineFunction {
        return SplineInterpolator().interpolate(xval, smooth(xval, yval))
    }

    /**
     * Compute a weighted loess fit on the data at the original abscissae.
     *
     * @param xval Arguments for the interpolation points.
     * @param yval Values for the interpolation points.
     * @param weights point weights: coefficients by which the robustness weight
     * of a point is multiplied.
     * @return the values of the loess fit at corresponding original abscissae.
     * @throws org.apache.commons.math3.exception.NonMonotonicSequenceException
     * if `xval` not sorted in strictly increasing order.
     * @throws DimensionMismatchException if `xval` and `yval` have
     * different sizes.
     * @throws NoDataException if `xval` or `yval` has zero size.
     * @throws org.apache.commons.math3.exception.NotFiniteNumberException if
     * any of the arguments and values are not finite real numbers.
     * @throws NumberIsTooSmallException if the bandwidth is too small to
     * accomodate the size of the input data (i.e. the bandwidth must be
     * larger than 2/n).
     * @since 2.1
     */
    private fun smooth(xval: DoubleArray, yval: DoubleArray, weights: DoubleArray): DoubleArray {
        if (xval.size != yval.size) {
            error("Dimension mismatch of interpolation points: ${xval.size} != ${yval.size}")
        }
        val n = xval.size
        if (n == 0) {
            error("No data to interpolate")
        }
        checkAllFiniteReal(xval)
        checkAllFiniteReal(yval)
        checkAllFiniteReal(weights)
        MathArrays.checkOrder(xval)
        if (n == 1) {
            return doubleArrayOf(yval[0])
        }
        if (n == 2) {
            return doubleArrayOf(yval[0], yval[1])
        }
        val bandwidthInPoints = (bandwidth * n).toInt()
        if (bandwidthInPoints < 2) {
            error("LOESS 'bandwidthInPoints' is too small: $bandwidthInPoints < 2")
        }
        val res = DoubleArray(n)
        val residuals = DoubleArray(n)
        val sortedResiduals = DoubleArray(n)
        val robustnessWeights = DoubleArray(n)
        // Do an initial fit and 'robustnessIters' robustness iterations.
        // This is equivalent to doing 'robustnessIters+1' robustness iterations
        // starting with all robustness weights set to 1.
        robustnessWeights.fill(1.0)
        for (iter in 0..robustnessIters) {
            val bandwidthInterval = intArrayOf(0, bandwidthInPoints - 1)
            // At each x, compute a local weighted linear regression
            for (i in 0 until n) {
                val x = xval[i]
                // Find out the interval of source points on which
                // a regression is to be made.
                if (i > 0) {
                    updateBandwidthInterval(
                        xval,
                        weights,
                        i,
                        bandwidthInterval
                    )
                }
                val ileft = bandwidthInterval[0]
                val iright = bandwidthInterval[1]
                // Compute the point of the bandwidth interval that is
                // farthest from x
                val edge: Int
                edge = if (xval[i] - xval[ileft] > xval[iright] - xval[i]) {
                    ileft
                } else {
                    iright
                }
                // Compute a least-squares linear fit weighted by
                // the product of robustness weights and the tricube
                // weight function.
                // See http://en.wikipedia.org/wiki/Linear_regression
                // (section "Univariate linear case")
                // and http://en.wikipedia.org/wiki/Weighted_least_squares
                // (section "Weighted least squares")
                var sumWeights = 0.0
                var sumX = 0.0
                var sumXSquared = 0.0
                var sumY = 0.0
                var sumXY = 0.0
                val denom: Double = abs(1.0 / (xval[edge] - x))
                for (k in ileft..iright) {
                    val xk = xval[k]
                    val yk = yval[k]
                    val dist = if (k < i) x - xk else xk - x
                    val w: Double = tricube(dist * denom) * robustnessWeights[k] * weights[k]
                    val xkw = xk * w
                    sumWeights += w
                    sumX += xkw
                    sumXSquared += xk * xkw
                    sumY += yk * w
                    sumXY += yk * xkw
                }
                val meanX = sumX / sumWeights
                val meanY = sumY / sumWeights
                val meanXY = sumXY / sumWeights
                val meanXSquared = sumXSquared / sumWeights
                val beta: Double
                beta = if (sqrt(abs(meanXSquared - meanX * meanX)) < accuracy) {
                    0.0
                } else {
                    (meanXY - meanX * meanY) / (meanXSquared - meanX * meanX)
                }
                val alpha = meanY - beta * meanX
                res[i] = beta * x + alpha
                residuals[i] = abs(yval[i] - res[i])
            }
            // No need to recompute the robustness weights at the last
            // iteration, they won't be needed anymore
            if (iter == robustnessIters) {
                break
            }
            // Recompute the robustness weights.
            // Find the median residual.
            // An arraycopy and a sort are completely tractable here,
            // because the preceding loop is a lot more expensive
            residuals.copyInto(sortedResiduals, 0, 0, n)
            sortedResiduals.sort()

            val medianResidual = sortedResiduals[n / 2]
            if (abs(medianResidual) < accuracy) {
                break
            }
            for (i in 0 until n) {
                val arg = residuals[i] / (6 * medianResidual)
                if (arg >= 1) {
                    robustnessWeights[i] = 0.0
                } else {
                    val w = 1 - arg * arg
                    robustnessWeights[i] = w * w
                }
            }
        }
        return res
    }


    /**
     * Given an index interval into xval that embraces a certain number of
     * points closest to `xval[i-1]`, update the interval so that it
     * embraces the same number of points closest to `xval[i]`,
     * ignoring zero weights.
     *
     * @param xval Arguments array.
     * @param weights Weights array.
     * @param i Index around which the new interval should be computed.
     * @param bandwidthInterval a two-element array {left, right} such that:
     * `(left==0 or xval[i] - xval[left-1] > xval[right] - xval[i])`
     * and
     * `(right==xval.length-1 or xval[right+1] - xval[i] > xval[i] - xval[left])`.
     * The array will be updated.
     */
    private fun updateBandwidthInterval(
        xval: DoubleArray, weights: DoubleArray,
        i: Int,
        bandwidthInterval: IntArray
    ) {
        val left = bandwidthInterval[0]
        val right = bandwidthInterval[1]
        // The right edge should be adjusted if the next point to the right
        // is closer to xval[i] than the leftmost point of the current interval
        val nextRight: Int = nextNonzero(weights, right)
        if (nextRight < xval.size && xval[nextRight] - xval[i] < xval[i] - xval[left]) {
            val nextLeft: Int = nextNonzero(
                weights, bandwidthInterval[0]
            )
            bandwidthInterval[0] = nextLeft
            bandwidthInterval[1] = nextRight
        }
    }


    /**
     * Compute the
     * [tricube](http://en.wikipedia.org/wiki/Local_regression#Weight_function)
     * weight function
     *
     * @param x Argument.
     * @return `(1 - |x|<sup>3</sup>)<sup>3</sup>` for |x| &lt; 1, 0 otherwise.
     */
    private fun tricube(x: Double): Double {
        val absX: Double = abs(x)
        if (absX >= 1.0) {
            return 0.0
        }
        val tmp = 1 - absX * absX * absX
        return tmp * tmp * tmp
    }

    /**
     * Return the smallest index `j` such that
     * `j > i && (j == weights.length || weights[j] != 0)`.
     *
     * @param weights Weights array.
     * @param i Index from which to start search.
     * @return the smallest compliant index.
     */
    private fun nextNonzero(weights: DoubleArray, i: Int): Int {
        var j = i + 1
        while (j < weights.size && weights[j] == 0.0) {
            ++j
        }
        return j
    }

    /**
     * Compute a loess fit on the data at the original abscissae.
     *
     * @param xval the arguments for the interpolation points
     * @param yval the values for the interpolation points
     * @return values of the loess fit at corresponding original abscissae
     * @throws org.apache.commons.math3.exception.NonMonotonicSequenceException
     * if `xval` not sorted in strictly increasing order.
     * @throws DimensionMismatchException if `xval` and `yval` have
     * different sizes.
     * @throws NoDataException if `xval` or `yval` has zero size.
     * @throws org.apache.commons.math3.exception.NotFiniteNumberException if
     * any of the arguments and values are not finite real numbers.
     * @throws NumberIsTooSmallException if the bandwidth is too small to
     * accomodate the size of the input data (i.e. the bandwidth must be
     * larger than 2/n).
     */
    private fun smooth(xval: DoubleArray, yval: DoubleArray): DoubleArray {
        if (xval.size != yval.size) {
            error("Dimension mismatch: ${xval.size} != ${yval.size}")
        }
        val unitWeights = DoubleArray(xval.size)
        unitWeights.fill(1.0)
        return smooth(xval, yval, unitWeights)
    }

    /**
     * Check that the argument is a real number.
     *
     * @param x Argument.
     * @throws NotFiniteNumberException if `x` is not a
     * finite real number.
     */
    private fun checkFinite(x: Double) {
        if (x.isInfinite() || x.isNaN()) {
            error("Argument $x is not a finite number")
        }
    }

    /**
     * Check that all elements of an array are finite real numbers.
     *
     * @param values Values array.
     * @throws org.apache.commons.math3.exception.NotFiniteNumberException
     * if one of the values is not a finite real number.
     */
    private fun checkAllFiniteReal(values: DoubleArray) {
        for (i in values.indices) {
            checkFinite(values[i])
        }
    }

    companion object {
        /** Default value of the bandwidth parameter.  */
        const val DEFAULT_BANDWIDTH = 0.3

        /** Default value of the number of robustness iterations.  */
        const val DEFAULT_ROBUSTNESS_ITERS = 2

        /**
         * Default value for accuracy.
         * @since 2.1
         */
        const val DEFAULT_ACCURACY = 1e-12
    }
}