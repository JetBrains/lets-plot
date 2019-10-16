package jetbrains.livemap.geom

import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection

internal class LayerDataPointAestheticsProcessor(
    private val myMapProjection: MapProjection,
    private val myGeodesic: Boolean
) {

    fun createMapLayer(layerData: LiveMapLayerData): MapLayer? {
        val geomKind = layerData.geomKind

//        if (isDebugLogEnabled()) {
//            debugLog("Geom Kind: $geomKind")
//        }

        val aesthetics = layerData.aesthetics

        val dataPointsConverter = DataPointsConverter(aesthetics, myMapProjection, myGeodesic)

        val mapObjects: List<MapObject>
        val layerKind: MapLayerKind
        when (geomKind) {
            POINT -> {
                mapObjects = dataPointsConverter.toPoint(layerData.geom)
                layerKind = MapLayerKind.POINT
            }

            H_LINE -> {
                mapObjects = dataPointsConverter.toHorizontalLine()
                layerKind = MapLayerKind.H_LINE
            }

            V_LINE -> {
                mapObjects = dataPointsConverter.toVerticalLine()
                layerKind = MapLayerKind.V_LINE
            }

            SEGMENT -> {
                mapObjects = dataPointsConverter.toSegment(layerData.geom)
                layerKind = MapLayerKind.PATH
            }

            RECT -> {
                mapObjects = dataPointsConverter.toRect()
                layerKind = MapLayerKind.POLYGON
            }

            TILE -> {
                mapObjects = dataPointsConverter.toTile()
                layerKind = MapLayerKind.POLYGON
            }

            DENSITY2D, CONTOUR, PATH -> {
                mapObjects = dataPointsConverter.toPath(layerData.geom)
                layerKind = MapLayerKind.PATH
            }

            TEXT -> {
                mapObjects = dataPointsConverter.toText()
                layerKind = MapLayerKind.TEXT
            }

            DENSITY2DF, CONTOURF, POLYGON -> {
                mapObjects = dataPointsConverter.toPolygon()
                layerKind = MapLayerKind.POLYGON
            }

            else -> throw IllegalArgumentException("Layer '" + geomKind.name + "' is not supported on Live Map.")
        }

        return if (aesthetics.dataPointCount() == 0) {
            null
        } else MapLayer(layerKind, mapObjects/*, createTooltipAesSpec(geomKind, layerData.getDataAccess())*/)

    }
}
