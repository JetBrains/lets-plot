/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.spatial.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.gis.tileprotocol.TileLayer
import org.jetbrains.letsPlot.gis.tileprotocol.TileService
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.viewport.CellKey
import jetbrains.livemap.mapengine.viewport.convertCellKeyToQuadKeys

internal class TileDataFetcherImpl(private val myMapProjection: MapProjection, private val myTileService: TileService) :
    TileDataFetcher {

    override fun fetch(cellKey: CellKey): Async<List<TileLayer>> {
        val quadKeys = convertCellKeyToQuadKeys(myMapProjection, cellKey)
        val bbox = calculateBBox(quadKeys) ?: return Asyncs.constant(emptyList())

        val zoom = cellKey.length
        return myTileService.getTileData(bbox, zoom)
    }

    private fun calculateBBox(quadKeys: Set<QuadKey<LonLat>>): Rect<LonLat>? = // TODO: add tests for antimeridians
        BBOX_CALCULATOR
            .geoRectsBBox(
                quadKeys.map(QuadKey<LonLat>::computeRect).map(::convertToGeoRectangle)
            )
}