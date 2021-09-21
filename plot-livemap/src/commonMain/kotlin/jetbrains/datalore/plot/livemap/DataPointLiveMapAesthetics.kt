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
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.svg.TextLabel.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.TextLabel.VerticalAnchor.*
import jetbrains.datalore.plot.builder.scale.DefaultNaValue
import jetbrains.datalore.plot.livemap.MapLayerKind.*
import jetbrains.livemap.api.*
import kotlin.math.ceil

internal class DataPointLiveMapAesthetics {
    constructor(p: DataPointAesthetics, layerKind: MapLayerKind) {
        myLayerKind = layerKind
        myP = p
    }

    constructor(p: MultiDataPointHelper.MultiDataPoint, layerKind: MapLayerKind) {
        myLayerKind = layerKind
        myP = p.aes
        indices = p.indices
        myValueArray = p.values
        myColorArray = p.colors
    }

    private val myP: DataPointAesthetics
    private var indices = emptyList<Int>()
    private var myArrowSpec: ArrowSpec? = null
    private var myValueArray: List<Double> = emptyList()
    private var myColorArray: List<Color> = emptyList()

    val myLayerKind: MapLayerKind

    var geometry: MultiPolygon<LonLat>? = null
    var point: Vec<LonLat>? = null
    var animation = 0
    var geodesic: Boolean = false
    var layerIndex: Int? = null

    val index get() = myP.index()
    val shape get() = myP.shape()!!.code
    val size get() = AestheticsUtil.textSize(myP)
    val speed get() = myP.speed()!!
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

    val flow get() = myP.flow()!!
    val fillColor get() = colorWithAlpha(myP.fill()!!)
    val strokeColor
        get() = when (myLayerKind) {
            POLYGON -> myP.color()!!
            else -> colorWithAlpha(myP.color()!!)
        }

    val label get() = myP.label()?.toString() ?: "n/a"
    val family get() = myP.family()
    val hjust get() = hjust(myP.hjust())
    val vjust get() = vjust(myP.vjust())
    val angle get() = myP.angle()!!

    val fontface
        get() = when (val fontface = myP.fontface()) {
            AesInitValue[Aes.FONTFACE] -> ""
            else -> fontface
        }

    val radius: Double
        get() = when (myLayerKind) {
            POLYGON, PATH, H_LINE, V_LINE, POINT, PIE, BAR -> ceil(myP.shape()!!.size(myP) / 2.0)
            HEATMAP -> myP.size()!!
            TEXT -> 0.0
        }

    val strokeWidth
        get() = when (myLayerKind) {
            POLYGON, PATH, H_LINE, V_LINE -> AestheticsUtil.strokeWidth(myP)
            POINT, PIE, BAR -> 1.0
            TEXT, HEATMAP -> 0.0
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

    private val colorArray: List<Color>
        get() = if (myLayerKind === PIE && myValueArray.all(0.0::equals)) {
            List(myValueArray.size) { DefaultNaValue[COLOR] }
        } else {
            myColorArray
        }

    private fun colorWithAlpha(color: Color): Color {
        return color.changeAlpha((AestheticsUtil.alpha(color, myP) * 255).toInt())
    }

    fun toPointBuilder(): PointBuilder.() -> Unit {
        return {
            layerIndex = this@DataPointLiveMapAesthetics.layerIndex
            index = this@DataPointLiveMapAesthetics.index
            point = this@DataPointLiveMapAesthetics.point
            label = this@DataPointLiveMapAesthetics.label
            animation = this@DataPointLiveMapAesthetics.animation
            shape = this@DataPointLiveMapAesthetics.shape
            radius = this@DataPointLiveMapAesthetics.radius
            fillColor = this@DataPointLiveMapAesthetics.fillColor
            strokeColor = this@DataPointLiveMapAesthetics.strokeColor
            strokeWidth = this@DataPointLiveMapAesthetics.strokeWidth
        }
    }

    fun createPolygonConfigurator(): PolygonsBuilder.() -> Unit {
        return {
            layerIndex = this@DataPointLiveMapAesthetics.layerIndex
            index = this@DataPointLiveMapAesthetics.index
            multiPolygon = this@DataPointLiveMapAesthetics.geometry
            geoObject = this@DataPointLiveMapAesthetics.geoObject
            lineDash = this@DataPointLiveMapAesthetics.lineDash
            fillColor = this@DataPointLiveMapAesthetics.fillColor
            strokeColor = this@DataPointLiveMapAesthetics.strokeColor
            strokeWidth = this@DataPointLiveMapAesthetics.strokeWidth
        }
    }

    fun toPathBuilder(): (PathBuilder.() -> Unit)? {
        return geometry?.let {
            {
                layerIndex = this@DataPointLiveMapAesthetics.layerIndex
                index = this@DataPointLiveMapAesthetics.index

                multiPolygon = it

                lineDash = this@DataPointLiveMapAesthetics.lineDash
                strokeColor = this@DataPointLiveMapAesthetics.strokeColor
                strokeWidth = this@DataPointLiveMapAesthetics.strokeWidth

                animation = this@DataPointLiveMapAesthetics.animation
                speed = this@DataPointLiveMapAesthetics.speed
                flow = this@DataPointLiveMapAesthetics.flow
            }
        }
    }

    fun toLineBuilder(): LineBuilder.() -> Unit {
        return {
            point = this@DataPointLiveMapAesthetics.point
            lineDash = this@DataPointLiveMapAesthetics.lineDash
            strokeColor = this@DataPointLiveMapAesthetics.strokeColor
            strokeWidth = this@DataPointLiveMapAesthetics.strokeWidth
        }
    }

    fun toChartBuilder(): Symbol.() -> Unit {
        return {
            layerIndex = this@DataPointLiveMapAesthetics.layerIndex
            point = this@DataPointLiveMapAesthetics.point

            radius = this@DataPointLiveMapAesthetics.radius

            strokeColor = this@DataPointLiveMapAesthetics.strokeColor
            strokeWidth = this@DataPointLiveMapAesthetics.strokeWidth

            indices = this@DataPointLiveMapAesthetics.indices
            values = this@DataPointLiveMapAesthetics.myValueArray
            colors = this@DataPointLiveMapAesthetics.colorArray
        }
    }

    fun toTextBuilder(): TextBuilder.() -> Unit {
        return {
            index = this@DataPointLiveMapAesthetics.index
            point = this@DataPointLiveMapAesthetics.point
            fillColor = this@DataPointLiveMapAesthetics.strokeColor // Text is filled by strokeColor
            strokeColor = this@DataPointLiveMapAesthetics.strokeColor
            strokeWidth = this@DataPointLiveMapAesthetics.strokeWidth
            label = this@DataPointLiveMapAesthetics.label
            size = this@DataPointLiveMapAesthetics.size
            family = this@DataPointLiveMapAesthetics.family
            fontface = this@DataPointLiveMapAesthetics.fontface
            hjust = this@DataPointLiveMapAesthetics.hjust
            vjust = this@DataPointLiveMapAesthetics.vjust
            angle = this@DataPointLiveMapAesthetics.angle
        }
    }

    private fun hjust(hjust: Any): Double {
        return when (GeomHelper.textLabelAnchor(hjust, GeomHelper.HJUST_MAP, MIDDLE)) {
            LEFT -> 0.0
            RIGHT -> 1.0
            MIDDLE -> 0.5
        }
    }

    private fun vjust(vjust: Any): Double {
        return when (GeomHelper.textLabelAnchor(vjust, GeomHelper.VJUST_MAP, CENTER)) {
            TOP -> 0.0
            BOTTOM -> 1.0
            CENTER -> 0.5
        }
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
}