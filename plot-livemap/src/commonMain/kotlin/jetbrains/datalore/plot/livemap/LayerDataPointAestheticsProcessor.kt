/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayerBuilder
import jetbrains.datalore.plot.livemap.MapLayerKind.*
import jetbrains.livemap.api.LayersBuilder

internal class LayerDataPointAestheticsProcessor(
    private val myGeodesic: Boolean
) {

    internal fun createLayerBuilder(layerIndex: Int, layerData: LiveMapLayerData): LayersBuilder.() -> Unit {
        val dataPointsConverter = DataPointsConverter(layerIndex, layerData.aesthetics, myGeodesic)
        val (layerKind, dataPointLiveMapAesthetics) = when (layerData.geomKind) {
            GeomKind.POINT -> 
                POINT to dataPointsConverter.toPoint(layerData.geom)
            GeomKind.H_LINE -> 
                H_LINE to dataPointsConverter.toHorizontalLine()
            GeomKind.V_LINE -> 
                V_LINE to dataPointsConverter.toVerticalLine()
            GeomKind.SEGMENT -> 
                PATH to dataPointsConverter.toSegment(layerData.geom)
            GeomKind.RECT -> 
                POLYGON to dataPointsConverter.toRect()
            GeomKind.TILE, GeomKind.BIN_2D -> 
                POLYGON to dataPointsConverter.toTile()
            GeomKind.DENSITY2D, GeomKind.CONTOUR, GeomKind.PATH -> 
                PATH to dataPointsConverter.toPath(layerData.geom)
            GeomKind.TEXT -> 
                TEXT to dataPointsConverter.toText()
            GeomKind.DENSITY2DF, GeomKind.CONTOURF, GeomKind.POLYGON, GeomKind.MAP -> 
                POLYGON to dataPointsConverter.toPolygon()
            else -> 
                throw IllegalArgumentException("Layer '" + layerData.geomKind.name + "' is not supported on Live Map.")
        }

        return createLayerBuilder(layerKind, dataPointLiveMapAesthetics, layerData.mappedAes)
    }
}
