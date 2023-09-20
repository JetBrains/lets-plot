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
 * An interface representing a univariate real function.
 * <br></br>
 * When a *user-defined* function encounters an error during
 * evaluation, the [value][.value] method should throw a
 * *user-defined* unchecked exception.
 * <br></br>
 * The following code excerpt shows the recommended way to do that using
 * a root solver as an example, but the same construct is applicable to
 * ODE integrators or optimizers.
 *
 * <pre>
 * private static class LocalException extends RuntimeException {
 * // The x value that caused the problem.
 * private final double x;
 *
 * public LocalException(double x) {
 * this.x = x;
 * }
 *
 * public double getX() {
 * return x;
 * }
 * }
 *
 * private static class MyFunction implements UnivariateFunction {
 * public double value(double x) {
 * double y = hugeFormula(x);
 * if (somethingBadHappens) {
 * throw new LocalException(x);
 * }
 * return y;
 * }
 * }
 *
 * public void compute() {
 * try {
 * solver.solve(maxEval, new MyFunction(a, b, c), min, max);
 * } catch (LocalException le) {
 * // Retrieve the x value.
 * }
 * }
</pre> *
 *
 * As shown, the exception is local to the user's code and it is guaranteed
 * that Apache Commons Math will not catch it.
 *
 * @version $Id: UnivariateFunction.java 1244107 2012-02-14 16:17:55Z erans $
 */
interface UnivariateFunction {
    /**
     * Compute the value of the function.
     *
     * @param x Point at which the function value should be computed.
     * @return the value of the function.
     * @throws IllegalArgumentException when the activated method itself can
     * ascertain that a precondition, specified in the API expressed at the
     * level of the activated method, has been violated.
     * When Commons Math throws an `IllegalArgumentException`, it is
     * usually the consequence of checking the actual parameters passed to
     * the method.
     */
    fun value(x: Double): Double
}
