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


/**
 * Provide a default implementation for several functions useful to generic
 * solvers.
 *
 * @param <FUNC> Type of function to solve.
 *
 * @version $Id$
 * @since 2.0
</FUNC> */
abstract class BaseAbstractUnivariateSolver<FUNC : UnivariateFunction>
/**
 * Construct a solver with given accuracies.
 *
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 * @param functionValueAccuracy Maximum function value error.
 */
@JvmOverloads protected constructor(
    /** Relative accuracy.  */
    /**
     * {@inheritDoc}
     */
    override val relativeAccuracy: Double,
    /** Absolute accuracy.  */
    /**
     * {@inheritDoc}
     */
    override val absoluteAccuracy: Double,
    /** Function value accuracy.  */
    /**
     * {@inheritDoc}
     */
    override val functionValueAccuracy: Double = DEFAULT_FUNCTION_VALUE_ACCURACY
) : BaseUnivariateSolver<FUNC> {
    /** Evaluations counter.  */
    private val myEvaluations = Incrementor()
    /** Lower end of search interval.  */
    /**
     * @return the lower end of the search interval.
     */
    var min: Double = 0.toDouble()
        private set
    /** Higher end of search interval.  */
    /**
     * @return the higher end of the search interval.
     */
    var max: Double = 0.toDouble()
        private set
    /** Initial guess.  */
    /**
     * @return the initial guess.
     */
    var startValue: Double = 0.toDouble()
        private set
    /** Function to solve.  */
    private var function: FUNC? = null

    /** {@inheritDoc}  */
    override val maxEvaluations: Int
        get() = myEvaluations.maximalCount

    /**
     * Construct a solver with given absolute accuracy.
     *
     * @param absoluteAccuracy Maximum absolute error.
     */
    protected constructor(absoluteAccuracy: Double) : this(
        DEFAULT_RELATIVE_ACCURACY,
        absoluteAccuracy,
        DEFAULT_FUNCTION_VALUE_ACCURACY
    ) {
    }

    override val evaluations = myEvaluations.count

    /**
     * Compute the objective function value.
     *
     * @param point Point at which the objective function must be evaluated.
     * @return the objective function value at specified point.
     * @throws TooManyEvaluationsException if the maximal number of evaluations
     * is exceeded.
     */
    protected fun computeObjectiveValue(point: Double): Double {
        incrementEvaluationCount()
        return function!!.value(point)
    }

    /**
     * Prepare for computation.
     * Subclasses must call this method if they override any of the
     * `solve` methods.
     *
     * @param f Function to solve.
     * @param min Lower bound for the interval.
     * @param max Upper bound for the interval.
     * @param startValue Start value to use.
     * @param maxEval Maximum number of evaluations.
     */
    protected fun setup(
        maxEval: Int,
        f: FUNC,
        min: Double, max: Double,
        startValue: Double
    ) {

        // Reset.
        this.min = min
        this.max = max
        this.startValue = startValue
        function = f
        myEvaluations.maximalCount = maxEval
        myEvaluations.resetCount()
    }

    /** {@inheritDoc}  */
    override fun solve(maxEval: Int, f: FUNC, min: Double, max: Double, startValue: Double): Double {
        // Initialization.
        setup(maxEval, f, min, max, startValue)

        // Perform computation.
        return doSolve()
    }

    /** {@inheritDoc}  */
    override fun solve(maxEval: Int, f: FUNC, min: Double, max: Double): Double {
        return solve(maxEval, f, min, max, min + 0.5 * (max - min))
    }

    /** {@inheritDoc}  */
    override fun solve(maxEval: Int, f: FUNC, startValue: Double): Double {
        return solve(maxEval, f, Double.NaN, Double.NaN, startValue)
    }

    /**
     * Method for implementing actual optimization algorithms in derived
     * classes.
     *
     * @return the root.
     * @throws TooManyEvaluationsException if the maximal number of evaluations
     * is exceeded.
     * @throws NoBracketingException if the initial search interval does not bracket
     * a root and the solver requires it.
     */
    protected abstract fun doSolve(): Double

    /**
     * Check whether the function takes opposite signs at the endpoints.
     *
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @return `true` if the function values have opposite signs at the
     * given points.
     */
    protected fun isBracketing(
        lower: Double,
        upper: Double
    ): Boolean {
        return UnivariateSolverUtils.isBracketing(function!!, lower, upper)
    }

    /**
     * Check whether the arguments form a (strictly) increasing sequence.
     *
     * @param start First number.
     * @param mid Second number.
     * @param end Third number.
     * @return `true` if the arguments form an increasing sequence.
     */
    protected fun isSequence(
        start: Double,
        mid: Double,
        end: Double
    ): Boolean {
        return UnivariateSolverUtils.isSequence(start, mid, end)
    }

    /**
     * Check that the endpoints specify an interval.
     *
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @throws org.apache.commons.math3.exception.NumberIsTooLargeException
     * if `lower >= upper`.
     */
    protected fun verifyInterval(
        lower: Double,
        upper: Double
    ) {
        UnivariateSolverUtils.verifyInterval(lower, upper)
    }

    /**
     * Check that `lower < initial < upper`.
     *
     * @param lower Lower endpoint.
     * @param initial Initial value.
     * @param upper Upper endpoint.
     * @throws org.apache.commons.math3.exception.NumberIsTooLargeException
     * if `lower >= initial` or `initial >= upper`.
     */
    protected fun verifySequence(
        lower: Double,
        initial: Double,
        upper: Double
    ) {
        UnivariateSolverUtils.verifySequence(lower, initial, upper)
    }

    /**
     * Check that the endpoints specify an interval and the function takes
     * opposite signs at the endpoints.
     *
     * @param lower Lower endpoint.
     * @param upper Upper endpoint.
     * @throws NoBracketingException if
     * the function has the same sign at the endpoints.
     */
    protected fun verifyBracketing(
        lower: Double,
        upper: Double
    ) {
        UnivariateSolverUtils.verifyBracketing(function!!, lower, upper)
    }

    /**
     * Increment the evaluation count by one.
     * Method [.computeObjectiveValue] calls this method internally.
     * It is provided for subclasses that do not exclusively use
     * `computeObjectiveValue` to solve the function.
     * See e.g. [AbstractDifferentiableUnivariateSolver].
     */
    protected fun incrementEvaluationCount() {
        myEvaluations.incrementCount()

    }

    companion object {
        /** Default relative accuracy.  */
        private val DEFAULT_RELATIVE_ACCURACY = 1e-14
        /** Default function value accuracy.  */
        private val DEFAULT_FUNCTION_VALUE_ACCURACY = 1e-15
    }
}
/**
 * Construct a solver with given accuracies.
 *
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 */
