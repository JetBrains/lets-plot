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
 * Arrays utilities.
 *
 * @since 3.0
 * @version $Id$
 */

object MathArrays {

    /**
     * Specification of ordering direction.
     */
    enum class OrderDirection {
        /** Constant for increasing direction.  */
        INCREASING,
        /** Constant for decreasing direction.  */
        DECREASING
    }

    /**
     * Check that the given array is sorted.
     *
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @param abort Whether to throw an exception if the check fails.
     * @return `true` if the array is sorted.
     * @throws NonMonotonicSequenceException if the array is not sorted
     * and `abort` is `true`.
     */
    fun checkOrder(
        `val`: DoubleArray, dir: OrderDirection?,
        strict: Boolean, abort: Boolean
    ): Boolean {
        var previous = `val`[0]
        val max = `val`.size
        var index: Int
        index = 1
        ITEM@ while (index < max) {
            when (dir) {
                OrderDirection.INCREASING -> if (strict) {
                    if (`val`[index] <= previous) {
                        break@ITEM
                    }
                } else {
                    if (`val`[index] < previous) {
                        break@ITEM
                    }
                }
                OrderDirection.DECREASING -> if (strict) {
                    if (`val`[index] >= previous) {
                        break@ITEM
                    }
                } else {
                    if (`val`[index] > previous) {
                        break@ITEM
                    }
                }
                else -> error("")
            }
            previous = `val`[index]
            index++
        }
        if (index == max) { // Loop completed.
            return true
        }
        // Loop early exit means wrong ordering.
        return if (abort) {
            error("Non monotonic sequence")
        } else {
            false
        }
    }

    /**
     * Check that the given array is sorted.
     *
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @throws NonMonotonicSequenceException if the array is not sorted.
     * @since 2.2
     */
    fun checkOrder(
        `val`: DoubleArray, dir: OrderDirection?,
        strict: Boolean
    ) {
        checkOrder(`val`, dir, strict, true)
    }

    /**
     * Check that the given array is sorted in strictly increasing order.
     *
     * @param val Values.
     * @throws NonMonotonicSequenceException if the array is not sorted.
     * @since 2.2
     */
    fun checkOrder(`val`: DoubleArray) {
        checkOrder(`val`, OrderDirection.INCREASING, true)
    }
}