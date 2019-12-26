/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.vector

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.tiles.CellKey
import jetbrains.livemap.tiles.convertCellKeyToQuadKeys

internal class TileDataFetcherImpl(private val myMapProjection: MapProjection, private val myTileService: TileService) :
    TileDataFetcher {

    override fun fetch(cellKey: CellKey): Async<List<TileLayer>> {
        val quadKeys = convertCellKeyToQuadKeys(myMapProjection, cellKey)
        val bbox = calculateBBox(quadKeys)

        val zoom = cellKey.length
        return myTileService.getTileData(bbox, zoom)
    }

    private fun calculateBBox(quadKeys: Set<QuadKey<LonLat>>): Rect<LonLat> = // TODO: add tests for antimeridians
        BBOX_CALCULATOR
            .geoRectsBBox(
                quadKeys.map { convertToGeoRectangle(it.computeRect()) }
            )
}