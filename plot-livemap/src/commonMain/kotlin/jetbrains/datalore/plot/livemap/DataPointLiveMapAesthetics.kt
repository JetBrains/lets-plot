/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.base.Aes.Companion.MAP_ID
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.TextUtil
import jetbrains.datalore.plot.base.render.svg.Text.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.Text.VerticalAnchor.*
import jetbrains.datalore.plot.builder.scale.DefaultNaValue
import jetbrains.datalore.plot.livemap.DataPointsConverter.LabelOptions
import jetbrains.datalore.plot.livemap.DataPointsConverter.MultiDataPointHelper.MultiDataPoint
import jetbrains.datalore.plot.livemap.DataPointsConverter.PieOptions
import jetbrains.datalore.plot.livemap.MapLayerKind.*
import jetbrains.livemap.api.GeoObject
import jetbrains.livemap.api.geometry
import jetbrains.livemap.api.limitCoord
import kotlin.math.ceil

internal class DataPointLiveMapAesthetics {
    constructor(p: DataPointAesthetics, layerKind: MapLayerKind) {
        myLayerKind = layerKind
        myP = p
        indices = emptyList<Int>()
        valueArray = emptyList()
        explodeArray = null
    }

    constructor(p: MultiDataPoint, layerKind: MapLayerKind) {
        myLayerKind = layerKind
        myP = p.aes
        indices = p.indices
        valueArray = p.values
        myColorArray = p.colors
        explodeArray = p.explodeValues
    }

    val myP: DataPointAesthetics
    private var myColorArray: List<Color> = emptyList()
    val indices: List<Int>
    val valueArray: List<Double>
    val explodeArray: List<Double>?

    val myLayerKind: MapLayerKind

    var geometry: MultiPolygon<LonLat>? = null
    var point: Vec<LonLat>? = null
    var animation = 0

    private var myArrowSpec: ArrowSpec? = null
    val arrowAngle: Double?
        get() = myArrowSpec?.angle
    val arrowLength: Double?
        get() = myArrowSpec?.length
    val arrowAtEnds: String?
        get() = myArrowSpec?.end?.name?.lowercase()
    val arrowType: String?
        get() = myArrowSpec?.type?.name?.lowercase()

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

            if (lineType.isSolid || lineType.isBlank) {
                return emptyList()
            }

            val width = AestheticsUtil.strokeWidth(myP)
            return lineType.dashArray.map { it * width }
        }

    val geoObject
        get(): GeoObject? {
            if (myP.mapId() != DefaultNaValue.get(MAP_ID)) {
                fun List<*>.toVec() = explicitVec<LonLat>(get(0) as Double, get(1) as Double)

                fun List<*>.toGeoRect() =
                    GeoRectangle(
                        startLongitude = get(0) as Double,
                        minLatitude = get(1) as Double,
                        endLongitude = get(2) as Double,
                        maxLatitude = get(3) as Double
                    )

                val geoReference = JsonSupport.parseJson(myP.mapId().toString())
                val id = geoReference.get("id") as String
                val lim = (geoReference.get("lim") as? List<*>)?.toGeoRect() ?: error("Limit have to be provided")
                val pos = (geoReference.get("pos") as? List<*>)?.toGeoRect() ?: error("Position have to be provided")
                val cen = (geoReference.get("cen") as? List<*>)?.toVec() ?: error("Centroid have to be provided")

                return GeoObject(id, cen, lim, pos)
            }

            return null
        }

    val strokeColor
        get() = when (myLayerKind) {
            POLYGON -> myP.color()!!
            PIE -> myPieOptions?.strokeColor ?: Color.WHITE
            else -> colorWithAlpha(myP.color()!!)
        }

    val radius: Double
        get() = when (myLayerKind) {
            POLYGON, PATH, H_LINE, V_LINE, POINT -> ceil(myP.shape()!!.size(myP) / 2.0)
            PIE -> AesScaling.pieDiameter(myP) / 2.0
            TEXT -> 0.0
        }

    val strokeWidth
        get() = when (myLayerKind) {
            POLYGON, PATH, H_LINE, V_LINE -> AestheticsUtil.strokeWidth(myP)
            POINT -> 1.0
            TEXT -> 0.0
            PIE -> myPieOptions?.strokeWidth ?: 0.0
        }


    val colorArray: List<Color>
        get() = if (myLayerKind === PIE && valueArray.all(0.0::equals)) {
            List(valueArray.size) { DefaultNaValue[COLOR] }
        } else {
            myColorArray
        }

    private var myLabelOptions: LabelOptions? = null
    val labelPadding: Double
        get() = myLabelOptions?.padding ?: 0.0
    val labelRadius: Double
        get() = myLabelOptions?.radius ?: 0.0
    val labelSize: Double
        get() = myLabelOptions?.size ?: 0.0

    private var myPieOptions: PieOptions? = null
    val holeRatio: Double
        get() = myPieOptions?.holeSize ?: 0.0

    private fun colorWithAlpha(color: Color): Color {
        return color.changeAlpha((AestheticsUtil.alpha(color, myP) * 255).toInt())
    }

    fun setGeometryPoint(lonlat: Vec<LonLat>): DataPointLiveMapAesthetics {
        point = limitCoord(lonlat)
        return this
    }

    fun setGeometryData(points: List<Vec<LonLat>>, isClosed: Boolean, isGeodesic: Boolean): DataPointLiveMapAesthetics {
        geometry = geometry(points, isClosed, isGeodesic)

        return this
    }

    fun setArrowSpec(arrowSpec: ArrowSpec?): DataPointLiveMapAesthetics {
        myArrowSpec = arrowSpec
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
}