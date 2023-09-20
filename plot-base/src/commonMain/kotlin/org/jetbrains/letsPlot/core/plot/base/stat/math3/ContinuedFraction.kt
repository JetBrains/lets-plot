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
import kotlin.math.abs
import kotlin.math.max


/**
 * Provides a generic means to evaluate continued fractions.  Subclasses simply
 * provided the a and b coefficients to evaluate the continued fraction.
 *
 *
 *
 * References:
 *
 *  * [
 * Continued Fraction](mathworld.wolfram.com/ContinuedFraction.html)
 *
 *
 *
 * @version $Id: ContinuedFraction.java 1244107 2012-02-14 16:17:55Z erans $
 */
abstract class ContinuedFraction
/**
 * Default constructor.
 */
protected constructor() {

    /**
     * Access the n-th a coefficient of the continued fraction.  Since a can be
     * a function of the evaluation point, x, that is passed in as well.
     * @param n the coefficient index to retrieve.
     * @param x the evaluation point.
     * @return the n-th a coefficient.
     */
    protected abstract fun getA(n: Int, x: Double): Double

    /**
     * Access the n-th b coefficient of the continued fraction.  Since b can be
     * a function of the evaluation point, x, that is passed in as well.
     * @param n the coefficient index to retrieve.
     * @param x the evaluation point.
     * @return the n-th b coefficient.
     */
    protected abstract fun getB(n: Int, x: Double): Double

    /**
     * Evaluates the continued fraction at the value x.
     * @param x the evaluation point.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x.
     * @throws ConvergenceException if the algorithm fails to converge.
     */
    fun evaluate(x: Double, maxIterations: Int): Double {
        return evaluate(x,
            DEFAULT_EPSILON, maxIterations)
    }

    /**
     *
     *
     * Evaluates the continued fraction at the value x.
     *
     *
     *
     *
     * The implementation of this method is based on equations 14-17 of:
     *
     *  *
     * Eric W. Weisstein. "Continued Fraction." From MathWorld--A Wolfram Web
     * Resource. [
 * mathworld.wolfram.com/ContinuedFraction.html](mathworld.wolfram.com/ContinuedFraction.html)
     *
     *
     * The recurrence relationship defined in those equations can result in
     * very large intermediate results which can result in numerical overflow.
     * As a means to combat these overflow conditions, the intermediate results
     * are scaled whenever they threaten to become numerically unstable.
     *
     * @param x the evaluation point.
     * @param epsilon maximum error allowed.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x.
     * @throws ConvergenceException if the algorithm fails to converge.
     */
    @JvmOverloads
    fun evaluate(x: Double, epsilon: Double = DEFAULT_EPSILON, maxIterations: Int = Int.MAX_VALUE): Double {
        var p0 = 1.0
        var p1 = getA(0, x)
        var q0 = 0.0
        var q1 = 1.0
        var c = p1 / q1
        var n = 0
        var relativeError = Double.MAX_VALUE
        while (n < maxIterations && relativeError > epsilon) {
            ++n
            val a = getA(n, x)
            val b = getB(n, x)
            var p2 = a * p1 + b * p0
            var q2 = a * q1 + b * q0
            var infinite = false
            if (p2.isInfinite() || q2.isInfinite()) {
                /*
                 * Need to scale. Try successive powers of the larger of a or b
                 * up to 5th power. Throw ConvergenceException if one or both
                 * of p2, q2 still overflow.
                 */
                var scaleFactor = 1.0
                @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
                var lastScaleFactor = 1.0
                val maxPower = 5
                val scale = max(a, b)
                if (scale <= 0) {  // Can't scale
                    error("ConvergenceException")
                    //LocalizedFormats.CONTINUED_FRACTION_INFINITY_DIVERGENCE,
                    //x
                }
                infinite = true
                for (i in 0 until maxPower) {
                    lastScaleFactor = scaleFactor
                    scaleFactor *= scale
                    if (a != 0.0 && a > b) {
                        p2 = p1 / lastScaleFactor + b / scaleFactor * p0
                        q2 = q1 / lastScaleFactor + b / scaleFactor * q0
                    } else if (b != 0.0) {
                        p2 = a / scaleFactor * p1 + p0 / lastScaleFactor
                        q2 = a / scaleFactor * q1 + q0 / lastScaleFactor
                    }
                    infinite = p2.isInfinite() || q2.isInfinite()
                    if (!infinite) {
                        break
                    }
                }
            }

            if (infinite) {
                // Scaling failed
                error("ConvergenceException")
                //LocalizedFormats.CONTINUED_FRACTION_INFINITY_DIVERGENCE,
                //x
            }

            val r = p2 / q2

            if (r.isNaN()) {
                error("ConvergenceException")
                //LocalizedFormats.CONTINUED_FRACTION_NAN_DIVERGENCE,
                //x
            }
            relativeError = abs(r / c - 1.0)

            // prepare for next iteration
            c = p2 / q2
            p0 = p1
            p1 = p2
            q0 = q1
            q1 = q2
        }

        if (n >= maxIterations) {
            error("MaxCountExceeded")
            //LocalizedFormats.NON_CONVERGENT_CONTINUED_FRACTION,
            //maxIterations, x
        }

        return c
    }

    companion object {
        /** Maximum allowed numerical error.  */
        private val DEFAULT_EPSILON = 10e-9
    }
}
/**
 * Evaluates the continued fraction at the value x.
 * @param x the evaluation point.
 * @return the value of the continued fraction evaluated at x.
 * @throws ConvergenceException if the algorithm fails to converge.
 */
/**
 * Evaluates the continued fraction at the value x.
 * @param x the evaluation point.
 * @param epsilon maximum error allowed.
 * @return the value of the continued fraction evaluated at x.
 * @throws ConvergenceException if the algorithm fails to converge.
 */
