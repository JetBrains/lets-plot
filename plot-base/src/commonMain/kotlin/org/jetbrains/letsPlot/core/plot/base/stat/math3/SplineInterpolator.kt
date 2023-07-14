/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.math3


/**
 * Computes a natural (also known as "free", "unclamped") cubic spline interpolation for the data set.
 *
 * The [.interpolate] method returns a [PolynomialSplineFunction]
 * consisting of n cubic polynomials, defined over the subintervals determined by the x values,
 * x[0] < x[i] ... < x[n].  The x values are referred to as "knot points."
 *
 * The value of the PolynomialSplineFunction at a point x that is greater than or equal to the smallest
 * knot point and strictly less than the largest knot point is computed by finding the subinterval to which
 * x belongs and computing the value of the corresponding polynomial at `x - x[i] ` where
 * `i` is the index of the subinterval.  See [PolynomialSplineFunction] for more details.
 *
 * The interpolating polynomials satisfy:
 *  1. The value of the PolynomialSplineFunction at each of the input x values equals the
 * corresponding y value.
 *  1. Adjacent polynomials are equal through two derivatives at the knot points (i.e., adjacent polynomials
 * "match up" at the knot points, as do their first and second derivatives).
 *
 * The cubic spline interpolation algorithm implemented is as described in R.L. Burden, J.D. Faires,
 * <u>Numerical Analysis</u>, 4th Ed., 1989, PWS-Kent, ISBN 0-53491-585-X, pp 126-131.
 *
 *
 * @version $Id: SplineInterpolator.java 1244107 2012-02-14 16:17:55Z erans $
 */

class SplineInterpolator {
    /**
     * Computes an interpolating function for the data set.
     * @param x the arguments for the interpolation points
     * @param y the values for the interpolation points
     * @return a function which interpolates the data set
     * @throws DimensionMismatchException if `x` and `y`
     * @throws DimensionMismatchException if `x` and `y`
     * have different sizes.
     * @throws org.apache.commons.math3.exception.NonMonotonicSequenceException
     * if `x` is not sorted in strict increasing order.
     * @throws NumberIsTooSmallException if the size of `x` is smaller
     * than 3.
     */
    fun interpolate(x: DoubleArray, y: DoubleArray): PolynomialSplineFunction {
        if (x.size != y.size) {
            error("Dimension mismatch ${x.size} != ${y.size}")
        }
        if (x.size < 3) {
            error("Too small value of points number: ${x.size} < 3")
        }
        // Number of intervals.  The number of data points is n + 1.
        val n = x.size - 1
        MathArrays.checkOrder(x)
        // Differences between knot points
        val h = DoubleArray(n)
        for (i in 0 until n) {
            h[i] = x[i + 1] - x[i]
        }
        val mu = DoubleArray(n)
        val z = DoubleArray(n + 1)
        mu[0] = 0.0
        z[0] = 0.0

        for (i in 1 until n) {
            var g = 2.0 * (x[i + 1] - x[i - 1]) - h[i - 1] * mu[i - 1]
            mu[i] = h[i] / g
            z[i] =
                (3.0 * (y[i + 1] * h[i - 1] - y[i] * (x[i + 1] - x[i - 1]) + y[i - 1] * h[i]) /
                        (h[i - 1] * h[i]) - h[i - 1] * z[i - 1]) / g
        }
        // cubic spline coefficients --  b is linear, c quadratic, d is cubic (original y's are constants)
        val b = DoubleArray(n)
        val c = DoubleArray(n + 1)
        val d = DoubleArray(n)
        z[n] = 0.0
        c[n] = 0.0
        for (j in n - 1 downTo 0) {
            c[j] = z[j] - mu[j] * c[j + 1]
            b[j] = (y[j + 1] - y[j]) / h[j] - h[j] * (c[j + 1] + 2.0 * c[j]) / 3.0
            d[j] = (c[j + 1] - c[j]) / (3.0 * h[j])
        }
        val polynomials: Array<PolynomialFunction?> = arrayOfNulls<PolynomialFunction>(n)
        val coefficients = DoubleArray(4)
        for (i in 0 until n) {
            coefficients[0] = y[i]
            coefficients[1] = b[i]
            coefficients[2] = c[i]
            coefficients[3] = d[i]
            polynomials[i] = PolynomialFunction(coefficients)
        }
        return PolynomialSplineFunction(x, polynomials)
    }
}


