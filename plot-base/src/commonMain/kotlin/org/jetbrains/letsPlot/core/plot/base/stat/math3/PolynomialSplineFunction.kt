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
 * Represents a polynomial spline function.
 * <p>
 * A <strong>polynomial spline function</strong> consists of a set of
 * <i>interpolating polynomials</i> and an ascending array of domain
 * <i>knot points</i>, determining the intervals over which the spline function
 * is defined by the constituent polynomials.  The polynomials are assumed to
 * have been computed to match the values of another function at the knot
 * points.  The value consistency constraints are not currently enforced by
 * <code>PolynomialSplineFunction</code> itself, but are assumed to hold among
 * the polynomials and knot points passed to the constructor.</p>
 * <p>
 * N.B.:  The polynomials in the <code>polynomials</code> property must be
 * centered on the knot points to compute the spline function values.
 * See below.</p>
 * <p>
 * The domain of the polynomial spline function is
 * <code>[smallest knot, largest knot]</code>.  Attempts to evaluate the
 * function at values outside of this range generate IllegalArgumentExceptions.
 * </p>
 * <p>
 * The value of the polynomial spline function for an argument <code>x</code>
 * is computed as follows:
 * <ol>
 * <li>The knot array is searched to find the segment to which <code>x</code>
 * belongs.  If <code>x</code> is less than the smallest knot point or greater
 * than the largest one, an <code>IllegalArgumentException</code>
 * is thrown.</li>
 * <li> Let <code>j</code> be the index of the largest knot point that is less
 * than or equal to <code>x</code>.  The value returned is <br>
 * <code>polynomials[j](x - knot[j])</code></li></ol></p>
 *
 * @version $Id: PolynomialSplineFunction.java 1244107 2012-02-14 16:17:55Z erans $
 */

class PolynomialSplineFunction(_knots: DoubleArray?, _polynomials: Array<PolynomialFunction?>)
{
    /**
     * Spline segment interval delimiters (knots).
     * Size is n + 1 for n segments.
     */
    val knots: DoubleArray

    /**
     * The polynomial functions that make up the spline.  The first element
     * determines the value of the spline over the first subinterval, the
     * second over the second, etc.   Spline function values are determined by
     * evaluating these functions at `(x - knot[i])` where i is the
     * knot segment to which x belongs.
     */
    val polynomials: Array<PolynomialFunction?>

    /**
     * Number of spline segments. It is equal to the number of polynomials and
     * to the number of partition points - 1.
     */
    private val n : Int

    /**
     * Construct a polynomial spline function with the given segment delimiters
     * and interpolating polynomials.
     * The constructor copies both arrays and assigns the copies to the knots
     * and polynomials properties, respectively.
     *
     * @param _knots Spline segment interval delimiters.
     * @param _polynomials Polynomial functions that make up the spline.
     * @throws NullArgumentException if either of the input arrays is {@code null}.
     * @throws NumberIsTooSmallException if knots has length less than 2.
     * @throws DimensionMismatchException if {@code polynomials.length != knots.length - 1}.
     * @throws org.apache.commons.math3.exception.NonMonotonicSequenceException if
     * the {@code knots} array is not strictly increasing.
     *
     */
    init {

        if (_knots == null) {
            error("Null argument ")
        }
        if (_knots.size < 2) {
            error("Spline partition must have at least 2 points, got ${_knots.size}")
        }
        if (_knots.size - 1 != _polynomials.size) {
            error("Dimensions mismatch: ${_polynomials.size} polynomial functions !=  ${_knots.size} segment delimiters")
        }

        MathArrays.checkOrder(_knots)

        this.n = _knots.size - 1
        this.knots = _knots

        this.polynomials = arrayOfNulls<PolynomialFunction?>(n)
        _polynomials.copyInto (this.polynomials, 0, 0, n)

    }

    /**
     * Compute the value for the function.
     * See [PolynomialSplineFunction] for details on the algorithm for
     * computing the value of the function.
     *
     * @param v Point for which the function value should be computed.
     * @return the value.
     * @throws OutOfRangeException if `v` is outside of the domain of the
     * spline function (smaller than the smallest knot point or larger than the
     * largest knot point).
     */
    fun value(v: Double): Double? {
        if (v < knots[0] || v > knots[n]) {
            error("$v out of [${knots[0]}, ${knots[n]}] range")
        }

        var i: Int =  knots.toMutableList().binarySearch(v)
        if (i < 0) {
            i = -i - 2
        }
        // This will handle the case where v is the last knot value
        // There are only n-1 polynomials, so if v is the last knot
        // then we will use the last polynomial to calculate the value.
        if (i >= polynomials.size) {
            i--
        }
        return polynomials[i]?.value(v - knots[i])
    }

}

