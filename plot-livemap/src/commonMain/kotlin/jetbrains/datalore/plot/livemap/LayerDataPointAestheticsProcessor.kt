/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayersBuilderBlock
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.mapobjects.MapLayerKind

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
                mapObjectBuilders = dataPointsConverter.toPoint(layerData.geom)
                layerKind = MapLayerKind.POINT
            }

            H_LINE -> {
                mapObjectBuilders = dataPointsConverter.toHorizontalLine()
                layerKind = MapLayerKind.H_LINE
            }

            V_LINE -> {
                mapObjectBuilders = dataPointsConverter.toVerticalLine()
                layerKind = MapLayerKind.V_LINE
            }

            SEGMENT -> {
                mapObjectBuilders = dataPointsConverter.toSegment(layerData.geom)
                layerKind = MapLayerKind.PATH
            }

            RECT -> {
                mapObjectBuilders = dataPointsConverter.toRect()
                layerKind = MapLayerKind.POLYGON
            }

            TILE -> {
                mapObjectBuilders = dataPointsConverter.toTile()
                layerKind = MapLayerKind.POLYGON
            }

            DENSITY2D, CONTOUR, PATH -> {
                mapObjectBuilders = dataPointsConverter.toPath(layerData.geom)
                layerKind = MapLayerKind.PATH
            }

            TEXT -> {
                mapObjectBuilders = dataPointsConverter.toText()
                layerKind = MapLayerKind.TEXT
            }

            DENSITY2DF, CONTOURF, POLYGON -> {
                mapObjectBuilders = dataPointsConverter.toPolygon()
                layerKind = MapLayerKind.POLYGON
            }

            else -> throw IllegalArgumentException("Layer '" + geomKind.name + "' is not supported on Live Map.")
        }

        return createLayersBuilderBlock(layerKind, mapObjectBuilders)
    }
}
