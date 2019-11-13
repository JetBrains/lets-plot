/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.collection
import jetbrains.livemap.projections.Coordinates.ZERO_WORLD_POINT
import jetbrains.livemap.projections.ProjectionType
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection


internal object ConverterDataHelper {
    val MAP_PROJECTION =
        createMapProjection(
            ProjectionType.MERCATOR,
            Rect(ZERO_WORLD_POINT, explicitVec(TILE_PIXEL_SIZE, TILE_PIXEL_SIZE))
        )

    val GENERIC_POINTS: List<Vec<LonLat>> = listOf(
        explicitVec(0.0, 5.0),
        explicitVec(5.0, 5.0)
    )

    val PATH = multiPolygon<LonLat>(
        polygon(
            ring(
                explicitVec(0.0, 5.0),
                explicitVec(1.0, 5.003032951),
                explicitVec(2.0, 5.004549647),
                explicitVec(3.0, 5.004549647),
                explicitVec(4.0, 5.003032951),
                explicitVec(5.0, 5.0)
            )
        )
    )

    val FIRST_RING = Ring<LonLat>(
        listOf(
            explicitVec(0.0, 5.0),
            explicitVec(0.0, 1.0),
            explicitVec(0.0, 5.0)
        )
    )

    val SECOND_RING = ring<LonLat>(
        explicitVec(5.0, 5.0),
        explicitVec(5.0, 1.0),
        explicitVec(5.0, 5.0)
    )

    val MULTIPOLYGON = multiPolygon(
        polygon(
            FIRST_RING,
            SECOND_RING
        )
    )

    fun <T> multiPolygon(vararg polygons: Polygon<T>): MultiPolygon<T> {
        return MultiPolygon(polygons.asList())
    }

    fun <T> polygon(vararg rings: Ring<T>): Polygon<T> {
        return Polygon(rings.asList())
    }

    private fun <T> ring(vararg points: Vec<T>): Ring<T> {
        return Ring(points.asList())
    }

    fun rings(): List<Vec<LonLat>> {
        val rings = ArrayList<Vec<LonLat>>()
        rings.addAll(FIRST_RING)
        rings.addAll(SECOND_RING)

        return rings
    }

    fun createDefaultMatcher(): MapObjectMatcher {
        return MapObjectMatcher()
    }

    internal class AestheticsDataHelper private constructor() {

        private val myGroups = ArrayList<AesGroup>()
        private lateinit var myAes: Aesthetics
        private val myAesBuilder: AestheticsBuilder = AestheticsBuilder()

        fun buildConverter(): DataPointsConverter {
            val aesthetics = build()
            return DataPointsConverter(
                aesthetics,
                true
            )
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
            val aesGroup = AesGroup(myGroups.size, x, y)
            myGroups.add(aesGroup)
            return aesGroup
        }

        fun addGroup(points: List<Vec<LonLat>>): AesGroup {
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
        internal val x: List<Double>,
        internal val y: List<Double>
    ) {

        internal val size: Int
            get() = x.size
    }
}
