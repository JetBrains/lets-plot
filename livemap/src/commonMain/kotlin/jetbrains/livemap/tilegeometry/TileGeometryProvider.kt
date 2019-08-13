package jetbrains.livemap.tilegeometry

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.gis.geoprotocol.GeocodingService

interface TileGeometryProvider {

    fun getGeometries(mapObjectIds: List<String>, tileIds: Collection<QuadKey>): Async<Map<String, List<GeoTile>>>

    companion object {
        fun create(geocodingService: GeocodingService, mapSize: DoubleVector): TileGeometryProvider {
            return TileGeometryProviderImpl(TileGeometryCache(mapSize), geocodingService)
        }
    }
}