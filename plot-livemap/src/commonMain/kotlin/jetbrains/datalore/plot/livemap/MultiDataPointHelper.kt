/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics

internal class MultiDataPointHelper private constructor(
) {

    companion object {
        fun getPoints(aesthetics: Aesthetics, sortingMode: SortingMode): List<MultiDataPoint> {
            val builders = HashMap<Vec<LonLat>, MultiDataPointBuilder>()

            fun fetchBuilder(p: DataPointAesthetics): MultiDataPointBuilder {
                val coord = explicitVec<LonLat>(p.x()!!, p.y()!!)
                return builders.getOrPut(coord) { MultiDataPointBuilder(p, sortingMode) }
            }

            aesthetics.dataPoints()
                .filter { it.symY() != null }
                .forEach { p -> fetchBuilder(p).add(p) }
            return builders.values.map(MultiDataPointBuilder::build)
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
            if (p.symX() != 0.0) {
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
                values = myPoints.map { it.symY()!! }, // symY can't be null - pre-filtered in function getPoints()
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
            private val BY_ORDER: (DataPointAesthetics) -> Double = { it.symX()!! }
            private val BY_VALUE: (DataPointAesthetics) -> Double = { it.symY()!! }
        }
    }

    internal data class MultiDataPoint(
        val aes: DataPointAesthetics,
        val indices: List<Int>,
        val values: List<Double>,
        val colors: List<Color>
    )
}
