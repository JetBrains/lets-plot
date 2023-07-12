/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package org.jetbrains.letsPlot.core.plot.base.stat.math3


/**
 * Interface for (univariate real) rootfinding algorithms.
 * Implementations will search for only one zero in the given interval.
 *
 * This class is not intended for use outside of the Apache Commons Math
 * library, regular user should rely on more specific interfaces like
 * [UnivariateSolver], [PolynomialSolver] or [ ].
 * @param <FUNC> Type of function to solve.
 *
 * @version $Id$
 * @since 3.0
 * @see UnivariateSolver
 *
 * @see PolynomialSolver
 *
 * @see DifferentiableUnivariateSolver
</FUNC> */
interface BaseUnivariateSolver<FUNC : UnivariateFunction> {
    /**
     * Get the maximum number of function evaluations.
     *
     * @return the maximum number of function evaluations.
     */
    val maxEvaluations: Int

    /**
     * Get the number of evaluations of the objective function.
     * The number of evaluations corresponds to the last call to the
     * `optimize` method. It is 0 if the method has not been
     * called yet.
     *
     * @return the number of evaluations of the objective function.
     */
    val evaluations: Int

    /**
     * Get the absolute accuracy of the solver.  Solutions returned by the
     * solver should be accurate to this tolerance, i.e., if  is the
     * absolute accuracy of the solver and `v` is a value returned by
     * one of the `solve` methods, then a root of the function should
     * exist somewhere in the interval (`v` - , `v` + ).
     *
     * @return the absolute accuracy.
     */
    val absoluteAccuracy: Double

    /**
     * Get the relative accuracy of the solver.  The contract for relative
     * accuracy is the same as [.getAbsoluteAccuracy], but using
     * relative, rather than absolute error.  If  is the relative accuracy
     * configured for a solver and `v` is a value returned, then a root
     * of the function should exist somewhere in the interval
     * (`v` -  `v`, `v` +  `v`).
     *
     * @return the relative accuracy.
     */
    val relativeAccuracy: Double

    /**
     * Get the function value accuracy of the solver.  If `v` is
     * a value returned by the solver for a function `f`,
     * then by contract, `|f(v)|` should be less than or equal to
     * the function value accuracy configured for the solver.
     *
     * @return the function value accuracy.
     */
    val functionValueAccuracy: Double

    /**
     * Solve for a zero root in the given interval.
     * A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.
     *
     * @param maxEval Maximum number of evaluations.
     * @param f Function to solve.
     * @param min Lower bound for the interval.
     * @param max Upper bound for the interval.
     * @return a value where the function is zero.
     * @throws org.apache.commons.math3.exception.MathIllegalArgumentException
     * if the arguments do not satisfy the requirements specified by the solver.
     * @throws org.apache.commons.math3.exception.TooManyEvaluationsException if
     * the allowed number of evaluations is exceeded.
     */
    fun solve(maxEval: Int, f: FUNC, min: Double, max: Double): Double

    /**
     * Solve for a zero in the given interval, start at `startValue`.
     * A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.
     *
     * @param maxEval Maximum number of evaluations.
     * @param f Function to solve.
     * @param min Lower bound for the interval.
     * @param max Upper bound for the interval.
     * @param startValue Start value to use.
     * @return a value where the function is zero.
     * @throws org.apache.commons.math3.exception.MathIllegalArgumentException
     * if the arguments do not satisfy the requirements specified by the solver.
     * @throws org.apache.commons.math3.exception.TooManyEvaluationsException if
     * the allowed number of evaluations is exceeded.
     */
    fun solve(maxEval: Int, f: FUNC, min: Double, max: Double, startValue: Double): Double

    /**
     * Solve for a zero in the vicinity of `startValue`.
     *
     * @param f Function to solve.
     * @param startValue Start value to use.
     * @return a value where the function is zero.
     * @param maxEval Maximum number of evaluations.
     * @throws org.apache.commons.math3.exception.MathIllegalArgumentException
     * if the arguments do not satisfy the requirements specified by the solver.
     * @throws org.apache.commons.math3.exception.TooManyEvaluationsException if
     * the allowed number of evaluations is exceeded.
     */
    fun solve(maxEval: Int, f: FUNC, startValue: Double): Double
}
