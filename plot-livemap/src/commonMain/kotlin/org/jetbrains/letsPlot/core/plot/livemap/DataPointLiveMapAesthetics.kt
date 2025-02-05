/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.livemap

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.limitLat
import org.jetbrains.letsPlot.commons.intern.spatial.normalizeLon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.times
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MAP_ID
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.geom.PieGeom
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor.*
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor.*
import org.jetbrains.letsPlot.core.plot.builder.scale.DefaultNaValue
import org.jetbrains.letsPlot.core.plot.livemap.DataPointsConverter.LabelOptions
import org.jetbrains.letsPlot.core.plot.livemap.DataPointsConverter.MultiDataPointHelper.MultiDataPoint
import org.jetbrains.letsPlot.core.plot.livemap.DataPointsConverter.PieOptions
import org.jetbrains.letsPlot.core.plot.livemap.MapLayerKind.*
import org.jetbrains.letsPlot.livemap.Client.Companion.px
import org.jetbrains.letsPlot.livemap.api.GeoObject
import org.jetbrains.letsPlot.livemap.chart.donut.StrokeSide
import kotlin.math.ceil

typealias LiveMapArrowSpec = org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec
internal class DataPointLiveMapAesthetics {
    constructor(p: DataPointAesthetics, layerKind: MapLayerKind) {
        myLayerKind = layerKind
        myP = p
        indices = emptyList()
        valueArray = emptyList()
        colorArray = emptyList()
        strokeArray = emptyList()
        explodeArray = emptyList()
    }

    constructor(p: MultiDataPoint, layerKind: MapLayerKind) {
        myLayerKind = layerKind
        myP = p.aes
        indices = p.indices
        valueArray = p.values
        myFillArray = p.fillArray
        colorArray = p.colorArray
        strokeArray = p.strokeArray
        explodeArray = p.explodeValues
    }

    val myP: DataPointAesthetics

    val indices: List<Int>
    val valueArray: List<Double>
    private var myFillArray: List<Color> = emptyList()
    val colorArray: List<Color>
    val strokeArray: List<Double>
    val explodeArray: List<Double>

    val myLayerKind: MapLayerKind

    var geometry: List<Vec<LonLat>>? = null
        set(value) {
            field = value?.map(::trimLonLat)
        }

    var point: Vec<LonLat> = LonLat.ZERO_VEC
        set(value) {
            field = trimLonLat(value)
        }

    var flat: Boolean = false
    var geodesic: Boolean = false
    var animation = 0
    var isCurve: Boolean = false

    private var myPlotArrowSpec: ArrowSpec? = null
    val arrowSpec: LiveMapArrowSpec? get() {
        return myPlotArrowSpec?.let {
            LiveMapArrowSpec(
                angle = it.angle,
                length = it.length.px,
                end = when (it.end) {
                    ArrowSpec.End.LAST -> org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec.End.LAST
                    ArrowSpec.End.FIRST -> org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec.End.FIRST
                    ArrowSpec.End.BOTH -> org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec.End.BOTH
                },
                type = when (it.type) {
                    ArrowSpec.Type.OPEN -> org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec.Type.OPEN
                    ArrowSpec.Type.CLOSED -> org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec.Type.CLOSED
                }
            )
        }
    }

    val index get() = myP.index()
    val flow get() = myP.flow()!!
    val speed get() = myP.speed()!!
    val family get() = myP.family()
    val angle get() = myP.angle()!!
    val shape get() = myP.shape()!!.code
    val size get() = AestheticsUtil.textSize(myP)
    val fillColor get() = colorWithAlpha(myP.fill()!!)
    val label get() = myP.label()?.toString() ?: "n/a"
    val lineheight get() = myP.lineheight()!!

    val hjust
        get() = when (TextUtil.hAnchor(myP.hjust())) {
            LEFT -> 0.0
            RIGHT -> 1.0
            MIDDLE -> 0.5
        }
    val vjust
        get() = when (TextUtil.vAnchor(myP.vjust())) {
            TOP -> 0.0
            BOTTOM -> 1.0
            CENTER -> 0.5
        }

    val fontface
        get() = when (val fontface = myP.fontface()) {
            AesInitValue[Aes.FONTFACE] -> ""
            else -> fontface
        }

    val lineDash: List<Double>
        get() {
            val lineType = myP.lineType()

            if (lineType.isSolid) {
                return emptyList()
            }

            val width = AestheticsUtil.strokeWidth(myP)
            return lineType.dashArray.map { it * width }
        }

    val lineDashOffset: Double
        get() {
            val lineType = myP.lineType()

            if (lineType.isSolid) {
                return 0.0
            }
            val width = AestheticsUtil.strokeWidth(myP)
            return lineType.dashOffset * width
        }

    val geoObject
        get(): GeoObject? {
            if (myP.mapId() != DefaultNaValue[MAP_ID]) {
                fun List<*>.toVec() = explicitVec<LonLat>(get(0) as Double, get(1) as Double)

                fun List<*>.toGeoRect() =
                    GeoRectangle(
                        startLongitude = get(0) as Double,
                        minLatitude = get(1) as Double,
                        endLongitude = get(2) as Double,
                        maxLatitude = get(3) as Double
                    )

                val geoReference = JsonSupport.parseJson(myP.mapId().toString())
                val id = geoReference["id"] as String
                val lim = (geoReference["lim"] as? List<*>)?.toGeoRect() ?: error("Limit have to be provided")
                val pos = (geoReference["pos"] as? List<*>)?.toGeoRect() ?: error("Position have to be provided")
                val cen = (geoReference["cen"] as? List<*>)?.toVec() ?: error("Centroid have to be provided")

                return GeoObject(id, cen, lim, pos)
            }

            return null
        }

    val strokeColor
        get() = when (myLayerKind) {
            POLYGON, PIE -> myP.color()!!
            else -> colorWithAlpha(myP.color()!!)
        }

    private fun pointRadius(size: Double) = ceil(size / 2.0)

    val radius: Double
        get() = when (myLayerKind) {
            POINT -> pointRadius(myP.shape()!!.size(myP))
            PIE -> AestheticsUtil.pieDiameter(myP) / 2.0
            else -> 0.0
        }

    val strokeWidth
        get() = when (myLayerKind) {
            POLYGON, PATH, H_LINE, V_LINE -> AestheticsUtil.strokeWidth(myP)
            POINT -> AestheticsUtil.pointStrokeWidth(myP)
            TEXT -> 0.0
            PIE -> myP.stroke() ?: 0.0
        }

    val fillArray: List<Color>
        get() = myFillArray.map(::colorWithAlpha)

    val sizeStart
        get() = pointRadius(AestheticsUtil.circleDiameter(myP,  DataPointAesthetics::sizeStart)).px * 2.0
    val sizeEnd
        get() = pointRadius(AestheticsUtil.circleDiameter(myP,  DataPointAesthetics::sizeEnd)).px * 2.0
    val strokeStart
        get() = AestheticsUtil.pointStrokeWidth(myP, DataPointAesthetics::strokeStart).px
    val strokeEnd
        get() = AestheticsUtil.pointStrokeWidth(myP, DataPointAesthetics::strokeEnd).px
    var spacer = 0.px

    private var myLabelOptions: LabelOptions? = null
    val labelPadding: Double
        get() = myLabelOptions?.padding ?: 0.0
    val labelRadius: Double
        get() = myLabelOptions?.radius ?: 0.0
    val labelSize: Double
        get() = myLabelOptions?.size ?: 0.0
    val alphaStroke: Boolean
        get() = myLabelOptions?.alphaStroke ?: false

    private var myPieOptions: PieOptions? = null
    val holeRatio: Double
        get() = myPieOptions?.holeSize ?: 0.0
    val spacerColor: Color
        get() = myPieOptions?.spacerColor ?: Color.WHITE
    val spacerWidth: Double
        get() = myPieOptions?.spacerWidth ?: 1.0
    val strokeSide: StrokeSide
        get() = myPieOptions?.strokeSide?.let {
            when (it) {
                PieGeom.StrokeSide.OUTER -> StrokeSide.OUTER
                PieGeom.StrokeSide.INNER -> StrokeSide.INNER
                PieGeom.StrokeSide.BOTH -> StrokeSide.BOTH
            }
        } ?: StrokeSide.OUTER

    private fun colorWithAlpha(color: Color): Color {
        return color.changeAlpha((AestheticsUtil.alpha(color, myP) * 255).toInt())
    }

    fun setArrowSpec(arrowSpec: ArrowSpec?): DataPointLiveMapAesthetics {
        myPlotArrowSpec = arrowSpec
        return this
    }

    fun setAnimation(animation: Int?): DataPointLiveMapAesthetics {
        if (animation != null) {
            this.animation = animation
        }
        return this
    }

    fun setLabelOptions(labelOptions: LabelOptions?): DataPointLiveMapAesthetics {
        myLabelOptions = labelOptions
        return this
    }

    fun setPieOptions(pieOptions: PieOptions?): DataPointLiveMapAesthetics {
        myPieOptions = pieOptions
        return this
    }

    // Limit Lon Lat to -180, 180; -90, 90
    private fun trimLonLat(p: Vec<LonLat>): Vec<LonLat> {
        return Vec(normalizeLon(p.x), limitLat(p.y))
    }
}