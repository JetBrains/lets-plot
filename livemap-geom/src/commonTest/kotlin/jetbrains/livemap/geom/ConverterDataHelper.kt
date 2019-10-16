package jetbrains.livemap.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.collection
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.gis.geoprotocol.TypedGeometry
import jetbrains.livemap.geom.MultiDataPointHelper.MultiDataPoint
import jetbrains.livemap.projections.Coordinates.Companion.ZERO_WORLD_POINT
import jetbrains.livemap.projections.ProjectionType
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.projections.ProjectionUtil.transformMultiPolygon
import jetbrains.livemap.projections.World


internal object ConverterDataHelper {
    val MAP_PROJECTION =
        createMapProjection(
            ProjectionType.MERCATOR,
            Rect(ZERO_WORLD_POINT, explicitVec(TILE_PIXEL_SIZE, TILE_PIXEL_SIZE))
        )

    val GENERIC_POINT = listOf(
        DoubleVector(0.0, 5.0)
    )

    val GENERIC_POINTS = listOf(
        DoubleVector(0.0, 5.0),
        DoubleVector(5.0, 5.0)
    )

    val PATH = MultiPolygon<LonLat>(
        listOf(
            Polygon(
                listOf(
                    Ring(
                        listOf(
                            explicitVec(0.0, 5.0),
                            explicitVec(1.0, 5.003032951),
                            explicitVec(2.0, 5.004549647),
                            explicitVec(3.0, 5.004549647),
                            explicitVec(4.0, 5.003032951),
                            explicitVec(5.0, 5.0)
                        )
                    )
                )

            )
        )

    )

    private val FIRST_RING = Ring<LonLat>(
        listOf(
            explicitVec(0.0, 5.0),
            explicitVec(0.0, 1.0),
            explicitVec(0.0, 5.0)
        )

    )

    private val SECOND_RING = Ring<LonLat>(
        listOf(
            explicitVec(5.0, 5.0),
            explicitVec(5.0, 1.0),
            explicitVec(5.0, 5.0)
        )
    )

    val MULTIPOLYGON = MultiPolygon(
        listOf(
            Polygon(
                listOf(
                    FIRST_RING, SECOND_RING
                )
            )
        )

    )

    fun rings(): List<Vec<LonLat>> {
        val rings = ArrayList<Vec<LonLat>>()
        rings.addAll(FIRST_RING)
        rings.addAll(SECOND_RING)

        return rings
    }

//    fun multipolygonToXYGeometry(multiPolygon: MultiPolygon<LonLat>): TypedGeometry<World> {
//        return Geometry.create(transformMultiPolygon(multiPolygon, MAP_PROJECTION::project))
//    }

    fun createDefaultMatcher(): MapObjectMatcher {
        return MapObjectMatcher()
    }

//    fun convertLonLatToXY(lonLat: Vec<LonLat>?): Vec<World>? {
//        return if (lonLat == null) null else MAP_PROJECTION.project(lonLat)
//    }

//    fun dataPointEq(value: Int): DataPointAesthetics {
//        return argThat(DataPointIndexMatcher(value))
//    }
//
//    fun multiDataPointEq(value: Int): MultiDataPoint {
//        return argThat(MultiDataPointIndexMatcher(value))
//    }

    private class DataPointIndexMatcher internal constructor(private val expected: Int) :
        ArgumentMatcher<DataPointAesthetics> {

        override fun matches(actual: DataPointAesthetics?): Boolean {
            return if (actual == null) {
                false
            } else actual.index() == expected

        }
    }

    private class MultiDataPointIndexMatcher internal constructor(private val expected: Int) :
        ArgumentMatcher<MultiDataPoint> {

        override fun matches(actual: MultiDataPoint?): Boolean {
            return if (actual == null) {
                false
            } else actual.aes.index() == expected

        }
    }

    interface ArgumentMatcher<T> {
        fun matches(actual: T?): Boolean
    }

    internal class AestheticsDataHelper private constructor() {

        private val myGroups = ArrayList<AesGroup>()
        private lateinit var myAes: Aesthetics
        private val myAesBuilder: AestheticsBuilder = AestheticsBuilder()

        private val nextGroupDataPointIndex: Int
            get() {
                var groupIndex = 0
                for (group in myGroups) {
                    groupIndex += group.size
                }

                return groupIndex
            }

        fun buildConverter(): DataPointsConverter {
            val aesthetics = build()
            return DataPointsConverter(aesthetics, MAP_PROJECTION, true)
        }

        private fun build(): Aesthetics {
            val x = ArrayList<Double>()
            val y = ArrayList<Double>()
            val groups = ArrayList<Int>()

            for (group in myGroups) {
                for (i in 0 until group.size) {
                    x.add(group.x[i])
                    y.add(group.y[i])
                    groups.add(group.index)
                }
            }

            myAes = builder()
                .group(collection(groups))
                .x(collection(x))
                .y(collection(y))
                .dataPointCount(x.size)
                .build()

            return myAes
        }

        private fun addGroup(x: List<Double>, y: List<Double>): AesGroup {
            val aesGroup = AesGroup(myGroups.size, nextGroupDataPointIndex, x, y)
            myGroups.add(aesGroup)
            return aesGroup
        }

        fun addGroup(points: List<DoubleVector>): AesGroup {
            val x = ArrayList<Double>()
            val y = ArrayList<Double>()

            for (point in points) {
                x.add(point.x)
                y.add(point.y)
            }

            return addGroup(x, y)
        }

        fun builder(): AestheticsBuilder {
            return myAesBuilder
        }

        companion object {
            fun create(): AestheticsDataHelper {
                return AestheticsDataHelper()
            }
        }
    }

    class AesGroup internal constructor(
        internal val index: Int,
        private val dataPointIndex: Int,
        internal val x: List<Double>,
        internal val y: List<Double>
    ) {

        internal val size: Int
            get() = x.size
    }
}
