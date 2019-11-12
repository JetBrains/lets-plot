/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.plot.FeatureSwitch.isDebugLogEnabled
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.geom.PathGeom
import jetbrains.datalore.plot.base.geom.PointGeom
import jetbrains.datalore.plot.base.geom.SegmentGeom
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.MultiPointData
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.collector
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.createMultiPointDataByGroup
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.multiPointAppender
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.singlePointAppender
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.livemap.mapobjects.MapLayerKind
import kotlin.math.abs
import kotlin.math.min

internal class DataPointsConverter(
    aesthetics: Aesthetics,
    geodesic: Boolean
) {

    private val myPointFeatureConverter: PointFeatureConverter
    private val mySinglePathFeatureConverter: SinglePathFeatureConverter
    private val myMultiPathFeatureConverter: MultiPathFeatureConverter
    private fun log(p: DataPointAesthetics) {
        if (isDebugLogEnabled()) {
            println(
                "DataPointAesthetics:"
                        + "\n\tgroup: " + p.group()
                        + "\n\tmapId: " + p.mapId()
                        + "\n\tsize: " + p.size()
                        + "\n\tcolor: " + p.color()
                        + "\n\tfill: " + p.fill()
                        + "\n\tshape: " + p.shape()?.code
                        + "\n\tshape.strokeWidth: " + p.shape()?.strokeWidth(p)
                        + "\n\tshape.size: " + p.shape()?.size(p)
                        + "\n\tlineType: " + p.lineType()
                        + "\n\tlabel: " + p.label()
                        + "\n\tfamily: " + p.family()
                        + "\n\tfontface: " + p.fontface()
                        + "\n\thjus: " + p.hjust()
                        + "\n\tvjus: " + p.vjust()
                        + "\n\tangle: " + p.angle()
            )
        }
    }

    init {
        myPointFeatureConverter = PointFeatureConverter(aesthetics)
        mySinglePathFeatureConverter = SinglePathFeatureConverter(aesthetics, geodesic)
        myMultiPathFeatureConverter = MultiPathFeatureConverter(aesthetics, geodesic)
    }

    fun toPoint(geom: Geom): List<MapObjectBuilder> {
        return myPointFeatureConverter.point(geom)
    }

    fun toHorizontalLine(): List<MapObjectBuilder> {
        return myPointFeatureConverter.hLine()
    }

    fun toVerticalLine(): List<MapObjectBuilder> {
        return myPointFeatureConverter.vLine()
    }

    fun toSegment(geom: Geom): List<MapObjectBuilder> {
        return mySinglePathFeatureConverter.segment(geom)
    }

    fun toRect(): List<MapObjectBuilder> {
        return myMultiPathFeatureConverter.rect()
    }

    fun toTile(): List<MapObjectBuilder> {
        return mySinglePathFeatureConverter.tile()
    }

    fun toPath(geom: Geom): List<MapObjectBuilder> {
        return myMultiPathFeatureConverter.path(geom)
    }

    fun toPolygon(): List<MapObjectBuilder> {
        return myMultiPathFeatureConverter.polygon()
    }

    fun toText(): List<MapObjectBuilder> {
        return myPointFeatureConverter.text()
    }

    private abstract inner class PathFeatureConverterBase internal constructor(
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

        internal fun pathToBuilder(
            p: DataPointAesthetics,
            points: List<Vec<LonLat>>,
            isClosed: Boolean
        ): MapObjectBuilder {
            log(p)

            return MapObjectBuilder(p, getRender(isClosed))
                .setGeometryData(points, isClosed, myGeodesic)
                .setArrowSpec(myArrowSpec)
                .setAnimation(myAnimation)
        }

        private fun getRender(isPolygon: Boolean): MapLayerKind {
            return if (isPolygon) MapLayerKind.POLYGON else MapLayerKind.PATH
        }

        internal fun setArrowSpec(arrowSpec: ArrowSpec?) {
            myArrowSpec = arrowSpec
        }

        internal fun setAnimation(animation: Any?) {
            myAnimation = parsePathAnimation(animation)
        }
    }

    private inner class MultiPathFeatureConverter(aes: Aesthetics, geodesic: Boolean) :
        PathFeatureConverterBase(aes, geodesic) {

        internal fun path(geom: Geom): List<MapObjectBuilder> {
            setAnimation(if (geom is PathGeom) geom.animation else null)

            return process(multiPointDataByGroup(singlePointAppender(GeomUtil.TO_LOCATION_X_Y)), false)
        }

        internal fun polygon(): List<MapObjectBuilder> {
            return process(multiPointDataByGroup(singlePointAppender(GeomUtil.TO_LOCATION_X_Y)), true)
        }

        internal fun rect(): List<MapObjectBuilder> {
            return process(multiPointDataByGroup(multiPointAppender(GeomUtil.TO_RECTANGLE)), true)
        }

        private fun multiPointDataByGroup(coordinateAppender: (DataPointAesthetics, (DoubleVector?) -> Unit) -> Unit): List<MultiPointData> {
            return createMultiPointDataByGroup(aesthetics.dataPoints(), coordinateAppender, collector())
        }

        private fun process(
            multiPointDataList: List<MultiPointData>,
            isClosed: Boolean
        ): List<MapObjectBuilder> {
            val mapObjects = ArrayList<MapObjectBuilder>()

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

    private inner class SinglePathFeatureConverter(aesthetics: Aesthetics, geodesic: Boolean) :
        PathFeatureConverterBase(aesthetics, geodesic) {

        internal fun tile(): List<MapObjectBuilder> {
            return process(true, tileGeometryGenerator())
        }

        internal fun segment(geom: Geom): List<MapObjectBuilder> {
            setArrowSpec(if (geom is SegmentGeom) geom.arrowSpec else null)
            setAnimation(if (geom is SegmentGeom) geom.animation else null)

            return process(false, ::pointToSegmentGeometry)
        }

        private fun process(
            isClosed: Boolean,
            dataPointToGeometry: (DataPointAesthetics) -> List<DoubleVector>
        ): List<MapObjectBuilder> {
            val mapObjects = ArrayList<MapObjectBuilder>(aesthetics.dataPointCount())

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

        private fun tileGeometryGenerator(): (DataPointAesthetics) -> List<DoubleVector> {
            val d = getMinXYNonZeroDistance(aesthetics)

            return { p ->
                if (SeriesUtil.allFinite(p.x(), p.y(), p.width(), p.height())) {
                    val w = nonZero(p.width()!! * d.x, 0.1)
                    val h = nonZero(p.height()!! * d.y, 0.1)
                    GeomUtil.rectToGeometry(
                        p.x()!! - w / 2,
                        p.y()!! - h / 2,
                        p.x()!! + w / 2,
                        p.y()!! + h / 2
                    )
                } else {
                    emptyList()
                }
            }
        }

        private fun pointToSegmentGeometry(p: DataPointAesthetics): List<DoubleVector> {
            return if (SeriesUtil.allFinite(p.x(), p.y(), p.xend(), p.yend())) {
                listOf(
                    DoubleVector(p.x()!!, p.y()!!),
                    DoubleVector(p.xend()!!, p.yend()!!)
                )
            } else emptyList()
        }

        private fun nonZero(d: Double, defaultValue: Double): Double {
            return if (d != 0.0) d else defaultValue
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

            return if (minDistance == 0.0) {
                delta
            } else min(minDistance, delta)

        }
    }

    private inner class PointFeatureConverter(private val myAesthetics: Aesthetics) {
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

        internal fun point(geom: Geom): List<MapObjectBuilder> {
            myAnimation = parsePointAnimation(if (geom is PointGeom) geom.animation else null)

            return process(MapLayerKind.POINT, ::pointToVector)
        }

        internal fun hLine(): List<MapObjectBuilder> {
            return process(MapLayerKind.H_LINE, ::pointToHorizontalLine)
        }

        internal fun vLine(): List<MapObjectBuilder> {
            return process(MapLayerKind.V_LINE, ::pointToVerticalLine)
        }

        internal fun text(): List<MapObjectBuilder> {
            return process(MapLayerKind.TEXT, ::pointToVector)
        }

        private fun process(
            layerKind: MapLayerKind,
            dataPointToGeometry: (DataPointAesthetics) -> Vec<LonLat>?
        ): List<MapObjectBuilder> {
            val mapObjects = ArrayList<MapObjectBuilder>(myAesthetics.dataPointCount())
            for (p in myAesthetics.dataPoints()) {
                pointToBuilder(p, layerKind, dataPointToGeometry)?.let(mapObjects::add)
            }

            return mapObjects
        }

        private fun pointToBuilder(
            p: DataPointAesthetics,
            layerKind: MapLayerKind,
            dataPointToGeometry: (DataPointAesthetics) -> Vec<LonLat>?
        ): MapObjectBuilder? {
            log(p)

            return dataPointToGeometry(p)?.let { v ->
                MapObjectBuilder(p, layerKind)
                    .setGeometryPoint(v)
                    .setAnimation(myAnimation)
            }
        }

        private fun pointToVerticalLine(p: DataPointAesthetics): Vec<LonLat>? {
            return if (SeriesUtil.isFinite(p.interceptX())) {
                explicitVec(p.interceptX()!!, 0.0)
            } else null
        }

        private fun pointToVector(p: DataPointAesthetics): Vec<LonLat> {
            return explicitVec(p.x()!!, p.y()!!)
        }

        private fun pointToHorizontalLine(p: DataPointAesthetics): Vec<LonLat>? {
            return if (SeriesUtil.isFinite(p.interceptY())) {
                explicitVec(0.0, p.interceptY()!!)
            } else null
        }
    }
}

private fun <T> List<DoubleVector>.toVecs(): List<Vec<T>> {
    return map { explicitVec<T>(it.x, it.y)}
}
