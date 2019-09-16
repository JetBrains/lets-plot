package jetbrains.livemap.tiles

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.projectionGeometry.GeoUtils.BBOX_CALCULATOR
import jetbrains.datalore.base.projectionGeometry.GeoUtils.convertToGeoRectangle
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getQuadKeyRect
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil.convertCellKeyToQuadKeys

internal class TileDataFetcherImpl(private val myMapProjection: MapProjection, private val myTileService: TileService) :
    TileDataFetcher {

    override fun fetch(cellKey: CellKey): Async<List<TileLayer>> {
        val quadKeys = convertCellKeyToQuadKeys(myMapProjection, cellKey)
        val bbox = calculateBBox(quadKeys)

        val zoom = cellKey.length
        return myTileService.getTileData(bbox, zoom)
    }

    private fun calculateBBox(quadKeys: Set<QuadKey>): Rect<LonLat> = // TODO: add tests for antimeridians
        BBOX_CALCULATOR
            .calculateBoundingBoxFromGeoRectangles(
                quadKeys.map { convertToGeoRectangle(getQuadKeyRect(it)) }
            )
}