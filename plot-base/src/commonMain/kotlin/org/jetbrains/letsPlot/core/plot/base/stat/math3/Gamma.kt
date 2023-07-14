/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package org.jetbrains.letsPlot.core.plot.base.stat.math3

import kotlin.jvm.JvmOverloads
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

/**
 * This is a utility class that provides computation methods related to the
 * Gamma family of functions.
 *
 * @version $Id: Gamma.java 1244107 2012-02-14 16:17:55Z erans $
 */
object Gamma {
    /**
     * [Euler-Mascheroni constant](https://en.wikipedia.org/wiki/Euler-Mascheroni_constant)
     * @since 2.0
     */
    val GAMMA = 0.577215664901532860606512090082
    /** Maximum allowed numerical error.  */
    private val DEFAULT_EPSILON = 10e-15
    /** Lanczos coefficients  */
    private val LANCZOS = doubleArrayOf(
        0.99999999999999709182,
        57.156235665862923517,
        -59.597960355475491248,
        14.136097974741747174,
        -0.49191381609762019978,
        .33994649984811888699e-4,
        .46523628927048575665e-4,
        -.98374475304879564677e-4,
        .15808870322491248884e-3,
        -.21026444172410488319e-3,
        .21743961811521264320e-3,
        -.16431810653676389022e-3,
        .84418223983852743293e-4,
        -.26190838401581408670e-4,
        .36899182659531622704e-5
    )
    /** Avoid repeated computation of log of 2 PI in logGamma  */
    private val HALF_LOG_2_PI = 0.5 * ln(2.0 * PI)
    // limits for switching algorithm in digamma
    /** C limit.  */
    private val C_LIMIT = 49.0
    /** S limit.  */
    private val S_LIMIT = 1e-5

    /**
     * Returns the natural logarithm of the gamma function &#915;(x).
     *
     * The implementation of this method is based on:
     *
     *  * [
 * Gamma Function](mathworld.wolfram.com/GammaFunction.html), equation (28).
     *  * [
 * Lanczos Approximation](mathworld.wolfram.com/LanczosApproximation.html), equations (1) through (5).
     *  * [Paul Godfrey, A note on
 * the computation of the convergent Lanczos complex Gamma approximation
](https://my.fit.edu/~gabdo/gamma.txt) *
     *
     *
     * @param x Value.
     * @return log(&#915;(x))
     */
    fun logGamma(x: Double): Double {
        val ret: Double

        if (x.isNaN() || x <= 0.0) {
            ret = Double.NaN
        } else {
            val g = 607.0 / 128.0

            var sum = 0.0
            for (i in LANCZOS.size - 1 downTo 1) {
                sum = sum + LANCZOS[i] / (x + i)
            }
            sum = sum + LANCZOS[0]

            val tmp = x + g + .5
            ret = (x + .5) * ln(tmp) - tmp +
                    HALF_LOG_2_PI + ln(sum / x)
        }

        return ret
    }

    /**
     * Returns the regularized gamma function P(a, x).
     *
     * The implementation of this method is based on:
     *
     *  *
     * [
 * Regularized Gamma Function](mathworld.wolfram.com/RegularizedGammaFunction.html), equation (1)
     *
     *  *
     * [
 * Incomplete Gamma Function](mathworld.wolfram.com/IncompleteGammaFunction.html), equation (4).
     *
     *  *
     * [
 * Confluent Hypergeometric Function of the First Kind](mathworld.wolfram.com/ConfluentHypergeometricFunctionoftheFirstKind.html), equation (1).
     *
     *
     *
     * @param a the a parameter.
     * @param x the value.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return the regularized gamma function P(a, x)
     * @throws MaxCountExceededException if the algorithm fails to converge.
     */
    @JvmOverloads
    fun regularizedGammaP(
        a: Double,
        x: Double,
        epsilon: Double = DEFAULT_EPSILON,
        maxIterations: Int = Int.MAX_VALUE
    ): Double {
        val ret: Double

        if (a.isNaN() || x.isNaN() || a <= 0.0 || x < 0.0) {
            ret = Double.NaN
        } else if (x == 0.0) {
            ret = 0.0
        } else if (x >= a + 1) {
            // use regularizedGammaQ because it should converge faster in this
            // case.
            ret = 1.0 - regularizedGammaQ(a, x, epsilon, maxIterations)
        } else {
            // calculate series
            var n = 0.0 // current element index
            var an = 1.0 / a // n-th element in the series
            var sum = an // partial sum
            while (abs(an / sum) > epsilon &&
                n < maxIterations &&
                sum < Double.POSITIVE_INFINITY
            ) {
                // compute next element in the series
                n = n + 1.0
                an = an * (x / (a + n))

                // update partial sum
                sum = sum + an
            }
            if (n >= maxIterations) {
                error("MaxCountExceeded - maxIterations: $maxIterations")
            } else if (sum.isInfinite()) {
                ret = 1.0
            } else {
                ret = exp(-x + a * ln(x) - logGamma(a)) * sum
            }
        }

        return ret
    }

    /**
     * Returns the regularized gamma function Q(a, x) = 1 - P(a, x).
     *
     * The implementation of this method is based on:
     *
     *  *
     * [
 * Regularized Gamma Function](mathworld.wolfram.com/RegularizedGammaFunction.html), equation (1).
     *
     *  *
     * [
 * Regularized incomplete gamma function: Continued fraction representations
 * (formula 06.08.10.0003)](functions.wolfram.com/GammaBetaErf/GammaRegularized/10/0003/)
     *
     *
     *
     * @param a the a parameter.
     * @param x the value.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return the regularized gamma function P(a, x)
     * @throws MaxCountExceededException if the algorithm fails to converge.
     */
    @JvmOverloads
    fun regularizedGammaQ(
        a: Double,
        x: Double,
        epsilon: Double = DEFAULT_EPSILON,
        maxIterations: Int = Int.MAX_VALUE
    ): Double {
        var ret: Double

        if (a.isNaN() || x.isNaN() || a <= 0.0 || x < 0.0) {
            ret = Double.NaN
        } else if (x == 0.0) {
            ret = 1.0
        } else if (x < a + 1.0) {
            // use regularizedGammaP because it should converge faster in this
            // case.
            ret = 1.0 - regularizedGammaP(a, x, epsilon, maxIterations)
        } else {
            // create continued fraction
            val cf = object : ContinuedFraction() {

                override fun getA(n: Int, x: Double): Double {
                    return 2.0 * n + 1.0 - a + x
                }

                override fun getB(n: Int, x: Double): Double {
                    return n * (a - n)
                }
            }

            ret = 1.0 / cf.evaluate(x, epsilon, maxIterations)
            ret = exp(-x + a * ln(x) - logGamma(a)) * ret
        }

        return ret
    }


    /**
     *
     * Computes the digamma function of x.
     *
     *
     * This is an independently written implementation of the algorithm described in
     * Jose Bernardo, Algorithm AS 103: Psi (Digamma) Function, Applied Statistics, 1976.
     *
     *
     * Some of the constants have been changed to increase accuracy at the moderate expense
     * of run-time.  The result should be accurate to within 10^-8 absolute tolerance for
     * x >= 10^-5 and within 10^-8 relative tolerance for x > 0.
     *
     *
     * Performance for large negative values of x will be quite expensive (proportional to
     * |x|).  Accuracy for negative values of x should be about 10^-8 absolute for results
     * less than 10^5 and 10^-8 relative for results larger than that.
     *
     * @param x Argument.
     * @return digamma(x) to within 10-8 relative or absolute error whichever is smaller.
     * @see [Digamma](https://en.wikipedia.org/wiki/Digamma_function)
     *
     * @see [Bernardo&apos;s original article ](https://www.uv.es/~bernardo/1976AppStatist.pdf)
     *
     * @since 2.0
     */
    fun digamma(x: Double): Double {
        if (x > 0 && x <= S_LIMIT) {
            // use method 5 from Bernardo AS103
            // accurate to O(x)
            return -GAMMA - 1 / x
        }

        if (x >= C_LIMIT) {
            // use method 4 (accurate to O(1/x^8)
            val inv = 1 / (x * x)
            //            1       1        1         1
            // log(x) -  --- - ------ + ------- - -------
            //           2 x   12 x^2   120 x^4   252 x^6
            return ln(x) - 0.5 / x - inv * (1.0 / 12 + inv * (1.0 / 120 - inv / 252))
        }

        return digamma(x + 1) - 1 / x
    }

    /**
     * Computes the trigamma function of x.
     * This function is derived by taking the derivative of the implementation
     * of digamma.
     *
     * @param x Argument.
     * @return trigamma(x) to within 10-8 relative or absolute error whichever is smaller
     * @see [Trigamma](https://en.wikipedia.org/wiki/Trigamma_function)
     *
     * @see Gamma.digamma
     * @since 2.0
     */
    fun trigamma(x: Double): Double {
        if (x > 0 && x <= S_LIMIT) {
            return 1 / (x * x)
        }

        if (x >= C_LIMIT) {
            val inv = 1 / (x * x)
            //  1    1      1       1       1
            //  - + ---- + ---- - ----- + -----
            //  x      2      3       5       7
            //      2 x    6 x    30 x    42 x
            return 1 / x + inv / 2 + inv / x * (1.0 / 6 - inv * (1.0 / 30 + inv / 42))
        }

        return trigamma(x + 1) + 1 / (x * x)
    }
}
/**
 * Default constructor.  Prohibit instantiation.
 */
/**
 * Returns the regularized gamma function P(a, x).
 *
 * @param a Parameter.
 * @param x Value.
 * @return the regularized gamma function P(a, x).
 * @throws MaxCountExceededException if the algorithm fails to converge.
 */
/**
 * Returns the regularized gamma function Q(a, x) = 1 - P(a, x).
 *
 * @param a the a parameter.
 * @param x the value.
 * @return the regularized gamma function Q(a, x)
 * @throws MaxCountExceededException if the algorithm fails to converge.
 */
