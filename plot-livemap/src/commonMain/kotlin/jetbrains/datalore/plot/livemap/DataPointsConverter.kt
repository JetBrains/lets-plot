/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.geom.PathGeom
import jetbrains.datalore.plot.base.geom.PointGeom
import jetbrains.datalore.plot.base.geom.SegmentGeom
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.GeomUtil.TO_LOCATION_X_Y
import jetbrains.datalore.plot.base.geom.util.GeomUtil.TO_RECTANGLE
import jetbrains.datalore.plot.base.geom.util.MultiPointData
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.createMultiPointDataByGroup
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.multiPointAppender
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.singlePointAppender
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.abs
import kotlin.math.min

internal class DataPointsConverter(
    layerIndex: Int,
    aesthetics: Aesthetics,
    geodesic: Boolean
) {
    private val myPointFeatureConverter = PointFeatureConverter(layerIndex, aesthetics)
    private val mySinglePathFeatureConverter = SinglePathFeatureConverter(layerIndex, aesthetics, geodesic)
    private val myMultiPathFeatureConverter = MultiPathFeatureConverter(layerIndex, aesthetics, geodesic)

    fun toPoint(geom: Geom) = myPointFeatureConverter.point(geom)
    fun toHorizontalLine() = myPointFeatureConverter.hLine()
    fun toVerticalLine() = myPointFeatureConverter.vLine()
    fun toSegment(geom: Geom) = mySinglePathFeatureConverter.segment(geom)
    fun toRect() = myMultiPathFeatureConverter.rect()
    fun toTile() = mySinglePathFeatureConverter.tile()
    fun toPath(geom: Geom) = myMultiPathFeatureConverter.path(geom)
    fun toPolygon() = myMultiPathFeatureConverter.polygon()
    fun toText() = myPointFeatureConverter.text()

    private abstract class PathFeatureConverterBase internal constructor(
        private val layerIndex: Int,
        internal val aesthetics: Aesthetics,
        private val myGeodesic: Boolean
    ) {
        private var myArrowSpec: ArrowSpec? = null
        private var myAnimation: Int? = null
        private fun parsePathAnimation(animation: Any?): Int? {
            when (animation) {
                null -> return null
                is Number -> return animation.toInt()
                is String -> when (animation) {
                    "dash" -> return 1
                    "plane" -> return 2
                    "circle" -> return 3
                }
            }
            throw IllegalArgumentException("Unknown path animation: '$animation'")
        }

        internal fun pathToBuilder(p: DataPointAesthetics, points: List<Vec<LonLat>>, isClosed: Boolean) =
            DataPointLiveMapAesthetics(
                p = p,
                layerIndex = layerIndex,
                layerKind = when {
                    isClosed -> MapLayerKind.POLYGON
                    else -> MapLayerKind.PATH
                }
            )
                .setGeometryData(points, isClosed, myGeodesic)
                .setArrowSpec(myArrowSpec)
                .setAnimation(myAnimation)


        internal fun setArrowSpec(arrowSpec: ArrowSpec?) {
            myArrowSpec = arrowSpec
        }

        internal fun setAnimation(animation: Any?) {
            myAnimation = parsePathAnimation(animation)
        }
    }

    private inner class MultiPathFeatureConverter(
        val layerIndex: Int,
        aes: Aesthetics,
        geodesic: Boolean
    ) : PathFeatureConverterBase(layerIndex, aes, geodesic) {

        internal fun path(geom: Geom): List<DataPointLiveMapAesthetics> {
            setAnimation((geom as? PathGeom)?.animation)

            return process(multiPointDataByGroup(singlePointAppender(TO_LOCATION_X_Y)), false)
        }

        internal fun polygon(): List<DataPointLiveMapAesthetics> {
            return process(multiPointDataByGroup(singlePointAppender(TO_LOCATION_X_Y)), true)
        }

        internal fun rect(): List<DataPointLiveMapAesthetics> {
            return process(multiPointDataByGroup(multiPointAppender(TO_RECTANGLE)), true)
        }

        private fun multiPointDataByGroup(coordinateAppender: (DataPointAesthetics, (DoubleVector?) -> Unit) -> Unit): List<MultiPointData> {
            return createMultiPointDataByGroup(
                aesthetics.dataPoints(),
                coordinateAppender,
                MultiPointDataConstructor.collector()
            )
        }

        private fun process(multiPointDataList: List<MultiPointData>, isClosed: Boolean): List<DataPointLiveMapAesthetics> {
            val mapObjects = ArrayList<DataPointLiveMapAesthetics>()

            for (multiPointData in multiPointDataList) {
                pathToBuilder(
                    multiPointData.aes,
                    multiPointData.points.toVecs(),
                    isClosed
                ).let(mapObjects::add)
            }
            return mapObjects
        }
    }

    private inner class SinglePathFeatureConverter(
        val layerIndex: Int,
        aesthetics: Aesthetics,
        geodesic: Boolean
    ) : PathFeatureConverterBase(layerIndex, aesthetics, geodesic) {
        internal fun tile(): List<DataPointLiveMapAesthetics> {
            val d = getMinXYNonZeroDistance(aesthetics)
            return process(isClosed = true, dataPointToGeometry = { p ->
                if (SeriesUtil.allFinite(p.x(), p.y(), p.width(), p.height())) {
                    val w = nonZero(p.width()!! * d.x, 1.0)
                    val h = nonZero(p.height()!! * d.y, 1.0)
                    GeomUtil.rectToGeometry(
                        p.x()!! - w / 2,
                        p.y()!! - h / 2,
                        p.x()!! + w / 2,
                        p.y()!! + h / 2
                    )
                } else {
                    emptyList()
                }
            })
        }

        internal fun segment(geom: Geom): List<DataPointLiveMapAesthetics> {
            setArrowSpec((geom as? SegmentGeom)?.arrowSpec)
            setAnimation((geom as? SegmentGeom)?.animation)

            return process(isClosed = false) {
                if (SeriesUtil.allFinite(it.x(), it.y(), it.xend(), it.yend())) {
                    listOf(
                        DoubleVector(it.x()!!, it.y()!!),
                        DoubleVector(it.xend()!!, it.yend()!!)
                    )
                } else {
                    emptyList()
                }
            }
        }

        private fun process(
            isClosed: Boolean,
            dataPointToGeometry: (DataPointAesthetics) -> List<DoubleVector>
        ): List<DataPointLiveMapAesthetics> {
            val mapObjects = ArrayList<DataPointLiveMapAesthetics>(aesthetics.dataPointCount())

            for (p in aesthetics.dataPoints()) {
                val points = dataPointToGeometry(p)
                if (points.isEmpty()) {
                    continue
                }
                pathToBuilder(p, points.toVecs(), isClosed).let(mapObjects::add)
            }
            mapObjects.trimToSize()
            return mapObjects
        }

        private fun nonZero(d: Double, defaultValue: Double): Double = when (d) {
            0.0 -> defaultValue
            else -> d
        }

        private fun getMinXYNonZeroDistance(aesthetics: Aesthetics): DoubleVector {
            val dataPoints = aesthetics.dataPoints().toList()

            if (dataPoints.size < 2) {
                return DoubleVector.ZERO
            }

            var minDx = 0.0
            var minDy = 0.0

            var i = 0
            val n = dataPoints.size - 1
            while (i < n) {
                var j = i + 1
                val k = dataPoints.size
                while (j < k) {
                    val p1 = dataPoints[i]
                    val p2 = dataPoints[j]

                    minDx = minNonZeroDistance(p1.x()!!, p2.x()!!, minDx)
                    minDy = minNonZeroDistance(p1.y()!!, p2.y()!!, minDy)
                    ++j
                }
                ++i
            }
            return DoubleVector(minDx, minDy)
        }

        private fun minNonZeroDistance(p1: Double, p2: Double, minDistance: Double): Double {
            val delta = abs(p1 - p2)
            if (delta == 0.0) {
                return minDistance
            }

            return when (minDistance) {
                0.0 -> delta
                else -> min(minDistance, delta)
            }
        }
    }

    private class PointFeatureConverter(
        val layerIndex: Int,
        private val myAesthetics: Aesthetics
    ) {
        private var myAnimation: Int? = null
        private fun parsePointAnimation(animation: Any?): Int? {
            when (animation) {
                null -> return null
                is Number -> return animation.toInt()
                is String -> when (animation) {
                    "ripple" -> return 1
                }
            }
            throw IllegalArgumentException("Unknown point animation: '$animation'")
        }

        internal fun point(geom: Geom): List<DataPointLiveMapAesthetics> {
            myAnimation = parsePointAnimation((geom as? PointGeom)?.animation)

            return process(MapLayerKind.POINT) { explicitVec(it.x()!!, it.y()!!) }
        }

        internal fun hLine(): List<DataPointLiveMapAesthetics> {
            return process(MapLayerKind.H_LINE) {
                if (SeriesUtil.isFinite(it.interceptY())) {
                    explicitVec(0.0, it.interceptY()!!)
                } else {
                    null
                }
            }
        }

        internal fun vLine(): List<DataPointLiveMapAesthetics> {
            return process(MapLayerKind.V_LINE) {
                if (SeriesUtil.isFinite(it.interceptX())) {
                    explicitVec(it.interceptX()!!, 0.0)
                } else {
                    null
                }
            }
        }

        internal fun text(): List<DataPointLiveMapAesthetics> {
            return process(MapLayerKind.TEXT) { explicitVec(it.x()!!, it.y()!!) }
        }

        private fun process(
            layerKind: MapLayerKind,
            dataPointToGeometry: (DataPointAesthetics) -> Vec<LonLat>?
        ): List<DataPointLiveMapAesthetics> {
            val mapObjects = ArrayList<DataPointLiveMapAesthetics>(myAesthetics.dataPointCount())
            for (p in myAesthetics.dataPoints()) {
                dataPointToGeometry(p)?.let { v ->
                    DataPointLiveMapAesthetics(p, layerIndex, layerKind)
                        .setGeometryPoint(v)
                        .setAnimation(myAnimation)
                }?.let(mapObjects::add)
            }

            return mapObjects
        }
    }

    private fun <T> List<DoubleVector>.toVecs(): List<Vec<T>> {
        return map { explicitVec<T>(it.x, it.y) }
    }
}