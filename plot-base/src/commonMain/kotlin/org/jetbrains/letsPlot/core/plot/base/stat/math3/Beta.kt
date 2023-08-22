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
import kotlin.math.exp
import kotlin.math.ln


/**
 * This is a utility class that provides computation methods related to the
 * Beta family of functions.
 *
 * @version $Id: Beta.java 1244107 2012-02-14 16:17:55Z erans $
 */
object Beta {
    /** Maximum allowed numerical error.  */
    private val DEFAULT_EPSILON = 10e-15

    /**
     * Returns the regularized beta function I(x, a, b).
     *
     * @param x the value.
     * @param a Parameter `a`.
     * @param b Parameter `b`.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return the regularized beta function I(x, a, b)
     * @throws org.apache.commons.math3.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     */
    fun regularizedBeta(
        x: Double,
        a: Double, b: Double,
        maxIterations: Int
    ): Double {
        return regularizedBeta(
            x,
            a,
            b,
            DEFAULT_EPSILON,
            maxIterations
        )
    }

    /**
     * Returns the regularized beta function I(x, a, b).
     *
     * The implementation of this method is based on:
     *
     *  *
     * [
 * Regularized Beta Function](mathworld.wolfram.com/RegularizedBetaFunction.html).
     *  *
     * [
 * Regularized Beta Function](functions.wolfram.com/06.21.10.0001.01).
     *
     *
     * @param x the value.
     * @param a Parameter `a`.
     * @param b Parameter `b`.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return the regularized beta function I(x, a, b)
     * @throws org.apache.commons.math3.exception.MaxCountExceededException
     * if the algorithm fails to converge.
     */
    @JvmOverloads
    fun regularizedBeta(
        x: Double,
        a: Double, b: Double,
        epsilon: Double = DEFAULT_EPSILON, maxIterations: Int = Int.MAX_VALUE
    ): Double {
        val ret: Double

        if (x.isNaN() ||
            a.isNaN() ||
            b.isNaN() ||
            x < 0 ||
            x > 1 ||
            a <= 0.0 ||
            b <= 0.0
        ) {
            ret = Double.NaN
        } else if (x > (a + 1.0) / (a + b + 2.0)) {
            ret = 1.0 - regularizedBeta(
                1.0 - x,
                b,
                a,
                epsilon,
                maxIterations
            )
        } else {
            val fraction = object : ContinuedFraction() {

                override fun getB(n: Int, x: Double): Double {
                    @Suppress("NAME_SHADOWING")
                    val ret: Double
                    val m: Double
                    if (n % 2 == 0) { // even
                        m = n / 2.0
                        ret = m * (b - m) * x / ((a + 2 * m - 1) * (a + 2 * m))
                    } else {
                        m = (n - 1.0) / 2.0
                        ret = -((a + m) * (a + b + m) * x) / ((a + 2 * m) * (a + 2 * m + 1.0))
                    }
                    return ret
                }

                override fun getA(n: Int, x: Double): Double {
                    return 1.0
                }
            }
            ret = exp(
                a * ln(x) + b * ln(1.0 - x) -
                        ln(a) - logBeta(a, b, epsilon, maxIterations)
            ) * 1.0 / fraction.evaluate(x, epsilon, maxIterations)
        }

        return ret
    }

    /**
     * Returns the natural logarithm of the beta function B(a, b).
     *
     * The implementation of this method is based on:
     *
     *  * [
 * Beta Function](mathworld.wolfram.com/BetaFunction.html), equation (1).
     *
     *
     * @param a Parameter `a`.
     * @param b Parameter `b`.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return log(B(a, b)).
     */
    @JvmOverloads
    fun logBeta(
        a: Double, b: Double,
        @Suppress("UNUSED_PARAMETER") epsilon: Double = DEFAULT_EPSILON,
        @Suppress("UNUSED_PARAMETER") maxIterations: Int = Int.MAX_VALUE
    ): Double {
        val ret: Double

        if (a.isNaN() ||
            b.isNaN() ||
            a <= 0.0 ||
            b <= 0.0
        ) {
            ret = Double.NaN
        } else {
            ret = Gamma.logGamma(a) + Gamma.logGamma(
                b
            ) - Gamma.logGamma(a + b)
        }

        return ret
    }
}
/**
 * Default constructor.  Prohibit instantiation.
 */
/**
 * Returns the
 * [
 * regularized beta function](mathworld.wolfram.com/RegularizedBetaFunction.html) I(x, a, b).
 *
 * @param x Value.
 * @param a Parameter `a`.
 * @param b Parameter `b`.
 * @return the regularized beta function I(x, a, b).
 * @throws org.apache.commons.math3.exception.MaxCountExceededException
 * if the algorithm fails to converge.
 */
/**
 * Returns the
 * [
 * regularized beta function](mathworld.wolfram.com/RegularizedBetaFunction.html) I(x, a, b).
 *
 * @param x Value.
 * @param a Parameter `a`.
 * @param b Parameter `b`.
 * @param epsilon When the absolute value of the nth item in the
 * series is less than epsilon the approximation ceases to calculate
 * further elements in the series.
 * @return the regularized beta function I(x, a, b)
 * @throws org.apache.commons.math3.exception.MaxCountExceededException
 * if the algorithm fails to converge.
 */
/**
 * Returns the natural logarithm of the beta function B(a, b).
 *
 * @param a Parameter `a`.
 * @param b Parameter `b`.
 * @return log(B(a, b)).
 */
