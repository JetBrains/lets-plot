package jetbrains.livemap.geom

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.collection
import jetbrains.livemap.geom.MultiDataPointHelper.MultiDataPoint
import jetbrains.livemap.geom.MultiDataPointHelper.SortingMode
import jetbrains.livemap.geom.MultiDataPointHelper.SortingMode.BAR
import jetbrains.livemap.geom.MultiDataPointHelper.SortingMode.PIE_CHART
import jetbrains.livemap.geom.MultiDataPointHelperTest.MultiDataBuilder.DataPointBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiDataPointHelperTest {

    private var myMultiDataBuilder = MultiDataBuilder()

    @Test
    fun whenSortingModePieChart_AndPointOrderSet_ShouldSortByOrder() {
        val dataPointBuilders = listOf(
            point().value(2.0).order(3.0),
            point().value(7.0).order(1.0),
            point().value(3.0).order(0.0),
            point().value(10.0).order(2.0)
        )

        assertPointsOrder(PIE_CHART, dataPointBuilders, POINT_2, POINT_1, POINT_3, POINT_0)
    }

    @Test
    fun whenSortingModePieChart_AndPointOrderNotSet_ShouldUseSpecialSortingByValue() {
        assertPointsOrder(
            PIE_CHART,
            pointsWithValue(2.0, 7.0, 3.0, 10.0),
            POINT_3, POINT_0, POINT_2, POINT_1
        )
    }

    @Test
    fun whenSortingModeBar_AndPointsAlreadySorted_ShouldNotChangeOrder() {
        assertPointsOrder(
            BAR,
            pointsWithOrder(0.0, 1.0, 2.0, 3.0),
            POINT_0, POINT_1, POINT_2, POINT_3
        )
    }

    @Test
    fun whenDataNotSorted_AndSortingModeBar_ShouldSortByOrder() {
        assertPointsOrder(
            BAR,
            pointsWithOrder(1.0, 3.0, 2.0, 0.0),
            POINT_3, POINT_0, POINT_2, POINT_1
        )
    }

    private fun pointsWithOrder(vararg orders: Double): List<DataPointBuilder> {
        val dataPointBuilders = ArrayList<DataPointBuilder>()
        for (order in orders) {
            dataPointBuilders.add(point().order(order))
        }

        return dataPointBuilders
    }

    private fun pointsWithValue(vararg values: Double): List<DataPointBuilder> {
        val dataPointBuilders = ArrayList<DataPointBuilder>()
        for (value in values) {
            dataPointBuilders.add(point().value(value))
        }

        return dataPointBuilders
    }

    private fun assertPointsOrder(
        sortingMode: SortingMode,
        dataPointBuilders: List<DataPointBuilder>,
        vararg expectedPointIndices: Int
    ) {
        val points = myMultiDataBuilder
            .sortingMode(sortingMode)
            .multiData("TX", dataPointBuilders)
            .points

        assertPointsOrder(points[0], *expectedPointIndices)
    }

    internal class MultiDataBuilder {
        private val myBuilder = AestheticsBuilder()
        private val myPoints = ArrayList<DataPointBuilder>()
        private var mySortingMode = BAR

        val points: List<MultiDataPoint>
            get() = MultiDataPointHelper.getPoints(build(), mySortingMode)

        private fun build(): Aesthetics {
            val values = ArrayList<Double>()
            val order = ArrayList<Double>()
            val mapId = ArrayList<Any>()
            for (pointBuilder in myPoints) {
                values.add(pointBuilder.myValue)
                order.add(pointBuilder.myOrder)
                mapId.add(pointBuilder.myMapId)
            }

            myBuilder
                .mapId(collection(mapId))
                .x(collection(order))
                .y(collection(values))
                .dataPointCount(order.size)

            return myBuilder.build()
        }

        fun sortingMode(v: SortingMode): MultiDataBuilder {
            mySortingMode = v
            return this
        }

        fun multiData(mapId: Any, v: List<DataPointBuilder>): MultiDataBuilder {
            for (point in v) {
                point.mapId(mapId)
                myPoints.add(point)
            }
            return this
        }

        internal class DataPointBuilder {
            var myValue: Double = 0.0
            var myOrder: Double = 0.0
            lateinit var myMapId: Any

            fun value(v: Double): DataPointBuilder {
                myValue = v
                return this
            }

            fun order(v: Double): DataPointBuilder {
                myOrder = v
                return this
            }

            fun mapId(v: Any): DataPointBuilder {
                myMapId = v
                return this
            }
        }
    }

    companion object {

        private const val POINT_0 = 0
        private const val POINT_1 = 1
        private const val POINT_2 = 2
        private const val POINT_3 = 3

        private fun assertPointsOrder(point: MultiDataPoint, vararg expectedPointIndices: Int) {
            for (i in expectedPointIndices.indices) {
                assertEquals(expectedPointIndices[i], point.indices[i])
            }
        }

        private fun point(): DataPointBuilder {
            return DataPointBuilder()
        }
    }

}