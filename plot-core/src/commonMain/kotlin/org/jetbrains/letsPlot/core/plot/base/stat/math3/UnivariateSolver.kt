/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package org.jetbrains.letsPlot.core.plot.base.stat.math3


/**
 * Interface for (univariate real) root-finding algorithms.
 * Implementations will search for only one zero in the given interval.
 *
 * @version $Id: UnivariateSolver.java 1244107 2012-02-14 16:17:55Z erans $
 */
interface UnivariateSolver :
    BaseUnivariateSolver<UnivariateFunction>
