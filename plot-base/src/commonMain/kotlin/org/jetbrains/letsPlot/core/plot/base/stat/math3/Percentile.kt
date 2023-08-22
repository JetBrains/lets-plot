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

import kotlin.math.floor
import kotlin.math.min


/**
 * Provides percentile computation.
 * <p>
 * There are several commonly used methods for estimating percentiles (a.k.a.
 * quantiles) based on sample data.  For large samples, the different methods
 * agree closely, but when sample sizes are small, different methods will give
 * significantly different results.  The algorithm implemented here works as follows:
 * <ol>
 * <li>Let <code>n</code> be the length of the (sorted) array and
 * <code>0 < p <= 100</code> be the desired percentile.</li>
 * <li>If <code> n = 1 </code> return the unique array element (regardless of
 * the value of <code>p</code>); otherwise </li>
 * <li>Compute the estimated percentile position
 * <code> pos = p * (n + 1) / 100</code> and the difference, <code>d</code>
 * between <code>pos</code> and <code>floor(pos)</code> (i.e. the fractional
 * part of <code>pos</code>).</li>
 * <li> If <code>pos < 1</code> return the smallest element in the array.</li>
 * <li> Else if <code>pos >= n</code> return the largest element in the array.</li>
 * <li> Else let <code>lower</code> be the element in position
 * <code>floor(pos)</code> in the array and let <code>upper</code> be the
 * next element in the array.  Return <code>lower + d * (upper - lower)</code>
 * </li>
 * </ol></p>
 * <p>
 * To compute percentiles, the data must be at least partially ordered.  Input
 * arrays are copied and recursively partitioned using an ordering definition.
 * The ordering used by <code>Arrays.sort(double[])</code> is the one determined
 * by {@link java.lang.Double#compareTo(Double)}.  This ordering makes
 * <code>Double.NaN</code> larger than any other value (including
 * <code>Double.POSITIVE_INFINITY</code>).  Therefore, for example, the median
 * (50th percentile) of
 * <code>{0, 1, 2, 3, 4, Double.NaN}</code> evaluates to <code>2.5.</code></p>
 * <p>
 * Since percentile estimation usually involves interpolation between array
 * elements, arrays containing  <code>NaN</code> or infinite values will often
 * result in <code>NaN</code> or infinite values returned.</p>
 * <p>
 * Since 2.2, Percentile uses only selection instead of complete sorting
 * and caches selection algorithm state between calls to the various
 * {@code evaluate} methods. This greatly improves efficiency, both for a single
 * percentile and multiple percentile computations. To maximize performance when
 * multiple percentiles are computed based on the same data, users should set the
 * data array once using either one of the {@link #evaluate(double[], double)} or
 * {@link #setData(double[])} methods and thereafter {@link #evaluate(double)}
 * with just the percentile provided.
 * </p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 *
 * @version $Id: Percentile.java 1244107 2012-02-14 16:17:55Z erans $
 */

object Percentile {

    /** Minimum size under which we use a simple insertion sort rather than Hoare's select.  */
    private const val MIN_SELECT_SIZE = 15

    /** Maximum number of partitioning pivots cached (each level double the number of pivots).  */
    private const val MAX_CACHED_LEVELS = 10

    /**
     * Returns an estimate of the `p`th percentile of the values
     * in the `values` array.
     *
     * Calls to this method do not modify the internal `quantile`
     * state of this statistic.
     *
     *  * Returns `Double.NaN` if `values` has length `0`
     *  * Returns (for any value of `p`) `values[0]`
     * if `values` has length `1`
     *  * Throws `IllegalArgumentException` if `values`
     * is null or p is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100)
     *
     * See [Percentile] for a description of the percentile estimation
     * algorithm used.
     *
     * @param values input array of values
     * @param p the percentile value to compute
     * @return the percentile value or Double.NaN if the array is empty
     * @throws IllegalArgumentException if `values` is null
     * or p is invalid
     */
    fun evaluate(values: DoubleArray, p: Double): Double {
        test(values, 0, 0, false)
        return evaluate(values, 0, values.size, p)
    }

    /**
     * This method is used by `evaluate(double[], int, int)` methods
     * to verify that the input parameters designate a subarray of positive length.
     *
     * returns `true` iff the parameters designate a subarray of
     * non-negative length
     *  * throws `IllegalArgumentException` if the array is null or
     * or the indices are invalid
     *  * returns `false` if the array is non-null, but
     * `length` is 0 unless `allowEmpty` is `true`
     *
     * @param values the input array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @param allowEmpty if `true` then zero length arrays are allowed
     * @return true if the parameters are valid
     * @throws IllegalArgumentException if the indices are invalid or the array is null
     * @since 3.0
     */
     private fun test(values: DoubleArray?, begin: Int, length: Int, allowEmpty: Boolean): Boolean {
        if (values == null) {
            error("Input array")
        }
        if (begin < 0) {
            error("start postion < 0")
        }
        if (length < 0) {
            error("length < 0")
        }
        if (begin + length > values.size) {
            error("subarray ends after array end")
        }
        return if (length == 0 && !allowEmpty) {
            false
        } else true
    }

    /**
     * Returns an estimate of the `p`th percentile of the values
     * in the `values` array, starting with the element in (0-based)
     * position `begin` in the array and including `length`
     * values.
     *
     * Calls to this method do not modify the internal `quantile`
     * state of this statistic.
     *
     *  * Returns `Double.NaN` if `length = 0`
     *  * Returns (for any value of `p`) `values[begin]`
     * if `length = 1 `
     *  * Throws `IllegalArgumentException` if `values`
     * is null , `begin` or `length` is invalid, or
     * `p` is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100)
     *
     * See [Percentile] for a description of the percentile estimation
     * algorithm used.
     *
     * @param values array of input values
     * @param p  the percentile to compute
     * @param begin  the first (0-based) element to include in the computation
     * @param length  the number of array elements to include
     * @return  the percentile value
     * @throws IllegalArgumentException if the parameters are not valid or the
     * input array is null
     */
    private fun evaluate(values: DoubleArray, begin: Int, length: Int, p: Double): Double {

        test(values, begin, length, false)

        if (p > 100 || p <= 0) {
            error("out of bounds quantile value: $p, must be in (0, 100]")
        }
        if (length == 0) {
            return Double.NaN
        }
        if (length == 1) {
            return values[begin] // always return single value for n = 1
        }
        val n = length.toDouble()
        val pos = p * (n + 1) / 100
        val fpos: Double = floor(pos)
        val intPos = fpos.toInt()
        val dif = pos - fpos

        val work = DoubleArray(length)
        values.copyInto(work, 0, begin, length)

        val pivotsHeap = IntArray((0x1 shl MAX_CACHED_LEVELS) - 1)
        pivotsHeap.fill(-1)

        if (pos < 1) {
            return select(work, pivotsHeap, 0)
        }
        if (pos >= n) {
            return select(work, pivotsHeap, length - 1)
        }
        val lower: Double = select(work, pivotsHeap, intPos - 1)
        val upper: Double = select(work, pivotsHeap, intPos)
        return lower + dif * (upper - lower)
    }

    /**
     * Select the k<sup>th</sup> smallest element from work array
     * @param work work array (will be reorganized during the call)
     * @param pivotsHeap set of pivot index corresponding to elements that
     * are already at their sorted location, stored as an implicit heap
     * (i.e. a sorted binary tree stored in a flat array, where the
     * children of a node at index n are at indices 2n+1 for the left
     * child and 2n+2 for the right child, with 0-based indices)
     * @param k index of the desired element
     * @return k<sup>th</sup> smallest element
     */
    private fun select(work: DoubleArray, pivotsHeap: IntArray, k: Int): Double {
        var begin = 0
        var end = work.size
        var node = 0
        while (end - begin > MIN_SELECT_SIZE) {
            val pivot: Int
            if (node < pivotsHeap.size && pivotsHeap[node] >= 0) { // the pivot has already been found in a previous call
                                                                   // and the array has already been partitioned around it
                pivot = pivotsHeap[node]
            } else { // select a pivot and partition work array around it
                pivot = partition(work, begin, end, medianOf3(work, begin, end))
                if (node < pivotsHeap.size) {
                    pivotsHeap[node] = pivot
                }
            }
            if (k == pivot) { // the pivot was exactly the element we wanted
                return work[k]
            } else if (k < pivot) { // the element is in the left partition
                end = pivot
                node = min(2 * node + 1, pivotsHeap.size) // the min is here to avoid integer overflow
            } else { // the element is in the right partition
                begin = pivot + 1
                node = min(2 * node + 2, pivotsHeap.size) // the min is here to avoid integer overflow
            }
        }
        // the element is somewhere in the small sub-array
        // sort the sub-array using insertion sort
        insertionSort(work, begin, end)
        return work[k]
    }


    /**
     * Partition an array slice around a pivot
     *
     * Partitioning exchanges array elements such that all elements
     * smaller than pivot are before it and all elements larger than
     * pivot are after it
     *
     * @param work data array
     * @param begin index of the first element of the slice
     * @param end index after the last element of the slice
     * @param pivot initial index of the pivot
     * @return index of the pivot after partition
     */
    private fun partition(work: DoubleArray, begin: Int, end: Int, pivot: Int): Int {
        val value = work[pivot]
        work[pivot] = work[begin]
        var i = begin + 1
        var j = end - 1
        while (i < j) {
            while (i < j && work[j] >= value) {
                --j
            }
            while (i < j && work[i] <= value) {
                ++i
            }
            if (i < j) {
                val tmp = work[i]
                work[i++] = work[j]
                work[j--] = tmp
            }
        }
        if (i >= end || work[i] > value) {
            --i
        }
        work[begin] = work[i]
        work[i] = value
        return i
    }

    /** Select a pivot index as the median of three
     * @param work data array
     * @param begin index of the first element of the slice
     * @param end index after the last element of the slice
     * @return the index of the median element chosen between the
     * first, the middle and the last element of the array slice
     */
    private fun medianOf3(work: DoubleArray, begin: Int, end: Int): Int {
        val inclusiveEnd = end - 1
        val middle = begin + (inclusiveEnd - begin) / 2
        val wBegin = work[begin]
        val wMiddle = work[middle]
        val wEnd = work[inclusiveEnd]
        return if (wBegin < wMiddle) {
            if (wMiddle < wEnd) {
                middle
            } else {
                if (wBegin < wEnd) inclusiveEnd else begin
            }
        } else {
            if (wBegin < wEnd) {
                begin
            } else {
                if (wMiddle < wEnd) inclusiveEnd else middle
            }
        }
    }

    /**
     * Sort in place a (small) array slice using insertion sort
     * @param work array to sort
     * @param begin index of the first element of the slice to sort
     * @param end index after the last element of the slice to sort
     */
    private fun insertionSort(work: DoubleArray, begin: Int, end: Int) {
        for (j in begin + 1 until end) {
            val saved = work[j]
            var i = j - 1
            while (i >= begin && saved < work[i]) {
                work[i + 1] = work[i]
                i--
            }
            work[i + 1] = saved
        }
    }
}