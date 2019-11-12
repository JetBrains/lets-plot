/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.gcommon.collect.Lists.transform
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.svg.TextLabel.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.TextLabel.VerticalAnchor.*
import jetbrains.datalore.plot.builder.scale.DefaultNaValue
import jetbrains.livemap.api.*
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.mapobjects.MapLayerKind.*
import kotlin.math.ceil

internal class MapObjectBuilder {

    val myLayerKind: MapLayerKind
    private val myP: DataPointAesthetics

    private var myValueArray: List<Double> = emptyList()
    private var myColorArray: List<Color> = emptyList()
    var geometry: MultiPolygon<LonLat>? = null
    var point: Vec<LonLat>? = null
    private var indices = emptyList<Int>()
    private var myArrowSpec: ArrowSpec? = null
    var animation = 0
    var geodesic: Boolean = false

    val index: Int
        get() = myP.index()

    private val mapId: String?
        get() {
            val mapId = myP.mapId()
            return if (mapId == AesInitValue[Aes.MAP_ID]) null else mapId.toString()
        }

    val regionId: String?
        get() = null

    val shape: Int
        get() = myP.shape()!!.code

    val radius: Double
        get() = when (myLayerKind) {
            POLYGON, PATH, H_LINE, V_LINE, POINT, PIE, BAR -> ceil(myP.shape()!!.size(myP) / 2.0)
            HEATMAP -> myP.size()!!
            TEXT -> 0.0
        }

    val size: Double
        get() = AestheticsUtil.textSize(myP)

    val strokeWidth: Double
        get() {
            return when (myLayerKind) {
                POLYGON, PATH, H_LINE, V_LINE -> AestheticsUtil.strokeWidth(myP)
                POINT, PIE, BAR -> 1.0
                TEXT, HEATMAP -> 0.0
            }
        }

//    val frame: String
//        get() = myP.frame()

    val speed: Double
        get() = myP.speed()!!

    val flow: Double
        get() = myP.flow()!!

    val fillColor: Color
        get() = colorWithAlpha(myP.fill()!!)

    val strokeColor: Color
        get() = colorWithAlpha(myP.color()!!)

    val lineDash: List<Double>
        get() {
            val lineType = myP.lineType()

            if (lineType.isSolid || lineType.isBlank) {
                return emptyList()
            }

            val width = AestheticsUtil.strokeWidth(myP)
            return ArrayList(transform(lineType.dashArray) { length -> length * width })
        }

    val label: String
        get() = myP.label()

    val family: String
        get() = myP.family()

    val fontface: String
        get() {
            val fontface = myP.fontface()
            return if (fontface == AesInitValue[Aes.FONTFACE]) "" else fontface
        }

    val hjust: Double
        get() = hjust(myP.hjust())

    val vjust: Double
        get() = vjust(myP.vjust())

    val angle: Double
        get() = myP.angle()!!

    private val colorArray: List<Color>
        get() = if (myLayerKind === PIE && allValuesAreZero(myValueArray)) {
            createNaColorList(myValueArray.size)
        } else {
            myColorArray
        }

    private fun allValuesAreZero(values: List<Double>): Boolean {
        return values.all { value -> value == 0.0 }
    }

    private fun createNaColorList(size: Int): List<Color> {
        return List(size) { DefaultNaValue[COLOR] }
    }

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

    private fun colorWithAlpha(color: Color): Color {
        return color.changeAlpha((AestheticsUtil.alpha(color, myP) * 255).toInt())
    }

    fun createPointBlock(): (PointBuilder.() -> Unit)? {
        return point?.let{
            {
                index = this@MapObjectBuilder.index
                this@MapObjectBuilder.mapId?.let { mapId = it }
                this@MapObjectBuilder.regionId?.let { regionId = it }
                point = it
                label = this@MapObjectBuilder.label
                animation = this@MapObjectBuilder.animation
                shape = this@MapObjectBuilder.shape
                radius = this@MapObjectBuilder.radius
                fillColor = this@MapObjectBuilder.fillColor
                strokeColor = this@MapObjectBuilder.strokeColor
                strokeWidth = this@MapObjectBuilder.strokeWidth
            }
        }
    }

    fun createPolygonBlock(): PolygonsBuilder.() -> Unit {
        return {
            index = this@MapObjectBuilder.index
            this@MapObjectBuilder.mapId?.let { mapId = it }
            this@MapObjectBuilder.regionId?.let { regionId = it }

            multiPolygon = this@MapObjectBuilder.geometry

            lineDash = this@MapObjectBuilder.lineDash
            fillColor = this@MapObjectBuilder.fillColor
            strokeColor = this@MapObjectBuilder.strokeColor
            strokeWidth = this@MapObjectBuilder.strokeWidth
        }
    }

    fun createPathBlock(): (PathBuilder.() -> Unit)? {
        return geometry?.let {
            {
                index = this@MapObjectBuilder.index
                this@MapObjectBuilder.mapId?.let { mapId = it }
                this@MapObjectBuilder.regionId?.let { regionId = it }

                multiPolygon = it

                lineDash = this@MapObjectBuilder.lineDash
                strokeColor = this@MapObjectBuilder.strokeColor
                strokeWidth = this@MapObjectBuilder.strokeWidth

                animation = this@MapObjectBuilder.animation
                speed = this@MapObjectBuilder.speed
                flow = this@MapObjectBuilder.flow
            }
        }
    }

    fun createLineBlock(): (LineBuilder.() -> Unit)? {
        return point?.let{
            {
                index = this@MapObjectBuilder.index
                this@MapObjectBuilder.mapId?.let { mapId = it }
                this@MapObjectBuilder.regionId?.let { regionId = it }
                point = it
                lineDash = this@MapObjectBuilder.lineDash
                strokeColor = this@MapObjectBuilder.strokeColor
                strokeWidth = this@MapObjectBuilder.strokeWidth
            }
        }
    }

    fun createChartBlock(): (ChartSource.() -> Unit)? {
        return point?.let {
            {
                point = it

                radius = this@MapObjectBuilder.radius

                strokeColor = this@MapObjectBuilder.strokeColor
                strokeWidth = this@MapObjectBuilder.strokeWidth

                indices = this@MapObjectBuilder.indices
                values = this@MapObjectBuilder.myValueArray
                colors = this@MapObjectBuilder.colorArray
            }
        }
    }

    fun createTextBlock(): (TextBuilder.() -> Unit)? {
        return point?.let {
            {
                index = this@MapObjectBuilder.index
                this@MapObjectBuilder.mapId?.let { mapId = it }
                this@MapObjectBuilder.regionId?.let { regionId = it }
                point = it
                fillColor = this@MapObjectBuilder.fillColor
                strokeColor = this@MapObjectBuilder.strokeColor
                strokeWidth = this@MapObjectBuilder.strokeWidth
                label = this@MapObjectBuilder.label
                size = this@MapObjectBuilder.size
                family = this@MapObjectBuilder.family
                fontface = this@MapObjectBuilder.fontface
                hjust = this@MapObjectBuilder.hjust
                vjust = this@MapObjectBuilder.vjust
                angle = this@MapObjectBuilder.angle
            }
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

    fun setGeometryPoint(lonlat: Vec<LonLat>): MapObjectBuilder {
        point = limitCoord(lonlat)
        return this
    }

    fun setGeometryData(points: List<Vec<LonLat>>, isClosed: Boolean, isGeodesic: Boolean): MapObjectBuilder {
        geometry = geometry(points, isClosed, isGeodesic)

        return this
    }

    fun setArrowSpec(arrowSpec: ArrowSpec?): MapObjectBuilder {
        myArrowSpec = arrowSpec
        return this
    }

    fun setAnimation(animation: Int?): MapObjectBuilder {
        if (animation != null) {
            this.animation = animation
        }
        return this
    }
}