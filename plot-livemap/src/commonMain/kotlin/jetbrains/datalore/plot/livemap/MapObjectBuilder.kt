package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.gcommon.collect.Lists.transform
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLat
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLon
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.render.svg.TextLabel.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.TextLabel.VerticalAnchor.*
import jetbrains.datalore.plot.builder.scale.DefaultNaValue
import jetbrains.gis.geoprotocol.TypedGeometry
import jetbrains.livemap.MapWidgetUtil
import jetbrains.livemap.api.ChartSource
import jetbrains.livemap.mapobjects.*
import jetbrains.livemap.mapobjects.MapLayerKind.*
import jetbrains.livemap.mapobjects.Utils.splitMapBarChart
import jetbrains.livemap.mapobjects.Utils.splitMapPieChart
import jetbrains.livemap.projections.MapProjection
import kotlin.math.ceil

internal class MapObjectBuilder {

    private val myLayerKind: MapLayerKind
    private val myMapProjection: MapProjection
    private val myP: DataPointAesthetics

    private var myValueArray: List<Double> = emptyList()
    private var myColorArray: List<Color> = emptyList()
    private var myStrokeWidth: Double? = null
    private var geometry: TypedGeometry<LonLat>? = null
    private var point: Vec<LonLat>? = null
    private var indicies = emptyList<Int>()
    private var myArrowSpec: ArrowSpec? = null
    private var animation = 0
    private var myMaxAbsValue: Double? = null

    private val index: Int
        get() = myP.index()

    private val mapId: String?
        get() {
            val mapId = myP.mapId()
            return if (mapId == AesInitValue[Aes.MAP_ID]) null else mapId.toString()
        }

    private val regionId: String?
        get() = null

    private val shape: Int
        get() = myP.shape()!!.code

    private val radius: Double
        get() = when (myLayerKind) {
            POLYGON, PATH, H_LINE, V_LINE, POINT, PIE, BAR -> ceil(myP.shape()!!.size(myP) / 2.0)
            HEATMAP -> myP.size()!!
            TEXT -> 0.0
            else -> 0.0
        }

    val size: Double
        get() = AestheticsUtil.textSize(myP)

    private val strokeWidth: Double
        get() {
            return myStrokeWidth ?: when (myLayerKind) {
                POLYGON, PATH, H_LINE, V_LINE -> AestheticsUtil.strokeWidth(myP)
                POINT, PIE, BAR -> 1.0
                TEXT, HEATMAP -> 0.0
                else -> 0.0
            }
        }

//    val frame: String
//        get() = myP.frame()

    private val speed: Double
        get() = myP.speed()!!

    private val flow: Double
        get() = myP.flow()!!

    private val fillColor: Color
        get() = colorWithAlpha(myP.fill()!!)

    private val strokeColor: Color
        get() = colorWithAlpha(myP.color()!!)

    private val lineDash: List<Double>
        get() {
            val lineType = myP.lineType()

            if (lineType.isSolid || lineType.isBlank) {
                return emptyList()
            }

            val width = AestheticsUtil.strokeWidth(myP)
            return ArrayList(transform(lineType.dashArray) { length -> length * width })
        }

    private val label: String
        get() = myP.label()

    private val family: String
        get() = myP.family()

    private val fontface: String
        get() {
            val fontface = myP.fontface()
            return if (fontface == AesInitValue[Aes.FONTFACE]) "" else fontface
        }

    private val hjust: Double
        get() = hjust(myP.hjust())

    private val vjust: Double
        get() = vjust(myP.vjust())

    private val angle: Double
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

    private fun limitCoord(point: DoubleVector): DoubleVector {
        return DoubleVector(limitLon(point.x), limitLat(point.y))
    }

    private fun limitCoord(point: Vec<LonLat>): Vec<LonLat> {
        return explicitVec(limitLon(point.x), limitLat(point.y))
    }

    constructor(p: DataPointAesthetics, layerKind: MapLayerKind, mapProjection: MapProjection) {
        myLayerKind = layerKind
        myMapProjection = mapProjection
        myP = p
    }

    constructor(p: MultiDataPointHelper.MultiDataPoint, layerKind: MapLayerKind, mapProjection: MapProjection) {
        myLayerKind = layerKind
        myMapProjection = mapProjection
        myP = p.aes
        indicies = p.indices
        myValueArray = p.values
        myColorArray = p.colors
    }

    private fun colorWithAlpha(color: Color): Color {
        return color.changeAlpha((AestheticsUtil.alpha(color, myP) * 255).toInt())
    }

    fun build(consumer: (MapObject) -> Unit) {
         when (myLayerKind) {
            POLYGON -> consumer(createPolygon())
            PATH -> consumer(createPath())
            POINT -> createPoint()?.run(consumer)
            PIE -> createChartSource()?.run { splitMapPieChart(this).forEach(consumer) }
            BAR -> createChartSource()?.run { splitMapBarChart(this, myMaxAbsValue!!).forEach(consumer) }
            //HEATMAP -> consumer(MapJsObjectUtil.createJsHeatmap(this))
            TEXT -> createText()?.run(consumer)
            H_LINE, V_LINE -> createLine()?.run(consumer)
            else -> throw IllegalArgumentException("Unknown map layer kind: $myLayerKind")
        }
    }

    private fun createPoint(): MapPoint? {
        return point?.let { p ->
            MapPoint(
                index,
                mapId,
                regionId,
                p,
                label,
                animation,
                shape,
                radius,
                fillColor,
                strokeColor,
                strokeWidth
            )
        }
    }

    private fun createPolygon(): MapPolygon {
        return MapPolygon(
            index,
            mapId,
            regionId,
            lineDash,
            strokeColor,
            strokeWidth,
            fillColor,
            geometry
        )
    }

    private fun createPath(): MapPath {
        return MapPath(
            index,
            mapId,
            regionId,

            geometry!!,

            animation,
            speed,

            flow,
            lineDash,
            strokeColor,
            strokeWidth
        )
    }

    private fun createLine(): MapLine? {
        return point?.let {
            MapLine(
                index,
                mapId,
                regionId,
                it,
                lineDash,
                strokeColor,
                strokeWidth
            )
        }
    }

    private fun createChartSource(): ChartSource? {
        return point?.let {
            ChartSource().apply {
                lon = it.x
                lat = it.y

                radius = this@MapObjectBuilder.radius

                strokeColor = this@MapObjectBuilder.strokeColor
                strokeWidth = this@MapObjectBuilder.strokeWidth

                indices = this@MapObjectBuilder.indicies
                values = this@MapObjectBuilder.myValueArray
                colors = this@MapObjectBuilder.colorArray
            }
        }
    }

    private fun createText(): MapText? {
        return point?.let {
            MapText(
                index,
                mapId,
                regionId,
                it,
                fillColor,
                strokeColor,
                strokeWidth,
                label,
                size,
                family,
                fontface,
                hjust,
                vjust,
                angle
            )
        }
    }

    private fun hjust(hjust: Any): Double {
        return when (GeomHelper.textLabelAnchor(hjust, GeomHelper.HJUST_MAP, MIDDLE)) {
            LEFT -> 0.0
            RIGHT -> 1.0
            MIDDLE -> 0.5
            else -> throw IllegalArgumentException("Unknown hjust: $hjust")
        }
    }

    private fun vjust(vjust: Any): Double {
        return when (GeomHelper.textLabelAnchor(vjust, GeomHelper.VJUST_MAP, CENTER)) {
            TOP -> 0.0
            BOTTOM -> 1.0
            CENTER -> 0.5
            else -> throw IllegalArgumentException("Unknown vjust: $vjust")
        }
    }

    fun setStrokeWidth(strokeWidth: Double?): MapObjectBuilder {
        myStrokeWidth = strokeWidth
        return this
    }

    fun setGeometryPoint(lonlat: Vec<LonLat>): MapObjectBuilder {
        point = limitCoord(lonlat)
        return this
    }

    fun setGeometryData(points: List<DoubleVector>, isPolygon: Boolean, isGeodesic: Boolean): MapObjectBuilder {
        val coordinates = points.map { limitCoord(it) }

        val multipolygon = if (isPolygon) {
            GeomUtil.createMultiPolygon(coordinates)
        } else {
            MapWidgetUtil
                .splitPathByAntiMeridian(if (isGeodesic) MapWidgetUtil.createArcPath(coordinates) else coordinates)
                .map { path -> Polygon(listOf(Ring(path.map { it.toVec<Generic>() }))) }
                .run(::MultiPolygon)
        }

        geometry = TypedGeometry.create(multipolygon.reinterpret())
        return this
    }

    private fun <T> DoubleVector.toVec(): Vec<T> {
        return explicitVec(x, y)
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

    fun setMaxAbsValue(maxAbsValue: Double?): MapObjectBuilder {
        myMaxAbsValue = maxAbsValue
        return this
    }
}