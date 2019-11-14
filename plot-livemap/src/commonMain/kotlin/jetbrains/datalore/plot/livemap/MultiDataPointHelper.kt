/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics

internal class MultiDataPointHelper private constructor(
) {

    companion object {
        fun getPoints(aesthetics: Aesthetics, sortingMode: SortingMode): List<MultiDataPoint> {
            val builders = HashMap<Any, MultiDataPointBuilder>()

            fun fetchBuilder(p: DataPointAesthetics): MultiDataPointBuilder =
                builders.getOrPut(p.mapId(), { MultiDataPointBuilder(p, sortingMode) })

            aesthetics.dataPoints().forEach { fetchBuilder(it).add(it) }
            return builders.values.map { it.build() }
        }
    }

    internal enum class SortingMode {
        BAR,
        PIE_CHART
    }

    private class MultiDataPointBuilder(
        private val myAes: DataPointAesthetics,
        private val mySortingMode: SortingMode
    ) {
        private val myPoints = ArrayList<DataPointAesthetics>()
        private var myUsesOrder: Boolean = false

        internal fun add(p: DataPointAesthetics) {
            if (p.x() != 0.0) {
                myUsesOrder = true
            }

            myPoints.add(p)
        }

        internal fun build(): MultiDataPoint {
            myPoints.sort(if (myUsesOrder) BY_ORDER else BY_VALUE)

            if (mySortingMode == SortingMode.PIE_CHART && !myUsesOrder) {
                myPoints.move(myPoints.lastIndex, 0)
            }

            return MultiDataPoint(
                aes = myAes,
                indices = myPoints.map { it.index() },
                values = myPoints.map { it.y()!! },
                colors = myPoints.map { it.fill()!! }
            )
        }

        private fun <T> MutableList<T>.sort(sorting: (T) -> Double) {
            sortedBy(sorting).also { clear(); addAll(it) }
        }

        private fun <T> MutableList<T>.move(from: Int, to: Int) {
            val p = this[from]

            val delta = if (to <= from) 0 else -1
            removeAt(from)
            add(to + delta, p)
        }

        companion object {
            private val BY_ORDER: (DataPointAesthetics) -> Double = { it.x()!! }
            private val BY_VALUE: (DataPointAesthetics) -> Double = { it.y()!! }
        }
    }

    internal data class MultiDataPoint(
        val aes: DataPointAesthetics,
        val indices: List<Int>,
        val values: List<Double>,
        val colors: List<Color>
    )

}