package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics

internal class MultiDataPointHelper private constructor(
    private val myAesthetics: Aesthetics,
    private val mySortingMode: SortingMode
) {

    private val myMultiDataPointBuilders = HashMap<Any, MultiDataPointBuilder>()

    private val points: List<MultiDataPoint>
        get() {
            for (p in myAesthetics.dataPoints()) {
                createOrGetBuilderFor(p).add(p)
            }

            val result = ArrayList<MultiDataPoint>(myMultiDataPointBuilders.keys.size)

            for (multiDataPointBuilder in myMultiDataPointBuilders.values) {
                result.add(multiDataPointBuilder.build())
            }

            return result
        }

    internal enum class SortingMode {
        BAR,
        PIE_CHART
    }

    private fun createOrGetBuilderFor(p: DataPointAesthetics): MultiDataPointBuilder {
        return myMultiDataPointBuilders.getOrPut(p.mapId(), {
            MultiDataPointBuilder(
                p,
                mySortingMode
            )
        })
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
                myPoints.move(lastIndex(myPoints), 0)
            }
            val keyList = ArrayList<Int>()
            val valueList = ArrayList<Double>()
            val colorList = ArrayList<Color>()

            for (p in myPoints) {
                keyList.add(p.index())
                valueList.add(p.y()!!)
                colorList.add(p.fill()!!)
            }

            return MultiDataPoint(
                myAes,
                keyList,
                valueList,
                colorList
            )
        }

        private fun <T> MutableList<T>.sort(
            sorting: (T) -> Double
        ) {
            sortedBy(sorting).also {
                clear()
                addAll(it)
            }
        }

        private fun <T> MutableList<T>.move(from: Int, to: Int) {
            val p = this[from]

            val delta = if (to <= from) 0 else -1
            removeAt(from)
            add(to + delta, p)
        }

        private fun <T> lastIndex(list: List<T>): Int {
            return list.size - 1
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
    ) {

//        init {
//            if (isDebugLogEnabled()) {
//                debugLog(
//                    StringBuilder()
//                        .append("MultiDataPoint: myIndex=").append(myAes.index())
//                        .append(" myMapId=").append(myAes.mapId())
//                        .toString()
//                )
//            }
//        }
    }

    companion object {

        fun getPoints(aesthetics: Aesthetics, sortingMode: SortingMode): List<MultiDataPoint> {
            return MultiDataPointHelper(aesthetics, sortingMode).points
        }
    }
}