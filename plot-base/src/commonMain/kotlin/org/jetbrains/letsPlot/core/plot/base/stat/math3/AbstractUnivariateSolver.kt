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
