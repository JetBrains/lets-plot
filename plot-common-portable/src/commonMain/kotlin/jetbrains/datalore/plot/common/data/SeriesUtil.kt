/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.gcommon.collect.Iterables.all
import jetbrains.datalore.base.gcommon.collect.Iterables.filter
import jetbrains.datalore.base.gcommon.collect.Ordering
import kotlin.math.max
import kotlin.math.min

object SeriesUtil {
    const val TINY = 1e-50

    private val REAL_NUMBER = { it: Double? -> isFinite(it) }

    val NEGATIVE_NUMBER = { input: Double -> input < 0 }

    fun isSubTiny(value: Double): Boolean {
        return value < TINY
    }

    fun isSubTiny(range: ClosedRange<Double>): Boolean {
        return isFinite(range) && span(range) < TINY
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

    fun isFinite(v: Double): Boolean {
        return v.isFinite()
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
        checkState(l0.size == l1.size)

        val l0Copy = ArrayList<Double>()
        val l1Copy = ArrayList<Double>()
        var copy: Boolean = false
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

    fun range(values: Iterable<Double?>): ClosedRange<Double>? {
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
            ClosedRange(min, max)
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
        val goodDataVector = filter(values, REAL_NUMBER) as Iterable<Double>
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

    fun ensureApplicableRange(range: ClosedRange<Double>?): ClosedRange<Double> {
        if (range == null) {
            return ClosedRange(-0.5, 0.5)
        }
        if (isSubTiny(range)) {
            val median = range.lowerEnd
            return ClosedRange(median - 0.5, median + 0.5)
        }
        return range
    }

    fun span(range: ClosedRange<Double>): Double {
        require(isFinite(range)) { "range must be finite: $range" }
        return range.upperEnd - range.lowerEnd
    }

    fun span(range0: ClosedRange<Double>?, range1: ClosedRange<Double>?): ClosedRange<Double>? {
        if (range0 == null) return range1
        return if (range1 == null) range0 else range0.span(range1)
    }

    fun expand(range: ClosedRange<Double>, newSpan: Double): ClosedRange<Double> {
        val expand = (newSpan - span(range)) / 2
        return expand(range, expand, expand)
    }

    fun expand(range: ClosedRange<Double>, lowerExpand: Double, upperExpand: Double): ClosedRange<Double> {
        return ClosedRange(range.lowerEnd - lowerExpand, range.upperEnd + upperExpand)
    }

    fun isFinite(range: ClosedRange<Double>): Boolean {
        return !(range.lowerEnd.isInfinite() || range.upperEnd.isInfinite())
    }

    fun matchingIndices(list: List<*>, matchedValue: Any?): MutableList<Int> {
        val result = ArrayList<Int>()
        for (i in list.indices) {
            if (matchedValue == list[i]) {
                result.add(i)
            }
        }
        return result
    }

    fun matchingIndices(list: List<*>, matchedValues: Set<*>): List<Int> {
        val result = ArrayList<Int>()
        for (i in list.indices) {
            if (matchedValues.contains(list[i])) {
                result.add(i)
            }
        }
        return result
    }

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
        var result = 0.0
        var i = -1.0
        for (value in values) {
            if (value != null && value.isFinite()) {
                i++
                result = value / (i + 1) + result * (i / (i + 1))
            }
        }
        return if (i >= 0) result else defaultValue
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
                all(filter(myIterable) { it != null }) { input -> input is Double }
            }
        }

        fun notEmptyAndCanBeCast(): Boolean {
            return !myEmpty && myCanBeCast
        }

        fun canBeCast(): Boolean {
            return myCanBeCast
        }

        open fun cast(): Iterable<Double?> {
            checkState(myCanBeCast, "Can't cast to collection of numbers")
            // Safe cast: all values were checked
            @Suppress("UNCHECKED_CAST")
            return myIterable as Iterable<Double?>
        }
    }
}
