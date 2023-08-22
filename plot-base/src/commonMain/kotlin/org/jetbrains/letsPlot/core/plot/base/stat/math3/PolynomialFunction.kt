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

import kotlin.math.max
import kotlin.math.min

/**
 * Immutable representation of a real polynomial function with real coefficients.
 * <p>
 * <a href="http://mathworld.wolfram.com/HornersMethod.html">Horner's Method</a>
 * is used to evaluate the function.</p>
 *
 * @version $Id: PolynomialFunction.java 1244107 2012-02-14 16:17:55Z erans $
 */

class PolynomialFunction(c: DoubleArray?) {

    /**
     * The coefficients of the polynomial, ordered by degree -- i.e.,
     * coefficients[0] is the constant term and coefficients[n] is the
     * coefficient of x^n where n is the degree of the polynomial.
     */
    private var coefficients: DoubleArray

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

    operator fun unaryPlus() = PolynomialFunction(coefficients)

    operator fun unaryMinus(): PolynomialFunction {
        val dd = DoubleArray(coefficients.size)

        for ((i, c) in coefficients.withIndex()) {
            dd[i] = -c
        }

        return PolynomialFunction(dd)
    }

    private fun apply_op(other: PolynomialFunction, op: (Double, Double) -> Double): PolynomialFunction {
        val sz = max(coefficients.size, other.coefficients.size)
        val nc = DoubleArray(sz)

        for (i in (0 until sz)) {
            val a = if (i < coefficients.size) coefficients[i] else 0.0
            val b = if (i < other.coefficients.size) other.coefficients[i] else 0.0
            nc[i] = op(a, b)
        }

        return PolynomialFunction(nc)
    }

    operator fun plus(other: PolynomialFunction): PolynomialFunction {
        return apply_op(other) { x, y -> x + y }
    }

    operator fun minus(other: PolynomialFunction): PolynomialFunction {
        return apply_op(other) { x, y -> x - y }
    }

    fun multiply(a: Double): PolynomialFunction {
        val dd = DoubleArray(coefficients.size)

        for ((i, c) in coefficients.withIndex()) {
            dd[i] = a * c
        }

        return PolynomialFunction(dd)
    }

    operator fun times(other: PolynomialFunction): PolynomialFunction {
        val nd = coefficients.size + other.coefficients.size - 1
        val nc = DoubleArray(nd)

        for (i in (0 until nd)) {
            for (j in (0..i)) {
                val a = if (j < coefficients.size) coefficients[j] else 0.0
                val k = i - j
                val b = if (k < other.coefficients.size) other.coefficients[k] else 0.0
                nc[i] += a * b
            }
        }

        return PolynomialFunction(nc)
    }

    fun degree(): Int = max(0, coefficients.indexOfLast { it != 0.0 })

    operator fun compareTo(other: PolynomialFunction): Int {
        val d1 = degree()
        val d2 = other.degree()
        val n = min(d1, d2) + 1

        for (i in 0 until n) {
            val a = coefficients[i]
            val b = other.coefficients[i]

            val res = a.compareTo(b)

            if (res != 0)
                return res
        }

        return d1.compareTo(d2)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null || other !is PolynomialFunction)
            return false

        return compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return coefficients.hashCode()
    }

    override fun toString(): String {
        val sb = StringBuilder()

        for (i in coefficients.lastIndex downTo 0) {

            if (coefficients[i] != 0.0) {

                if (!sb.isEmpty())
                    sb.append(" + ")

                sb.append(coefficients[i].toString())

                if (i > 0)
                    sb.append("x")

                if (i > 1)
                    sb.append("^").append(i)
            }
        }

        return sb.toString()
    }
}

operator fun Double.times(p: PolynomialFunction): PolynomialFunction {
    return p.multiply(this)
}

