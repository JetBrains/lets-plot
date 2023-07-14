/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.math3


/**
 * Base class for solvers.
 *
 * @version $Id$
 * @since 3.0
 */
abstract class AbstractUnivariateSolver : BaseAbstractUnivariateSolver<UnivariateFunction>,
    UnivariateSolver {
    /**
     * Construct a solver with given absolute accuracy.
     *
     * @param absoluteAccuracy Maximum absolute error.
     */
    protected constructor(absoluteAccuracy: Double) : super(absoluteAccuracy) {}

    /**
     * Construct a solver with given accuracies.
     *
     * @param relativeAccuracy Maximum relative error.
     * @param absoluteAccuracy Maximum absolute error.
     */
    protected constructor(
        relativeAccuracy: Double,
        absoluteAccuracy: Double
    ) : super(relativeAccuracy, absoluteAccuracy) {
    }

    /**
     * Construct a solver with given accuracies.
     *
     * @param relativeAccuracy Maximum relative error.
     * @param absoluteAccuracy Maximum absolute error.
     * @param functionValueAccuracy Maximum function value error.
     */
    protected constructor(
        relativeAccuracy: Double,
        absoluteAccuracy: Double,
        functionValueAccuracy: Double
    ) : super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy) {
    }
}
