/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.livemap

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.Geom
import org.jetbrains.letsPlot.core.plot.base.geom.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_Y
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_RECTANGLE
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.createPathGroups
import org.jetbrains.letsPlot.core.plot.builder.scale.DefaultNaValue
import kotlin.math.abs
import kotlin.math.min

internal class DataPointsConverter(
    private val layerIndex: Int,
    private val aesthetics: Aesthetics
) {

    companion object {
        private fun <T> List<DoubleVector>.toVecs(): List<Vec<T>> = map { explicitVec(it.x, it.y) }
    }

    private val pointFeatureConverter get() = PointFeatureConverter(aesthetics)
    private val mySinglePathFeatureConverter get() = SinglePathFeatureConverter(aesthetics)
    private val myMultiPathFeatureConverter get() = MultiPathFeatureConverter(aesthetics)

    data class PieOptions(
        val spacerColor: Color?,
        val spacerWidth: Double,
        val holeSize: Double,
        val strokeSide: PieGeom.StrokeSide
    )

    private fun pieConverter(geom: PieGeom): List<DataPointLiveMapAesthetics> {
        val pieOptions = PieOptions(geom.spacerColor, geom.spacerWidth, geom.holeSize, geom.strokeSide)
        val definedDataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.SLICE)
        return MultiDataPointHelper.getPoints(definedDataPoints)
            .map {
                DataPointLiveMapAesthetics(it, MapLayerKind.PIE).apply {
                    point = Vec(it.aes.x()!!, it.aes.y()!!)
                    setPieOptions(pieOptions)
                }
            }
    }

    fun toPoint(geom: PointGeom) = pointFeatureConverter.point(geom)
    fun toHorizontalLine() = pointFeatureConverter.hLine()
    fun toVerticalLine() = pointFeatureConverter.vLine()
    fun toSegment(geom: SegmentGeom) = mySinglePathFeatureConverter.segment(geom)
    fun toRect() = myMultiPathFeatureConverter.rect()
    fun toTile() = mySinglePathFeatureConverter.tile()
    fun toPath(geom: Geom) = myMultiPathFeatureConverter.path(geom)
    fun toPolygon() = myMultiPathFeatureConverter.polygon()
    fun toText(geom: Geom) = pointFeatureConverter.text(geom)
    fun toPie(geom: PieGeom) = pieConverter(geom)
    fun toCurve(geom: CurveGeom) = mySinglePathFeatureConverter.curve(geom)
    fun toSpoke(geom: SpokeGeom) = mySinglePathFeatureConverter.spoke(geom)

    private abstract class PathFeatureConverterBase(
        val aesthetics: Aesthetics
    ) {
        private var myArrowSpec: ArrowSpec? = null
        private var myAnimation: Int? = null
        private var myFlat: Boolean = false
        private var myGeodesic: Boolean = false
        private var mySpacer: Double = 0.0
        private var myIsCurve: Boolean = false

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

        fun pathToBuilder(p: DataPointAesthetics, points: List<Vec<LonLat>>, isClosed: Boolean) =
            DataPointLiveMapAesthetics(
                p = p,
                layerKind = when (isClosed) {
                    true -> MapLayerKind.POLYGON
                    false -> MapLayerKind.PATH
                }
            ).apply {
                this.geometry = points
                this.flat = myFlat
                this.geodesic = myGeodesic
                this.spacer = mySpacer
                this.isCurve = myIsCurve
                setArrowSpec(myArrowSpec)
                setAnimation(myAnimation)
            }

        fun setArrowSpec(arrowSpec: ArrowSpec?) {
            myArrowSpec = arrowSpec
        }

        fun setAnimation(animation: Any?) {
            myAnimation = parsePathAnimation(animation)
        }

        fun setFlat(flat: Boolean) {
            myFlat = flat
        }

        fun setGeodesic(geodesic: Boolean) {
            myGeodesic = geodesic
        }

        fun setSpacer(spacer: Double) {
            mySpacer = spacer
        }

        fun setIsCurve(isCurve: Boolean) {
            myIsCurve = isCurve
        }
    }

    private inner class MultiPathFeatureConverter(
        aes: Aesthetics
    ) : PathFeatureConverterBase(aes) {

        fun path(geom: Geom): List<DataPointLiveMapAesthetics> {
            if (geom is PathGeom) {
                setAnimation(geom.animation)
                setFlat(geom.flat)
                setGeodesic(geom.geodesic)
            }

            val pathData = createPathGroups(aesthetics.dataPoints(), TO_LOCATION_X_Y, sorted = true)
            val variadicPathData = pathData.mapValues { (_, pathData) -> LinesHelper.splitByStyle(pathData) }
            val interpolatedPathData = LinesHelper.interpolatePathData(variadicPathData)

            return process(paths = interpolatedPathData.values.flatten(), isClosed = false)
        }

        fun polygon(): List<DataPointLiveMapAesthetics> {
            val paths = createPathGroups(aesthetics.dataPoints(), TO_LOCATION_X_Y, sorted = true)
            return process(paths = paths.values, isClosed = true)
        }

        fun rect(): List<DataPointLiveMapAesthetics> {
            val groupedData = GeomUtil.createGroups(aesthetics.dataPoints(), sorted = true)
            val rectangles = groupedData.map { (_, groupData) ->
                groupData.flatMap { aes -> TO_RECTANGLE(aes).map { PathPoint(aes, it) } }
            }
            return process(rectangles.map(::PathData), isClosed = true)
        }

        private fun process(paths: Collection<PathData>, isClosed: Boolean): List<DataPointLiveMapAesthetics> {
            return paths.map { pathToBuilder(it.aes, it.coordinates.toVecs(), isClosed) }
        }
    }

    private inner class SinglePathFeatureConverter(
        aesthetics: Aesthetics
    ) : PathFeatureConverterBase(aesthetics) {
        fun tile(): List<DataPointLiveMapAesthetics> {
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

        fun segment(geom: SegmentGeom): List<DataPointLiveMapAesthetics> {
            setArrowSpec(geom.arrowSpec)
            setAnimation(geom.animation)
            setFlat(geom.flat)
            setGeodesic(geom.geodesic)
            setSpacer(geom.spacer)

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

        fun curve(geom: CurveGeom): List<DataPointLiveMapAesthetics> {
            setArrowSpec(geom.arrowSpec)
            setSpacer(geom.spacer)
            setIsCurve(true)
            setFlat(true)

            return process(isClosed = false) {
                if (SeriesUtil.allFinite(it.x(), it.y(), it.xend(), it.yend())) {
                    CurveGeom.createGeometry(
                        start = DoubleVector(it.x()!!, it.y()!!),
                        end = DoubleVector(it.xend()!!, it.yend()!!),
                        curvature = geom.curvature,
                        angle = geom.angle,
                        ncp = geom.ncp
                    )
                } else {
                    emptyList()
                }
            }
        }

        fun spoke(geom: SpokeGeom): List<DataPointLiveMapAesthetics> {
            return process(isClosed = false) {
                val x = finiteOrNull(it.x()) ?: return@process emptyList()
                val y = finiteOrNull(it.y()) ?: return@process emptyList()
                val angle = finiteOrNull(it.angle()) ?: return@process emptyList()
                val radius = finiteOrNull(it.radius()) ?: return@process emptyList()

                SpokeGeom.createGeometry(x, y, angle, radius, geom.pivot)
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
        val size: Double,
        val alphaStroke: Boolean
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

        fun point(geom: PointGeom): List<DataPointLiveMapAesthetics> {
            myAnimation = parsePointAnimation(geom.animation)

            return process(MapLayerKind.POINT) { explicitVec(it.x()!!, it.y()!!) }
        }

        fun hLine(): List<DataPointLiveMapAesthetics> {
            return process(MapLayerKind.H_LINE) {
                if (SeriesUtil.isFinite(it.interceptY())) {
                    explicitVec(0.0, it.interceptY()!!)
                } else {
                    null
                }
            }
        }

        fun vLine(): List<DataPointLiveMapAesthetics> {
            return process(MapLayerKind.V_LINE) {
                if (SeriesUtil.isFinite(it.interceptX())) {
                    explicitVec(it.interceptX()!!, 0.0)
                } else {
                    null
                }
            }
        }

        private var myLabelOptions: LabelOptions? = null

        fun text(geom: Geom): List<DataPointLiveMapAesthetics> {
            if (geom is LabelGeom) {
                myLabelOptions = LabelOptions(
                    padding = geom.paddingFactor,
                    radius = geom.radiusFactor,
                    size = geom.borderWidth,
                    alphaStroke = geom.alphaStroke
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
                    DataPointLiveMapAesthetics(p, layerKind).apply {
                        point = v
                        setAnimation(myAnimation)
                        setLabelOptions(myLabelOptions)
                    }
                }?.let(mapObjects::add)
            }

            return mapObjects
        }
    }

    internal class MultiDataPointHelper private constructor(
    ) {
        companion object {
            fun getPoints(
                dataPoints: Iterable<DataPointAesthetics>
            ): List<MultiDataPoint> {
                val builders = HashMap<Vec<LonLat>, MultiDataPointBuilder>()

                fun fetchBuilder(p: DataPointAesthetics): MultiDataPointBuilder {
                    val coord = explicitVec<LonLat>(p.x()!!, p.y()!!)
                    return builders.getOrPut(coord) { MultiDataPointBuilder(p) }
                }

                dataPoints.forEach { p -> fetchBuilder(p).add(p) }
                return builders.values.map(MultiDataPointBuilder::build)
            }
        }

        private class MultiDataPointBuilder(
            private val myAes: DataPointAesthetics
        ) {
            private val myPoints = ArrayList<DataPointAesthetics>()

            fun add(p: DataPointAesthetics) {
                myPoints.add(p)
            }

            fun build(): MultiDataPoint {
                return MultiDataPoint(
                    aes = myAes,
                    indices = myPoints.map(DataPointAesthetics::index),
                    values = myPoints.map { it.slice()!! },
                    colorArray = myPoints.map { it.color() ?: Color.TRANSPARENT },
                    fillArray = myPoints.map { it.fill() ?: DefaultNaValue[Aes.FILL] },
                    strokeArray = myPoints.map { it.stroke() ?: 0.0 },
                    explodeValues = myPoints.map { it.explode() ?: 0.0 }
                )
            }
        }

        internal data class MultiDataPoint(
            val aes: DataPointAesthetics,
            val indices: List<Int>,
            val values: List<Double>,
            val colorArray: List<Color>,
            val fillArray: List<Color>,
            val strokeArray: List<Double>,
            val explodeValues: List<Double>
        )
    }
}