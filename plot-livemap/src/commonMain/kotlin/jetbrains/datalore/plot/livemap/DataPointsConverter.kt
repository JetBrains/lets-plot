/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.geom.LabelGeom
import jetbrains.datalore.plot.base.geom.PathGeom
import jetbrains.datalore.plot.base.geom.PieGeom
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
    private val layerIndex: Int,
    private val aesthetics: Aesthetics
) {
    private val pointFeatureConverter get() = PointFeatureConverter(aesthetics)
    private val mySinglePathFeatureConverter get() = SinglePathFeatureConverter(aesthetics)
    private val myMultiPathFeatureConverter get() = MultiPathFeatureConverter(aesthetics)

    data class PieOptions(
        val strokeColor: Color,
        val strokeWidth: Double,
        val holeSize: Double
    )
    private fun pieConverter(geom: Geom): List<DataPointLiveMapAesthetics> {
        val pieOptions = (geom as? PieGeom)?.let {
            PieOptions(it.strokeColor, it.strokeWidth, it.holeSize)
        }
        val fillWithColor = (geom as? PieGeom)?.fillWithColor ?: false
        val colorGetter: (DataPointAesthetics) -> Color =  { p: DataPointAesthetics ->
            if (fillWithColor) p.color()!! else p.fill()!!
        }

        return MultiDataPointHelper.getPoints(aesthetics, colorGetter)
            .map {
                DataPointLiveMapAesthetics(it, MapLayerKind.PIE)
                    .setGeometryPoint(explicitVec(it.aes.x()!!, it.aes.y()!!))
                    .setPieOptions(pieOptions)
            }
    }

    fun toPoint(geom: Geom) = pointFeatureConverter.point(geom)
    fun toHorizontalLine() = pointFeatureConverter.hLine()
    fun toVerticalLine() = pointFeatureConverter.vLine()
    fun toSegment(geom: Geom) = mySinglePathFeatureConverter.segment(geom)
    fun toRect() = myMultiPathFeatureConverter.rect()
    fun toTile() = mySinglePathFeatureConverter.tile()
    fun toPath(geom: Geom) = myMultiPathFeatureConverter.path(geom)
    fun toPolygon() = myMultiPathFeatureConverter.polygon()
    fun toText(geom: Geom) = pointFeatureConverter.text(geom)
    fun toPie(geom: Geom): List<DataPointLiveMapAesthetics> = pieConverter(geom)

    private abstract class PathFeatureConverterBase internal constructor(
        internal val aesthetics: Aesthetics
    ) {
        private var myArrowSpec: ArrowSpec? = null
        private var myAnimation: Int? = null
        private var myFlat: Boolean = false

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
                layerKind = when {
                    isClosed -> MapLayerKind.POLYGON
                    else -> MapLayerKind.PATH
                }
            )
                .setGeometryData(points, isClosed, flat = myFlat)
                .setArrowSpec(myArrowSpec)
                .setAnimation(myAnimation)


        internal fun setArrowSpec(arrowSpec: ArrowSpec?) {
            myArrowSpec = arrowSpec
        }

        internal fun setAnimation(animation: Any?) {
            myAnimation = parsePathAnimation(animation)
        }

        internal fun setFlat(flat: Boolean) {
            myFlat = flat
        }
    }

    private inner class MultiPathFeatureConverter(
        aes: Aesthetics
    ) : PathFeatureConverterBase(aes) {

        internal fun path(geom: Geom): List<DataPointLiveMapAesthetics> {
            setAnimation((geom as? PathGeom)?.animation)
            (geom as? PathGeom)?.flat?.let(::setFlat)

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
        aesthetics: Aesthetics
    ) : PathFeatureConverterBase(aesthetics) {
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
            (geom as? SegmentGeom)?.flat?.let(::setFlat)

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

    data class LabelOptions(
        val padding: Double,
        val radius: Double,
        val size: Double
    )

    private class PointFeatureConverter(
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

        private var myLabelOptions: LabelOptions? = null

        internal fun text(geom: Geom): List<DataPointLiveMapAesthetics> {
            if (geom is LabelGeom) {
                myLabelOptions = LabelOptions(
                    padding = geom.paddingFactor,
                    radius = geom.radiusFactor,
                    size = geom.borderWidth
                )
            }
            return process(MapLayerKind.TEXT) { explicitVec(it.x()!!, it.y()!!) }
        }

        private fun process(
            layerKind: MapLayerKind,
            dataPointToGeometry: (DataPointAesthetics) -> Vec<LonLat>?
        ): List<DataPointLiveMapAesthetics> {
            val mapObjects = ArrayList<DataPointLiveMapAesthetics>(myAesthetics.dataPointCount())
            for (p in myAesthetics.dataPoints()) {
                dataPointToGeometry(p)?.let { v ->
                    DataPointLiveMapAesthetics(p, layerKind)
                        .setGeometryPoint(v)
                        .setAnimation(myAnimation)
                        .setLabelOptions(myLabelOptions)
                }?.let(mapObjects::add)
            }

            return mapObjects
        }
    }

    private fun <T> List<DoubleVector>.toVecs(): List<Vec<T>> {
        return map { explicitVec<T>(it.x, it.y) }
    }

    internal class MultiDataPointHelper private constructor(
    ) {
        companion object {
            fun getPoints(aesthetics: Aesthetics, colorGetter: (DataPointAesthetics) -> Color): List<MultiDataPoint> {
                val builders = HashMap<Vec<LonLat>, MultiDataPointBuilder>()

                fun fetchBuilder(p: DataPointAesthetics): MultiDataPointBuilder {
                    val coord = explicitVec<LonLat>(p.x()!!, p.y()!!)
                    return builders.getOrPut(coord) { MultiDataPointBuilder(p, colorGetter) }
                }

                aesthetics.dataPoints()
                    .forEach { p -> fetchBuilder(p).add(p) }
                return builders.values.map(MultiDataPointBuilder::build)
            }
        }

        private class MultiDataPointBuilder(
            private val myAes: DataPointAesthetics,
            private val myColorGetter: (DataPointAesthetics) -> Color
        ) {
            private val myPoints = ArrayList<DataPointAesthetics>()

            fun add(p: DataPointAesthetics) {
                myPoints.add(p)
            }

            fun build(): MultiDataPoint {
                return MultiDataPoint(
                    aes = myAes,
                    indices = myPoints.map { it.index() },
                    values = myPoints.map { it.slice()!! },
                    colors = myPoints.map { myColorGetter(it) },
                    explodeValues = myPoints.map { it.explode()!! }
                )
            }
        }

        internal data class MultiDataPoint(
            val aes: DataPointAesthetics,
            val indices: List<Int>,
            val values: List<Double>,
            val colors: List<Color>,
            val explodeValues: List<Double>
        )
    }
}