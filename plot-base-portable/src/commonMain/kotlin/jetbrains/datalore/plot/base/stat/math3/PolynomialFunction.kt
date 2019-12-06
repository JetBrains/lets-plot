/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package jetbrains.datalore.plot.base.stat.math3

import jetbrains.datalore.base.gcommon.collect.Comparables.min
import jetbrains.datalore.base.gcommon.collect.Comparables.max
import kotlin.math.abs

/**
 * Immutable representation of a real polynomial function with real coefficients.
 * <p>
 * <a href="http://mathworld.wolfram.com/HornersMethod.html">Horner's Method</a>
 * is used to evaluate the function.</p>
 *
 * @version $Id: PolynomialFunction.java 1244107 2012-02-14 16:17:55Z erans $
 */

public class PolynomialFunction(c: DoubleArray?) {

    /**
     * The coefficients of the polynomial, ordered by degree -- i.e.,
     * coefficients[0] is the constant term and coefficients[n] is the
     * coefficient of x^n where n is the degree of the polynomial.
     */
    private var coefficients : DoubleArray

    /**
     * Construct a polynomial with the given coefficients.  The first element
     * of the coefficients array is the constant term.  Higher degree
     * coefficients follow in sequence.  The degree of the resulting polynomial
     * is the index of the last non-null element of the array, or 0 if all elements
     * are null.
     *
     * The constructor makes a copy of the input array and assigns the copy to
     * the coefficients property.
     *
     * @param c Polynomial coefficients.
     * @throws NullArgumentException if `c` is `null`.
     * @throws NoDataException if `c` is empty.
     */
    init {
        if (c == null || c.isEmpty()) {
            error("Empty polynomials coefficients array")
        }

        var n = c.size
        while (n > 1 && c[n - 1] == 0.0) {
            --n
        }
        coefficients = DoubleArray(n)
        c.copyInto(coefficients, 0, 0, n)
    }

    /**
     * Compute the value of the function for the given argument.
     *
     * The value returned is <br></br>
     * `coefficients[n] * x^n + ... + coefficients[1] * x  + coefficients[0]`
     *
     * @param x Argument for which the function value should be computed.
     * @return the value of the polynomial at the given point.
     * @see UnivariateFunction.value
     */
    fun value(x: Double): Double {
        return evaluate(coefficients, x)
    }

    /**
     * Uses Horner's Method to evaluate the polynomial with the given coefficients at
     * the argument.
     *
     * @param coefficients Coefficients of the polynomial to evaluate.
     * @param argument Input value.
     * @return the value of the polynomial.
     * @throws NoDataException if `coefficients` is empty.
     * @throws NullArgumentException if `coefficients` is `null`.
     */
    private fun evaluate(coefficients: DoubleArray?, argument: Double): Double {

        if (coefficients == null)
            error("Null argument: coefficients of the polynomial to evaluate")

        val n = coefficients.size
        if (n == 0) {
            error("Empty polynomials coefficients array")
        }
        var result = coefficients[n - 1]
        for (j in n - 2 downTo 0) {
            result = argument * result + coefficients[j]
        }
        return result
    }

}