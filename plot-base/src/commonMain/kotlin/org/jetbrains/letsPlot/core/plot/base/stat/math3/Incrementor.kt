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

import org.jetbrains.letsPlot.core.plot.base.stat.math3.Incrementor.MaxCountExceededCallback
import kotlin.jvm.JvmOverloads


/**
 * Utility that increments a counter until a maximum is reached, at
 * which point, the instance will by default throw a
 * [MaxCountExceededException].
 * However, the user is able to override this behaviour by defining a
 * custom [callback][MaxCountExceededCallback], in order to e.g.
 * select which exception must be thrown.
 *
 * @version $Id$
 * @since 3.0
 */
class Incrementor
/**
 * Defines a maximal count and a callback method to be triggered at
 * counter exhaustion.
 *
 * @param max Maximal count.
 * @param cb Function to be called when the maximal count has been reached.
 */
@JvmOverloads constructor(
    /**
     * Upper limit for the counter.
     */
    /**
     * Gets the upper limit of the counter.
     *
     * @return the counter upper limit.
     */
    /**
     * Sets the upper limit for the counter.
     * This does not automatically reset the current count to zero (see
     * [.resetCount]).
     *
     * @param max Upper limit of the counter.
     */
    var maximalCount: Int = 0,
    /**
     * Function called at counter exhaustion.
     */
    private val maxCountCallback: MaxCountExceededCallback = object : MaxCountExceededCallback {
        /** {@inheritDoc}  */
        override fun trigger(maximalCount: Int) {
            error("MaxCountExceeded: $maximalCount")
        }
    }
) {
    /**
     * Current count.
     */
    /**
     * Gets the current count.
     *
     * @return the current count.
     */
    var count = 0
        private set

    /**
     * Checks whether a single increment is allowed.
     *
     * @return `false` if the next call to [ incrementCount][.incrementCount] will trigger a `MaxCountExceededException`,
     * `true` otherwise.
     */
    fun canIncrement(): Boolean {
        return count < maximalCount
    }

    /**
     * Performs multiple increments.
     * See the other [incrementCount][.incrementCount] method).
     *
     * @param value Number of increments.
     * @throws MaxCountExceededException at counter exhaustion.
     */
    fun incrementCount(value: Int) {
        for (i in 0 until value) {
            incrementCount()
        }
    }

    /**
     * Adds one to the current iteration count.
     * At counter exhaustion, this method will call the
     * [trigger][MaxCountExceededCallback.trigger] method of the
     * callback object passed to the
     * [constructor][.Incrementor].
     * If not explictly set, a default callback is used that will throw
     * a `MaxCountExceededException`.
     *
     * @throws MaxCountExceededException at counter exhaustion, unless a
     * custom [callback][MaxCountExceededCallback] has been set at
     * construction.
     */
    fun incrementCount() {
        if (++count > maximalCount) {
            maxCountCallback.trigger(maximalCount)
        }
    }

    /**
     * Resets the counter to 0.
     */
    fun resetCount() {
        count = 0
    }

    /**
     * Defines a method to be called at counter exhaustion.
     * The [trigger][.trigger] method should usually throw an exception.
     */
    interface MaxCountExceededCallback {
        /**
         * Function called when the maximal count has been reached.
         *
         * @param maximalCount Maximal count.
         */
        fun trigger(maximalCount: Int)
    }
}
/**
 * Default constructor.
 * For the new instance to be useful, the maximal count must be set
 * by calling [setMaximalCount][.setMaximalCount].
 */
/**
 * Defines a maximal count.
 *
 * @param max Maximal count.
 */
