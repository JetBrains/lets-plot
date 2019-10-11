package jetbrains.livemap.geom

import jetbrains.datalore.base.gcommon.collect.Lists.transform
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLat
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLon
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.builder.scale.DefaultNaValue
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.svg.TextLabel.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.TextLabel.VerticalAnchor.*
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.mapobjects.MapLayerKind.*
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.mapobjects.MapPoint
import jetbrains.livemap.projections.MapProjection
import kotlin.math.ceil

internal class MapObjectBuilder {

    private val myLayerKind: MapLayerKind
    private val myMapProjection: MapProjection
    private val myP: DataPointAesthetics

    private var myValueArray: List<Double> = emptyList()
    private var myColorArray: List<Color> = emptyList()
    private var myStrokeWidth: Double? = null
    var geometry: Geometry? = null
        private set
    var point: Vec<LonLat>? = null
        private set
    var indicies = emptyList<Int>()
    private var myArrowSpec: ArrowSpec? = null
    var animation = 0
        private set
    private var myMaxAbsValue: Double? = null

    private val index: Int
        get() = myP.index()

    private val mapId: String?
        get() {
            val mapId = myP.mapId()
            return if (mapId == AesInitValue.get(Aes.MAP_ID)) null else mapId.toString()
        }

    private val regionId: String?
        get() = null

    private val shape: Int
        get() = myP.shape()!!.code

    private val radius: Double
        get() {
            return when (myLayerKind) {
                POLYGON, PATH, H_LINE, V_LINE, POINT, PIE, BAR -> ceil(myP.shape()!!.size(myP) / 2.0)
                HEATMAP -> myP.size()!!
                TEXT -> 0.0
                else -> 0.0
            }
        }

    val size: Double
        get() = AestheticsUtil.textSize(myP)

    private val strokeWidth: Double
        get() {
            if (myStrokeWidth != null) {
                return myStrokeWidth!!
            }

            return when (myLayerKind) {
                POLYGON, PATH, H_LINE, V_LINE -> AestheticsUtil.strokeWidth(myP)
                POINT, PIE, BAR -> 1.0
                TEXT -> 0.0
                HEATMAP -> myStrokeWidth!!
                else -> myStrokeWidth!!
            }
        }

    val frame: String
        get() = myP.frame()

    val speed: Double
        get() = myP.speed()!!

    val flow: Double
        get() = myP.flow()!!

    private val fillColor: Color
        get() = colorWithAlpha(myP.fill()!!)

    private val strokeColor: Color
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
            return if (fontface == AesInitValue.get(Aes.FONTFACE)) "" else fontface
        }

    val hjust: Double
        get() = hjust(myP.hjust())

    val vjust: Double
        get() = vjust(myP.vjust())

    val angle: Double
        get() = myP.angle()!!

    val colorArray: List<Color>
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
        myP = p.aes()
        indicies = p.indices()
        myValueArray = p.values()
        myColorArray = p.colors()
    }

    fun getArrowSpec(): ArrowSpec? {
        return myArrowSpec
    }

    private fun colorWithAlpha(color: Color): Color {
        return color.changeAlpha((AestheticsUtil.alpha(color, myP) * 255).toInt())
    }

    fun build(consumer: (MapObject) -> Unit) {
        when (myLayerKind) {
            //POLYGON -> consumer(MapJsObjectUtil.createJsPolygon(this))
            //PATH -> consumer(MapJsObjectUtil.createJsPath(this))
            POINT -> consumer(createPoint())
            //PIE -> MapJsObjectUtil.splitMapPieChart(this).forEach(consumer)
            //BAR -> MapJsObjectUtil.splitMapBarChart(this, myMaxAbsValue).forEach(consumer)
            //HEATMAP -> consumer(MapJsObjectUtil.createJsHeatmap(this))
            //TEXT -> consumer(MapJsObjectUtil.createJsText(this))
            //H_LINE, V_LINE -> consumer(MapJsObjectUtil.createJsLine(this))
            else -> throw IllegalArgumentException("Unknown map layer kind: $myLayerKind")
        }
    }

    private fun createPoint(): MapPoint {
        return MapPoint(
            index,
            mapId,
            regionId,
            point!!,
            label,
            animation,
            shape,
            radius,
            fillColor,
            strokeColor,
            strokeWidth
        )
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

//    fun setGeometryData(points: List<DoubleVector>, isPolygon: Boolean, isGeodesic: Boolean): MapObjectBuilder {
//        val coordinates = points.map { limitCoord(it) }
//
//        val multipolygon: MultiPolygon<LonLat>
//        if (isPolygon) {
//            multipolygon = GeomUtil.createMultiPolygon(coordinates)
//        } else {
//            val polygons = ArrayList<Polygon<*>>()
//            MapWidgetUtil
//                .splitPathByAntiMeridian(if (isGeodesic) MapWidgetUtil.createArcPath(coordinates) else coordinates)
//                .forEach { path -> polygons.add(Polygon.create(Ring(path))) }
//            multipolygon = MultiPolygon(polygons)
//        }
//        geometry = Geometry.create(transformMultipolygon(multipolygon, myMapProjection::project))
//        return this
//    }

    fun setArrowSpec(arrowSpec: ArrowSpec): MapObjectBuilder {
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