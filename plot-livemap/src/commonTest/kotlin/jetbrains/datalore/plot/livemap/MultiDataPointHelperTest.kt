/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.list
import jetbrains.datalore.plot.livemap.DataPointsConverter.MultiDataPointHelper.*
import jetbrains.datalore.plot.livemap.DataPointsConverter.MultiDataPointHelper.SortingMode.BAR
import jetbrains.datalore.plot.livemap.DataPointsConverter.MultiDataPointHelper.SortingMode.PIE_CHART
import jetbrains.datalore.plot.livemap.MultiDataPointHelperTest.MultiDataBuilder.DataPointBuilder
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiDataPointHelperTest {

    private var myMultiDataBuilder = MultiDataBuilder()

    @Test
    @Ignore // ToDo Fix test
    fun whenSortingModePieChart_AndPointOrderSet_ShouldSortByOrder() {
        val dataPointBuilders = listOf(
            point().value(2.0).order(3.0),
            point().value(7.0).order(1.0),
            point().value(3.0).order(0.0),
            point().value(10.0).order(2.0)
        )

        assertPointsOrder(
            PIE_CHART, dataPointBuilders,
            POINT_2,
            POINT_1,
            POINT_3,
            POINT_0
        )
    }

    @Test
    @Ignore // ToDo Fix test
    fun whenSortingModePieChart_AndPointOrderNotSet_ShouldUseSpecialSortingByValue() {
        assertPointsOrder(
            PIE_CHART,
            pointsWithValue(2.0, 7.0, 3.0, 10.0),
            POINT_3,
            POINT_0,
            POINT_2,
            POINT_1
        )
    }

    @Test
    fun whenSortingModeBar_AndPointsAlreadySorted_ShouldNotChangeOrder() {
        assertPointsOrder(
            BAR,
            pointsWithOrder(0.0, 1.0, 2.0, 3.0),
            POINT_0,
            POINT_1,
            POINT_2,
            POINT_3
        )
    }

    @Test
    fun whenDataNotSorted_AndSortingModeBar_ShouldSortByOrder() {
        assertPointsOrder(
            BAR,
            pointsWithOrder(1.0, 3.0, 2.0, 0.0),
            POINT_3,
            POINT_0,
            POINT_2,
            POINT_1
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
            .multiData(explicitVec(0.0, 0.0), dataPointBuilders)
            .points

        assertPointsOrder(
            points[0],
            *expectedPointIndices
        )
    }

    internal class MultiDataBuilder {
        private val myBuilder = AestheticsBuilder()
        private val myPoints = ArrayList<DataPointBuilder>()
        private var mySortingMode = BAR

        val points: List<MultiDataPoint>
            get() = DataPointsConverter.MultiDataPointHelper.getPoints(build(), mySortingMode)

        private fun build(): Aesthetics {
            val values = ArrayList<Double>()
            val order = ArrayList<Double>()
            val coord = ArrayList<Vec<LonLat>>()
            for (pointBuilder in myPoints) {
                values.add(pointBuilder.myValue)
                order.add(pointBuilder.myOrder)
                coord.add(pointBuilder.myCoord)
            }

            myBuilder
                .x(list(coord.map(Vec<LonLat>::x)))
                .y(list(coord.map(Vec<LonLat>::y)))
                .symX(list(order))
                .symY(list(values))
                .dataPointCount(order.size)

            return myBuilder.build()
        }

        fun sortingMode(v: SortingMode): MultiDataBuilder {
            mySortingMode = v
            return this
        }

        fun multiData(coord: Vec<LonLat>, v: List<DataPointBuilder>): MultiDataBuilder {
            for (point in v) {
                point.coord(coord)
                myPoints.add(point)
            }
            return this
        }

        internal class DataPointBuilder {
            var myValue: Double = 0.0
            var myOrder: Double = 0.0
            lateinit var myCoord: Vec<LonLat>

            fun value(v: Double): DataPointBuilder {
                myValue = v
                return this
            }

            fun order(v: Double): DataPointBuilder {
                myOrder = v
                return this
            }

            fun coord(v: Vec<LonLat>): DataPointBuilder {
                myCoord = v
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