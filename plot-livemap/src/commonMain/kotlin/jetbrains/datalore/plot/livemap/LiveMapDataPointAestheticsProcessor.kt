package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.livemap.LivemapConstants
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.MultiDataPoint
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.SortingMode
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection
import kotlin.math.abs
import kotlin.math.max

internal class LiveMapDataPointAestheticsProcessor(
    private val myAesthetics: Aesthetics,
    private val myMappedDataAccess: MappedDataAccess,
    liveMapOptions: LiveMapOptions,
    private val myMapProjection: MapProjection
) {
    private val myStrokeWidth: Double? = liveMapOptions.stroke
    private val myLayerKind: MapLayerKind
    private val myFrameSpecified: Boolean
    private val myLonLatInsideMapIdSpecified: Boolean
    private var myMaxAbsValue: Double? = null

    private val mapObjects: List<MapObject>
        get() = if (useMultiDataPoint()) processMultiDataPoints() else processDataPoints()

    private fun isFrameSet(p: DataPointAesthetics): Boolean {
        return p.frame() != AesInitValue[Aes.FRAME]
    }

    private fun isLiveMapWithLonLat(p: DataPointAesthetics): Boolean {
        return LonLatParser.parse(p.mapId().toString()) != null
    }

    private fun getLayerKind(displayMode: LivemapConstants.DisplayMode): MapLayerKind {
        return when (displayMode) {
            LivemapConstants.DisplayMode.POLYGON -> MapLayerKind.POLYGON
            LivemapConstants.DisplayMode.POINT -> MapLayerKind.POINT
            LivemapConstants.DisplayMode.PIE -> MapLayerKind.PIE
            LivemapConstants.DisplayMode.HEATMAP -> MapLayerKind.HEATMAP
            LivemapConstants.DisplayMode.BAR -> MapLayerKind.BAR
            else -> throw IllegalArgumentException("Unknown display mode: $displayMode")
        }
    }

    private fun getSortingMode(layerKind: MapLayerKind): SortingMode {
        return if (layerKind === MapLayerKind.PIE)
            SortingMode.PIE_CHART
        else
            SortingMode.BAR
    }

    private fun getMaxAbsValue(multiDataPoints: List<MultiDataPoint>): Double {
        var maxAbsValue = 0.0
        for (multiDataPoint in multiDataPoints) {
            for (value in multiDataPoint.values) {
                maxAbsValue = max(abs(value), maxAbsValue)
            }
        }
        return maxAbsValue
    }

    init {

        myLayerKind = getLayerKind(liveMapOptions.displayMode)

        myFrameSpecified = allAesMatch(myAesthetics, ::isFrameSet)
        myLonLatInsideMapIdSpecified = allAesMatch(myAesthetics, ::isLiveMapWithLonLat)
    }

    fun heatMapWithFrame(): Boolean {
        return myLayerKind === MapLayerKind.HEATMAP && myFrameSpecified
    }

    fun createMapLayer(): MapLayer {
        return MapLayer(myLayerKind, mapObjects/*, createTooltipAesSpec(GeomKind.LIVE_MAP, myMappedDataAccess)*/)
    }

    private fun processDataPoints(): List<MapObject> {
        val mapObjects = ArrayList<MapObject>(myAesthetics.dataPointCount())
        for (p in myAesthetics.dataPoints()) {
            dataPointToMapObject(p) { mapObjects.add(it) }
        }
        return mapObjects
    }

    private fun processMultiDataPoints(): List<MapObject> {
        val multiDataPoints = MultiDataPointHelper.getPoints(
            myAesthetics,
            getSortingMode(myLayerKind)
        )
        if (myLayerKind === MapLayerKind.BAR) {
            myMaxAbsValue = getMaxAbsValue(multiDataPoints)
        }

        val mapObjects = ArrayList<MapObject>(multiDataPoints.size)
        for (multiDataPoint in multiDataPoints) {
            multiDataPointToMapObject(multiDataPoint) { mapObjects.add(it) }
        }
        return mapObjects
    }

    private fun dataPointToMapObject(p: DataPointAesthetics, consumer: (MapObject) -> Unit) {
        createMapObject(p, MapObjectBuilder(p, myLayerKind, myMapProjection), consumer)
    }

    private fun multiDataPointToMapObject(multiDataPoint: MultiDataPoint, consumer: (MapObject) -> Unit) {
        createMapObject(multiDataPoint.aes,
            MapObjectBuilder(multiDataPoint, myLayerKind, myMapProjection), consumer)
    }

    private fun createMapObject(
        p: DataPointAesthetics,
        mapObjectBuilder: MapObjectBuilder,
        consumer: (MapObject) -> Unit
    ) {
        setGeometryPointIfNeeded(p, mapObjectBuilder)
        setStrokeWidthIfNeeded(mapObjectBuilder)
        setMaxAbsValueIfNeeded(mapObjectBuilder)

        mapObjectBuilder.build(consumer)
    }

    private fun setGeometryPointIfNeeded(p: DataPointAesthetics, mapObjectBuilder: MapObjectBuilder) {
        var lonlat: Vec<LonLat>? = null
        if (myLonLatInsideMapIdSpecified) {
            lonlat = LonLatParser.parse(p.mapId().toString())
        }

        if (lonlat != null) {
            mapObjectBuilder.setGeometryPoint(lonlat)
        }
    }

    private fun setMaxAbsValueIfNeeded(mapObjectBuilder: MapObjectBuilder) {
        if (myMaxAbsValue == null) {
            return
        }

        mapObjectBuilder.setMaxAbsValue(myMaxAbsValue)
    }

    private fun useMultiDataPoint(): Boolean {
        return myLayerKind === MapLayerKind.PIE || myLayerKind === MapLayerKind.BAR
    }

    private fun setStrokeWidthIfNeeded(mapObjectBuilder: MapObjectBuilder) {
        if (myStrokeWidth == null) {
            return
        }

        if (myLayerKind === MapLayerKind.POINT || myLayerKind === MapLayerKind.PIE || myLayerKind === MapLayerKind.BAR) {
            mapObjectBuilder.setStrokeWidth(myStrokeWidth)
        }
    }

    private fun allAesMatch(aes: Aesthetics, matcher: (DataPointAesthetics) -> Boolean): Boolean {
        if (aes.dataPointCount() == 0) {
            return false
        }

        for (p in aes.dataPoints()) {
            if (!matcher(p)) {
                return false
            }
        }
        return true
    }
}