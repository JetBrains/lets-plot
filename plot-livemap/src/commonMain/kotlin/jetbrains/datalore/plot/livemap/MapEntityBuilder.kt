/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.gcommon.collect.Lists.transform
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.spatial.LonLat
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

internal class MapEntityBuilder {
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

    val index get() = myP.index()
    val regionId get() = null
    val shape get() = myP.shape()!!.code
    val size get() = AestheticsUtil.textSize(myP)
    val speed get() = myP.speed()!!
    val flow get() = myP.flow()!!
    val fillColor get() = colorWithAlpha(myP.fill()!!)
    val strokeColor get() = colorWithAlpha(myP.color()!!)
    val label get() = myP.label()
    val family get() = myP.family()
    val hjust get() = hjust(myP.hjust())
    val vjust get() = vjust(myP.vjust())
    val angle get() = myP.angle()!!

    private val mapId get() = when (val mapId = myP.mapId()) {
        AesInitValue[Aes.MAP_ID] -> null
        else -> mapId.toString()
    }

    val fontface get() = when (val fontface = myP.fontface()) {
        AesInitValue[Aes.FONTFACE] -> ""
        else -> fontface
    }

    val radius: Double get() = when (myLayerKind) {
        POLYGON, PATH, H_LINE, V_LINE, POINT, PIE, BAR -> ceil(myP.shape()!!.size(myP) / 2.0)
        HEATMAP -> myP.size()!!
        TEXT -> 0.0
    }

    val strokeWidth get() = when (myLayerKind) {
        POLYGON, PATH, H_LINE, V_LINE -> AestheticsUtil.strokeWidth(myP)
        POINT, PIE, BAR -> 1.0
        TEXT, HEATMAP -> 0.0
    }

//    val frame: String
//        get() = myP.frame()

    val lineDash: List<Double>
        get() {
            val lineType = myP.lineType()

            if (lineType.isSolid || lineType.isBlank) {
                return emptyList()
            }

            val width = AestheticsUtil.strokeWidth(myP)
            return ArrayList(transform(lineType.dashArray) { it * width })
        }

    private val colorArray: List<Color>
        get() = if (myLayerKind === PIE && allZeroes(myValueArray)) {
            createNaColorList(myValueArray.size)
        } else {
            myColorArray
        }

    private fun allZeroes(values: List<Double>): Boolean {
        return values.all { value -> value == 0.0 }
    }

    private fun createNaColorList(size: Int): List<Color> {
        return List(size) { DefaultNaValue[COLOR] }
    }

    private fun colorWithAlpha(color: Color): Color {
        return color.changeAlpha((AestheticsUtil.alpha(color, myP) * 255).toInt())
    }

    fun toPointBuilder(): (PointBuilder.() -> Unit)? {
        return point?.let {
            {
                index = this@MapEntityBuilder.index
                this@MapEntityBuilder.mapId?.let { mapId = it }
                this@MapEntityBuilder.regionId?.let { regionId = it }
                point = it
                label = this@MapEntityBuilder.label
                animation = this@MapEntityBuilder.animation
                shape = this@MapEntityBuilder.shape
                radius = this@MapEntityBuilder.radius
                fillColor = this@MapEntityBuilder.fillColor
                strokeColor = this@MapEntityBuilder.strokeColor
                strokeWidth = this@MapEntityBuilder.strokeWidth
            }
        }
    }

    fun createPolygonConfigurator(): PolygonsBuilder.() -> Unit {
        return {
            index = this@MapEntityBuilder.index
            this@MapEntityBuilder.mapId?.let { mapId = it }
            this@MapEntityBuilder.regionId?.let { regionId = it }

            multiPolygon = this@MapEntityBuilder.geometry

            lineDash = this@MapEntityBuilder.lineDash
            fillColor = this@MapEntityBuilder.fillColor
            strokeColor = this@MapEntityBuilder.strokeColor
            strokeWidth = this@MapEntityBuilder.strokeWidth
        }
    }

    fun toPathBuilder(): (PathBuilder.() -> Unit)? {
        return geometry?.let {
            {
                index = this@MapEntityBuilder.index
                this@MapEntityBuilder.mapId?.let { mapId = it }
                this@MapEntityBuilder.regionId?.let { regionId = it }

                multiPolygon = it

                lineDash = this@MapEntityBuilder.lineDash
                strokeColor = this@MapEntityBuilder.strokeColor
                strokeWidth = this@MapEntityBuilder.strokeWidth

                animation = this@MapEntityBuilder.animation
                speed = this@MapEntityBuilder.speed
                flow = this@MapEntityBuilder.flow
            }
        }
    }

    fun toLineBuilder(): (LineBuilder.() -> Unit)? {
        return point?.let {
            {
                index = this@MapEntityBuilder.index
                this@MapEntityBuilder.mapId?.let { mapId = it }
                this@MapEntityBuilder.regionId?.let { regionId = it }
                point = it
                lineDash = this@MapEntityBuilder.lineDash
                strokeColor = this@MapEntityBuilder.strokeColor
                strokeWidth = this@MapEntityBuilder.strokeWidth
            }
        }
    }

    fun toChartBuilder(): (ChartSource.() -> Unit)? {
        return point?.let {
            {
                point = it

                radius = this@MapEntityBuilder.radius

                strokeColor = this@MapEntityBuilder.strokeColor
                strokeWidth = this@MapEntityBuilder.strokeWidth

                indices = this@MapEntityBuilder.indices
                values = this@MapEntityBuilder.myValueArray
                colors = this@MapEntityBuilder.colorArray
            }
        }
    }

    fun toTextBuilder(): (TextBuilder.() -> Unit)? {
        return point?.let {
            {
                index = this@MapEntityBuilder.index
                this@MapEntityBuilder.mapId?.let { mapId = it }
                this@MapEntityBuilder.regionId?.let { regionId = it }
                point = it
                fillColor = this@MapEntityBuilder.fillColor
                strokeColor = this@MapEntityBuilder.strokeColor
                strokeWidth = this@MapEntityBuilder.strokeWidth
                label = this@MapEntityBuilder.label
                size = this@MapEntityBuilder.size
                family = this@MapEntityBuilder.family
                fontface = this@MapEntityBuilder.fontface
                hjust = this@MapEntityBuilder.hjust
                vjust = this@MapEntityBuilder.vjust
                angle = this@MapEntityBuilder.angle
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

    fun setGeometryPoint(lonlat: Vec<LonLat>): MapEntityBuilder {
        point = limitCoord(lonlat)
        return this
    }

    fun setGeometryData(points: List<Vec<LonLat>>, isClosed: Boolean, isGeodesic: Boolean): MapEntityBuilder {
        geometry = geometry(points, isClosed, isGeodesic)

        return this
    }

    fun setArrowSpec(arrowSpec: ArrowSpec?): MapEntityBuilder {
        myArrowSpec = arrowSpec
        return this
    }

    fun setAnimation(animation: Int?): MapEntityBuilder {
        if (animation != null) {
            this.animation = animation
        }
        return this
    }
}