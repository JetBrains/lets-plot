/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Iterables
import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Ordering
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min


object SeriesUtil {
    const val TINY = 1e-50
    private const val MAX_DECIMAL_PLACES = 12

    private val REAL_NUMBER = { it: Double? -> isFinite(it) }

    val NEGATIVE_NUMBER = { input: Double -> input < 0 }

    fun isBeyondPrecision(range: DoubleSpan): Boolean {
        val delta = range.length
        return delta < TINY ||                       // ??
                isBeyondPrecision(range.lowerEnd, delta) ||
                isBeyondPrecision(range.upperEnd, delta)
    }

    fun isBeyondPrecision(base: Double, delta: Double): Boolean {
        val basePower = log10(base)
        val deltaPower = log10(delta)
        return (basePower - deltaPower) > MAX_DECIMAL_PLACES
    }

    fun checkedDoubles(values: Iterable<*>): CheckedDoubleIterable {
        return CheckedDoubleIterable(values)
    }

    fun checkedDoubles(values: List<*>): CheckedDoubleList {
        return CheckedDoubleList(values)
    }

    fun isFinite(v: Double?): Boolean {
        return v != null && v.isFinite()
    }

    fun asFinite(v: Double?, defaultValue: Double): Double {
        return if (v != null && v.isFinite())
            v
        else
            defaultValue
    }

    fun finiteOrNull(v: Double?): Double? {
        return if (v != null && v.isFinite())
            v
        else
            null
    }

    fun isFinite(v: Double): Boolean {
        return v.isFinite()
    }

    fun isFinite(v: DoubleVector): Boolean {
        return v.isFinite
    }

    fun finiteOrNull(v: DoubleVector?): DoubleVector? {
        return if (v != null && v.isFinite)
            v
        else
            null
    }

    fun allFinite(v0: Double?, v1: Double?): Boolean {
        return isFinite(v0) && isFinite(v1)
    }

    fun allFinite(v0: Double?, v1: Double?, v2: Double?): Boolean {
        return allFinite(
            v0,
            v1
        ) && isFinite(v2)
    }

    fun allFinite(v0: Double?, v1: Double?, v2: Double?, v3: Double?): Boolean {
        return allFinite(
            v0,
            v1,
            v2
        ) && isFinite(v3)
    }

    fun filterFinite(l0: List<Double?>, l1: List<Double?>): List<List<Double>> {
        check(l0.size == l1.size)

        val l0Copy = ArrayList<Double>()
        val l1Copy = ArrayList<Double>()
        var copy = false
        for ((i, v0) in l0.withIndex()) {
            val v1 = l1[i]
            if (!allFinite(v0, v1)) {
                if (!copy) {
                    // copy already checked elements
                    @Suppress("UNCHECKED_CAST")
                    l0Copy.addAll(l0.take(i).toList() as List<Double>)
                    @Suppress("UNCHECKED_CAST")
                    l1Copy.addAll(l1.take(i).toList() as List<Double>)
                    copy = true
                }
                continue
            }

            if (copy) {
                l0Copy.add(v0 as Double)
                l1Copy.add(v1 as Double)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return when (copy) {
            true -> listOf(l0Copy, l1Copy)
            false -> listOf(l0 as List<Double>, l1 as List<Double>)
        }
    }

    fun range(values: Iterable<Double?>): DoubleSpan? {
        var min = 0.0
        var max = 0.0
        var inited = false
        for (v in values) {
            if (isFinite(v)) {
                if (inited) {
                    min = min(min, v!!)
                    max = max(max, v)
                } else {
                    max = v!!
                    min = max
                    inited = true
                }
            }
        }
        return if (inited)
            DoubleSpan(min, max)
        else
            null
    }

    fun resolution(values: Iterable<Double?>, naValue: Double): Double {

        // check if this is a row of a regular grid
        val rowDetector = RegularMeshDetector.tryRow(values)
        if (rowDetector.isMesh) {
            return rowDetector.resolution
        }

        // check if this is a column of a regular grid
        val columnDetector = RegularMeshDetector.tryColumn(values)
        return if (columnDetector.isMesh) {
            columnDetector.resolution
        } else {
            // use brut force method to find data resolution
            resolutionFullScan(values, naValue)
        }
    }

    private fun resolutionFullScan(values: Iterable<Double?>, naValue: Double): Double {
        @Suppress("UNCHECKED_CAST")
        val goodDataVector = values.filter(REAL_NUMBER) as Iterable<Double>
        if (Iterables.isEmpty(goodDataVector)) {
            return naValue
        }

        val copy = Ordering.natural<Double>().sortedCopy(goodDataVector)
        if (copy.size < 2) {
            return naValue
        }

        val it = copy.iterator()
        var resolution = naValue
        var allZero = true
        var prev = it.next()
        while (it.hasNext()) {
            val curr = it.next()
            val dist = curr - prev
            if (dist > 0 && (dist < resolution || allZero)) {
                allZero = false
                resolution = dist
            }

            prev = curr
        }

        return resolution
    }

    /**
     * Use with caution!
     *
     * Do not use this method on original data ranges (i.e. before transform).
     * The correct method for validation of original data ranges is 'Transforms.ensureApplicableDomain'.
     *
     * Can only be used on transformed data ranges or when "transform" is irrelevant.
     */
    fun ensureApplicableRange(
        range: DoubleSpan?,
        preferableNullRange: DoubleSpan? = null
    ): DoubleSpan {
        if (range == null) {
            return preferableNullRange ?: DoubleSpan(-0.5, 0.5)
        }
        if (isBeyondPrecision(range)) {
            val median = range.lowerEnd
            return DoubleSpan(median - 0.5, median + 0.5)
        }
        return range
    }

    fun span(range0: DoubleSpan?, range1: DoubleSpan?): DoubleSpan? {
        if (range0 == null) return range1
        return if (range1 == null) range0 else range0.union(range1)
    }

    fun isFinite(range: DoubleSpan): Boolean {
        return !(range.lowerEnd.isInfinite() || range.upperEnd.isInfinite())
    }

    fun <T> matchingIndices(list: List<T>, predicate: (T) -> Boolean): MutableList<Int> {
        val result = ArrayList<Int>()
        for (i in list.indices) {
            if (predicate(list[i])) {
                result.add(i)
            }
        }
        return result
    }

    fun matchingIndices(list: List<*>, matchedValue: Any?): MutableList<Int> {
        return matchingIndices(list) { matchedValue == it }
    }

    fun matchingIndices(list: List<*>, matchedValues: Set<*>): List<Int> {
        return matchingIndices(list) { it in matchedValues }
    }

    // ToDo: see Kotlin `slice()`
    fun <T> pickAtIndices(list: List<T>, indices: List<Int>): List<T> {
        val initialCapacity = if (indices.size > 10) indices.size else 10
        val result = ArrayList<T>(initialCapacity)
        for (index in indices) {
            if (index < list.size) {
                result.add(list[index])
            }
        }
        return result
    }

    // ToDo: see Kotlin `slice()`
    fun <T> pickAtIndices(list: List<T>, indices: Set<Int>): List<T> {
        val result = ArrayList<T>(list.size)
        for (i in list.indices) {
            if (indices.contains(i)) {
                result.add(list[i])
            }
        }
        return result
    }

    fun <T> skipAtIndices(list: List<T>, indices: Set<Int>): List<T> {
        val result = ArrayList<T>(list.size)
        for (i in list.indices) {
            if (!indices.contains(i)) {
                result.add(list[i])
            }
        }
        return result
    }

    fun <T> firstNotNull(list: List<T>, defaultValue: T): T {
        for (v in list) {
            if (v != null) {
                return v
            }
        }
        return defaultValue
    }

    fun mean(values: List<Double?>, defaultValue: Double?): Double? {
        fun notNanDoubles() = values
            .asSequence()
            .filterNotNull()
            .filterNot(Double::isNaN)

        val firstValue: Double = notNanDoubles().firstOrNull() ?: return defaultValue // no values left

        if (notNanDoubles().all { it == firstValue }) {
            return firstValue
        }

        // Other good algorithms:
        // https://mlblogblog.wordpress.com/2017/11/22/r2-the-best-algorithm-to-compute-the-online-mean/

        val result = notNanDoubles().foldIndexed(0.0) { i, mean, value ->
            value / (i + 1.0) + mean * (i / (i + 1.0))
        }

        return result.takeUnless { it.isNaN() } ?: defaultValue
    }

    fun sum(values: List<Double?>): Double {
        var result = 0.0
        for (value in values) {
            if (value != null && value.isFinite()) {
                result += value
            }
        }
        return result
    }

    fun toDoubleList(l: List<*>?): List<Double?>? {
        return if (l == null)
            null
        else
            CheckedDoubleList(l).cast()
    }

    class CheckedDoubleList(list: List<*>) : CheckedDoubleIterable(list) {

        override fun cast(): List<Double?> {
            return super.cast() as List<Double?>
        }
    }

    open class CheckedDoubleIterable(private val myIterable: Iterable<*>) {
        private val myEmpty: Boolean = Iterables.isEmpty(myIterable)
        private val myCanBeCast: Boolean

        init {
            myCanBeCast = if (myEmpty) {
                true
            } else {
                myIterable.filterNotNull().all { input -> input is Double }
            }
        }

        fun notEmptyAndCanBeCast(): Boolean {
            return !myEmpty && myCanBeCast
        }

        fun canBeCast(): Boolean {
            return myCanBeCast
        }

        open fun cast(): Iterable<Double?> {
            check(myCanBeCast) { "Can't cast to a collection of Double(s)" }
            // Safe cast: all values were checked
            @Suppress("UNCHECKED_CAST")
            return myIterable as Iterable<Double?>
        }
    }
}
