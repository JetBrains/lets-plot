/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

import jetbrains.datalore.plot.common.data.SeriesUtil.isFinite
import kotlin.math.abs
import kotlin.math.min

abstract class RegularMeshDetector protected constructor(
    private val maxError: Double
) {

    open var isMesh: Boolean = false
        protected set

    var resolution: Double = 0.0
        get() {
            check(isMesh) { "Not a mesh" }
            return field
        }
        protected set

    protected fun equalsEnough(d1: Double, d2: Double): Boolean {
        return d1 == d2 || abs(d1 - d2) <= maxError
    }

    protected fun nearZero(d: Double): Boolean {
        return abs(d) <= maxError
    }


    private class MyRowDetector internal constructor(
        private val myMinRowSize: Int,
        error: Double,
        values: Iterable<Double?>
    ) : RegularMeshDetector(error) {

        init {
            init(values)
        }

        private fun init(values: Iterable<Double?>) {
            // check if first N elements are equally spaced
            isMesh = false
            var distance = 0.0
            var distanceInitialized = false
            var prevValue: Double? = null
            var count = myMinRowSize
            for (value in values) {
                if (!isFinite(value)) {
                    return
                }
                if (prevValue != null) {
                    val dist = value!! - prevValue
                    if (nearZero(dist)) {
                        return
                    }
                    if (distanceInitialized) {
                        if (!equalsEnough(dist, distance)) {
                            return
                        }
                    } else {
                        distance = dist
                        distanceInitialized = true
                    }
                }

                prevValue = value
                if (--count == 0) {
                    break
                }
            }

            if (distanceInitialized && count == 0) {
                resolution = abs(distance)
                isMesh = true
            }
        }
    }

    private class MyColumnDetector internal constructor(
        private val minColSize: Int,
        error: Double,
        values: Iterable<Double?>
    ) : RegularMeshDetector(error) {

        init {
            resOrNull(values)?.let {
                isMesh = true
                resolution = it
            }
        }

        private fun resOrNull(values: Iterable<Double?>): Double? {
            // check if serie can be split into sets of elements where:
            // 1. all sets are equal in size;
            // 2. all elements in each set are equal

            val rowValues = ArrayList<Double>()

            var firstColSize: Int = 0
            var currColSize: Int = 0
            var colIndex = 0
            var lastValue: Double = Double.NaN
            for (value in values) {
                if (value == null || !value.isFinite()) {
                    return null   // not a grid.
                }
                if (lastValue.isNaN()) {
                    // start 1st col
                    currColSize = 1
                    rowValues.add(value)
                } else if (equalsEnough(lastValue, value)) {
                    currColSize++
                } else {
                    // end of col
                    if (firstColSize == 0) {
                        if (currColSize < minColSize) {
                            return null // not a grid.
                        }
                        firstColSize = currColSize
                    }

                    // all equal size so far?
                    if (currColSize != firstColSize) {
                        return null // not a grid.
                    }

                    // start next col
                    colIndex++
                    currColSize = 1
                    rowValues.add(value)
                }

                lastValue = value
            }

            // at least 2 columns
            if (rowValues.size < 2) {
                return null // not a grid.
            }

            // check last col size
            if (currColSize != firstColSize) {
                return null // not a grid.
            }

            // This is columns serie in a grid - compute step is the row.
            rowValues.sort()
            var minDelta = rowValues[1] - rowValues[0]
            for (i in 1 until rowValues.size) {
                minDelta = min(minDelta, rowValues[i] - rowValues[i - 1])
            }

            return if (nearZero(minDelta)) {
                null  // not a grid.
            } else {
                minDelta
            }
        }
    }

    companion object {
        const val GRID_THRESHOLD = 50

        private val NO_MESH: RegularMeshDetector = object : RegularMeshDetector(0.0) {
            override var isMesh: Boolean
                get() = false
                set(value: Boolean) {
                    super.isMesh = value
                }
        }

        fun tryRow(values: Iterable<Double?>): RegularMeshDetector {
            // choose 'error' value
            val valuesIterator = values.iterator()
            val v0 = if (valuesIterator.hasNext()) valuesIterator.next() else null
            val v1 = if (valuesIterator.hasNext()) valuesIterator.next() else null
            if (v0 == null || v1 == null) {
                return NO_MESH
            }
            val delta = abs(v1 - v0)
            if (!delta.isFinite()) {
                return NO_MESH
            }
            val error = delta / 10000.0
            return tryRow(GRID_THRESHOLD, error, values)
        }

        fun tryRow(minRowSize: Int, error: Double, values: Iterable<Double?>): RegularMeshDetector {
            return MyRowDetector(minRowSize, error, values)
        }

        fun tryColumn(values: Iterable<Double?>): RegularMeshDetector {
            return tryColumn(
                GRID_THRESHOLD,
                SeriesUtil.TINY,
                values
            )
        }

        fun tryColumn(minRowSize: Int, error: Double, values: Iterable<Double?>): RegularMeshDetector {
            return MyColumnDetector(minRowSize, error, values)
        }
    }
}
