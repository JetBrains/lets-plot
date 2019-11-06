/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayersBuilderBlock
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.mapobjects.MapObject

internal class LayerDataPointAestheticsProcessor(
    private val myGeodesic: Boolean
) {

    internal fun createBlock(layerData: LiveMapLayerData): LayersBuilder.() -> Unit {
        val geomKind = layerData.geomKind

//        if (isDebugLogEnabled()) {
//            debugLog("Geom Kind: $geomKind")
//        }

        val aesthetics = layerData.aesthetics

        val dataPointsConverter = DataPointsConverter(aesthetics, myGeodesic)

        val mapObjectBuilders: List<MapObjectBuilder>
        val layerKind: MapLayerKind
        when (geomKind) {
            POINT -> {
                mapObjectBuilders = dataPointsConverter.toPoint2(layerData.geom)
                layerKind = MapLayerKind.POINT
            }

            H_LINE -> {
                mapObjectBuilders = dataPointsConverter.toHorizontalLine2()
                layerKind = MapLayerKind.H_LINE
            }

            V_LINE -> {
                mapObjectBuilders = dataPointsConverter.toVerticalLine2()
                layerKind = MapLayerKind.V_LINE
            }

            SEGMENT -> {
                mapObjectBuilders = dataPointsConverter.toSegment2(layerData.geom)
                layerKind = MapLayerKind.PATH
            }

            RECT -> {
                mapObjectBuilders = dataPointsConverter.toRect2()
                layerKind = MapLayerKind.POLYGON
            }

            TILE -> {
                mapObjectBuilders = dataPointsConverter.toTile2()
                layerKind = MapLayerKind.POLYGON
            }

            DENSITY2D, CONTOUR, PATH -> {
                mapObjectBuilders = dataPointsConverter.toPath2(layerData.geom)
                layerKind = MapLayerKind.PATH
            }

            TEXT -> {
                mapObjectBuilders = dataPointsConverter.toText2()
                layerKind = MapLayerKind.TEXT
            }

            DENSITY2DF, CONTOURF, POLYGON -> {
                mapObjectBuilders = dataPointsConverter.toPolygon2()
                layerKind = MapLayerKind.POLYGON
            }

            else -> throw IllegalArgumentException("Layer '" + geomKind.name + "' is not supported on Live Map.")
        }

        return createLayersBuilderBlock(layerKind, mapObjectBuilders)
    }

    fun createMapLayer(layerData: LiveMapLayerData): MapLayer? {
        val geomKind = layerData.geomKind

//        if (isDebugLogEnabled()) {
//            debugLog("Geom Kind: $geomKind")
//        }

        val aesthetics = layerData.aesthetics

        val dataPointsConverter = DataPointsConverter(aesthetics, myGeodesic)

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
