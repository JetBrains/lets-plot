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
import kotlin.math.min

/**
 * Utility routines for [UnivariateSolver] objects.
 *
 * @version $Id: UnivariateSolverUtils.java 1244107 2012-02-14 16:17:55Z erans $
 */
object UnivariateSolverUtils {

    /**
     * Convenience method to find a zero of a univariate real function.  A default
     * solver is used.
     *
     * @param function Function.
     * @param x0 Lower bound for the interval.
     * @param x1 Upper bound for the interval.
     * @return a value where the function is zero.
     * @throws IllegalArgumentException if f is null or the endpoints do not
     * specify a valid interval.
     */
    fun solve(function: UnivariateFunction, x0: Double, x1: Double): Double {
        val solver = BrentSolver()
        return solver.solve(Int.MAX_VALUE, function, x0, x1)
    }

    /**
     * Convenience method to find a zero of a univariate real function.  A default
     * solver is used.
     *
     * @param function Function.
     * @param x0 Lower bound for the interval.
     * @param x1 Upper bound for the interval.
     * @param absoluteAccuracy Accuracy to be used by the solver.
     * @return a value where the function is zero.
     * @throws IllegalArgumentException if `function` is `null`,
     * the endpoints do not specify a valid interval, or the absolute accuracy
     * is not valid for the default solver.
     */
    fun solve(
        function: UnivariateFunction,
        x0: Double, x1: Double,
        absoluteAccuracy: Double
    ): Double {
        val solver = BrentSolver(absoluteAccuracy)
        return solver.solve(Int.MAX_VALUE, function, x0, x1)
    }

    /** Force a root found by a non-bracketing solver to lie on a specified side,
     * as if the solver was a bracketing one.
     * @param maxEval maximal number of new evaluations of the function
     * (evaluations already done for finding the root should have already been subtracted
     * from this number)
     * @param f function to solve
     * @param bracketing bracketing solver to use for shifting the root
     * @param baseRoot original root found by a previous non-bracketing solver
     * @param min minimal bound of the search interval
     * @param max maximal bound of the search interval
     * @param allowedSolution the kind of solutions that the root-finding algorithm may
     * accept as solutions.
     * @return a root approximation, on the specified side of the exact root
     */
    fun forceSide(
        maxEval: Int, f: UnivariateFunction,
        bracketing: BracketedUnivariateSolver<UnivariateFunction>,
        baseRoot: Double, min: Double, max: Double,
        allowedSolution: AllowedSolution
    ): Double {

        if (allowedSolution == AllowedSolution.ANY_SIDE) {
            // no further bracketing required
            return baseRoot
        }

        // find a very small interval bracketing the root
        val step = max(
            bracketing.absoluteAccuracy,
            abs(baseRoot * bracketing.relativeAccuracy)
        )
        var xLo = max(min, baseRoot - step)
        var fLo = f.value(xLo)
        var xHi = min(max, baseRoot + step)
        var fHi = f.value(xHi)
        var remainingEval = maxEval - 2
        while (remainingEval > 0) {

            if (fLo >= 0 && fHi <= 0 || fLo <= 0 && fHi >= 0) {
                // compute the root on the selected side
                return bracketing.solve(remainingEval, f, xLo, xHi, baseRoot, allowedSolution)
            }

            // try increasing the interval
            var changeLo = false
            var changeHi = false
            if (fLo < fHi) {
                // increasing function
                if (fLo >= 0) {
                    changeLo = true
                } else {
                    changeHi = true
                }
            } else if (fLo > fHi) {
                // decreasing function
                if (fLo <= 0) {
                    changeLo = true
                } else {
                    changeHi = true
                }
            } else {
                // unknown variation
                changeLo = true
                changeHi = true
            }

            // update the lower bound
            if (changeLo) {
                xLo = max(min, xLo - step)
                fLo = f.value(xLo)
                remainingEval--
            }

            // update the higher bound
            if (changeHi) {
                xHi = min(max, xHi + step)
                fHi = f.value(xHi)
                remainingEval--
            }

        }

        error("NoBracketing")
        //Exception - FAILED_BRACKETING xLo:$xLo, xHi: $xHi, fLo: $fLo, fHi: $fHi, maxEval: ${maxEval - remainingEval}, maxEval: $maxEval, baseRoot: $baseRoot, min: $min, max: $max")
    }

    /**
     * This method attempts to find two values a and b satisfying
     *  *  ` lowerBound <= a < initial < b <= upperBound`
     *  *  ` f(a) * f(b) <= 0 `
     *
     * If f is continuous on `[a,b],` this means that `a`
     * and `b` bracket a root of f.
     *
     *
     * The algorithm starts by setting
     * `a := initial -1; b := initial +1,` examines the value of the
     * function at `a` and `b` and keeps moving
     * the endpoints out by one unit each time through a loop that terminates
     * when one of the following happens:
     *  *  ` f(a) * f(b) <= 0 ` --  success!
     *  *  ` a = lower ` and ` b = upper`
     * -- NoBracketingException
     *  *  ` maximumIterations` iterations elapse
     * -- NoBracketingException
     *
     * @param function Function.
     * @param initial Initial midpoint of interval being expanded to
     * bracket a root.
     * @param lowerBound Lower bound (a is never lower than this value).
     * @param upperBound Upper bound (b never is greater than this
     * value).
     * @param maximumIterations Maximum number of iterations to perform
     * @return a two element array holding a and b.
     * @throws NoBracketingException if the algorithm fails to find a and b
     * satisfying the desired conditions.
     * @throws IllegalArgumentException if function is null, maximumIterations
     * is not positive, or initial is not between lowerBound and upperBound.
     */
    @JvmOverloads
    fun bracket(
        function: UnivariateFunction,
        initial: Double,
        lowerBound: Double, upperBound: Double,
        maximumIterations: Int = Int.MAX_VALUE
    ): DoubleArray {
        if (maximumIterations <= 0) {
            error("NotStrictlyPositive")
        }
        verifySequence(lowerBound, initial, upperBound)

        var a = initial
        var b = initial
        var fa: Double
        var fb: Double
        var numIterations = 0

        do {
            a = max(a - 1.0, lowerBound)
            b = min(b + 1.0, upperBound)
            fa = function.value(a)

            fb = function.value(b)
            ++numIterations
        } while (fa * fb > 0.0 && numIterations < maximumIterations &&
            (a > lowerBound || b < upperBound)
        )

        if (fa * fb > 0.0) {
            error("NoBracketing")
            //LocalizedFormats.FAILED_BRACKETING,
            //a, b, fa, fb,
            //numIterations, maximumIterations, initial,
            //lowerBound, upperBound
        }

        return doubleArrayOf(a, b)
    }

    /**
     * Compute the midpoint of two values.
     *
     * @param a first value.
     * @param b second value.
     * @return the midpoint.
     */
    fun midpoint(a: Double, b: Double): Double {
        return (a + b) * 0.5
    }

    /**
     * Check whether the interval bounds bracket a root. That is, if the
     * values at the endpoints are not equal to zero, then the function takes
     * opposite signs at the endpoints.
     *
     * @param function Function.
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @return `true` if the function values have opposite signs at the
     * given points.
     */
    fun isBracketing(
        function: UnivariateFunction,
        lower: Double,
        upper: Double
    ): Boolean {
        val fLo = function.value(lower)
        val fHi = function.value(upper)
        return fLo >= 0 && fHi <= 0 || fLo <= 0 && fHi >= 0
    }

    /**
     * Check whether the arguments form a (strictly) increasing sequence.
     *
     * @param start First number.
     * @param mid Second number.
     * @param end Third number.
     * @return `true` if the arguments form an increasing sequence.
     */
    fun isSequence(
        start: Double,
        mid: Double,
        end: Double
    ): Boolean {
        return start < mid && mid < end
    }

    /**
     * Check that the endpoints specify an interval.
     *
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @throws NumberIsTooLargeException if `lower >= upper`.
     */
    fun verifyInterval(
        lower: Double,
        upper: Double
    ) {
        if (lower >= upper) {
            error("NumberIsTooLarge")
            //LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL,
            //lower, upper, false
        }
    }

    /**
     * Check that `lower < initial < upper`.
     *
     * @param lower Lower endpoint.
     * @param initial Initial value.
     * @param upper Upper endpoint.
     * @throws NumberIsTooLargeException if `lower >= initial` or
     * `initial >= upper`.
     */
    fun verifySequence(
        lower: Double,
        initial: Double,
        upper: Double
    ) {
        verifyInterval(lower, initial)
        verifyInterval(initial, upper)
    }

    /**
     * Check that the endpoints specify an interval and the end points
     * bracket a root.
     *
     * @param function Function.
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @throws NoBracketingException if function has the same sign at the
     * endpoints.
     */
    fun verifyBracketing(
        function: UnivariateFunction,
        lower: Double,
        upper: Double
    ) {
        verifyInterval(lower, upper)
        if (!isBracketing(function, lower, upper)) {
            error("NoBracketing")
            //lower, upper,
            //function.value(lower),
            //function.value(upper)
        }
    }
}
/**
 * Class contains only static methods.
 */
/**
 * This method attempts to find two values a and b satisfying
 *  *  ` lowerBound <= a < initial < b <= upperBound`
 *  *  ` f(a) * f(b) < 0 `
 *
 * If f is continuous on `[a,b],` this means that `a`
 * and `b` bracket a root of f.
 *
 *
 * The algorithm starts by setting
 * `a := initial -1; b := initial +1,` examines the value of the
 * function at `a` and `b` and keeps moving
 * the endpoints out by one unit each time through a loop that terminates
 * when one of the following happens:
 *  *  ` f(a) * f(b) < 0 ` --  success!
 *  *  ` a = lower ` and ` b = upper`
 * -- NoBracketingException
 *  *  ` Integer.MAX_VALUE` iterations elapse
 * -- NoBracketingException
 *
 *
 *
 * **Note: ** this method can take
 * `Integer.MAX_VALUE` iterations to throw a
 * `ConvergenceException.`  Unless you are confident that there
 * is a root between `lowerBound` and `upperBound`
 * near `initial,` it is better to use
 * [.bracket],
 * explicitly specifying the maximum number of iterations.
 *
 * @param function Function.
 * @param initial Initial midpoint of interval being expanded to
 * bracket a root.
 * @param lowerBound Lower bound (a is never lower than this value)
 * @param upperBound Upper bound (b never is greater than this
 * value).
 * @return a two-element array holding a and b.
 * @throws NoBracketingException if a root cannot be bracketted.
 * @throws IllegalArgumentException if function is null, maximumIterations
 * is not positive, or initial is not between lowerBound and upperBound.
 */
