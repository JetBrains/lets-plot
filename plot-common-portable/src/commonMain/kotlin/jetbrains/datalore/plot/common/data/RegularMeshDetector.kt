/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

import jetbrains.datalore.plot.common.data.SeriesUtil.isFinite
import kotlin.math.abs

abstract class RegularMeshDetector protected constructor(private val myError: Double) {
    open var isMesh: Boolean = false
        protected set
    var resolution: Double = 0.toDouble()
        get() {
            check(isMesh) { "Not a mesh" }
            return field
        }
        protected set

    protected fun equalsEnough(d1: Double, d2: Double): Boolean {
        return d1 == d2 || abs(d1 - d2) <= myError
    }

    protected fun nearZero(d: Double): Boolean {
        return abs(d) <= myError
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
        private val myMinRowSize: Int,
        error: Double,
        values: Iterable<Double?>
    ) : RegularMeshDetector(error) {

        init {
            init(values)
        }

        private fun init(values: Iterable<Double?>) {
            // check if there are at least 2 sets of elements where:
            // 1. sets are equal in size;
            // 2. all elements in each set are equal
            isMesh = false
            val rowSize = intArrayOf(0, 0)
            val rowValue = arrayOf<Double?>(null, null)
            var rowIndex = 0
            for (value in values) {
                if (!isFinite(value)) {
                    break
                }
                if (rowValue[rowIndex] == null) {
                    rowValue[rowIndex] = value
                    rowSize[rowIndex]++
                } else if (equalsEnough(rowValue[rowIndex]!!, value!!)) {
                    rowSize[rowIndex]++
                } else {
                    if (rowIndex == 0) {
                        rowIndex++ // next row
                        rowValue[rowIndex] = value
                        rowSize[rowIndex]++
                    } else {
                        break
                    }
                }
            }

            // check results
            if (rowSize[0] == rowSize[1] && rowSize[0] >= myMinRowSize) {
                isMesh = true
                resolution = abs(rowValue[1]!! - rowValue[0]!!)
            }
        }
    }

    companion object {
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
            return tryRow(50, error, values)
        }

        fun tryRow(minRowSize: Int, error: Double, values: Iterable<Double?>): RegularMeshDetector {
            return MyRowDetector(minRowSize, error, values)
        }

        fun tryColumn(values: Iterable<Double?>): RegularMeshDetector {
            return tryColumn(
                50,
                SeriesUtil.TINY,
                values
            )
        }

        fun tryColumn(minRowSize: Int, error: Double, values: Iterable<Double?>): RegularMeshDetector {
            return MyColumnDetector(minRowSize, error, values)
        }
    }
}
