package jetbrains.livemap.tilegeometry

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.gis.geoprotocol.GeocodingService

internal class TileGeometryProviderImpl(
    private val tileGeometryCache: TileGeometryCache,
    private val geocodingService: GeocodingService
) : TileGeometryProvider {

    override fun getGeometries(mapObjectIds: List<String>, tileIds: Collection<QuadKey>): Async<Map<String, List<GeoTile>>> {
        val objectsWithMissingTiles = HashMap<String, List<QuadKey>>()

        var isMissing = false
        for (mapObjectId in mapObjectIds) {
            val missingTiles = ArrayList<QuadKey>()
            for (tileId in tileIds) {
                if (!tileGeometryCache.contains(mapObjectId, tileId)) {
                    missingTiles.add(tileId)
                    isMissing = true
                }
            }
            if (!missingTiles.isEmpty()) {
                objectsWithMissingTiles[mapObjectId] = missingTiles
            }
        }

        if (!isMissing) {
            return Asyncs.constant(getCachedGeometries(mapObjectIds, tileIds))
        }

        val request = GeoRequestBuilder.ExplicitRequestBuilder()
            .setIds(mapObjectIds)
            .addFeature(GeoRequest.FeatureOption.TILES)
            .setTiles(objectsWithMissingTiles)
            .build()

        return geocodingService
            .execute(request)
            .map { features ->
                tileIds.forEach { tileId ->
                    mapObjectIds.forEach { mapObjectId ->
                        if (!tileGeometryCache.contains(mapObjectId, tileId)) {
                            tileGeometryCache.putEmpty(mapObjectId, tileId)
                        }
                    }
                }

                features.forEach { geocodedFeature ->
                    geocodedFeature.tiles?.forEach {
                        tileGeometryCache.put(geocodedFeature.id, it.key, it)
                    }
                }
                getCachedGeometries(mapObjectIds, tileIds)
            }
    }

    private fun getCachedGeometries(
        mapObjectIds: List<String>,
        tileIds: Collection<QuadKey>
    ): Map<String, List<GeoTile>> {
        val result = HashMap<String, List<GeoTile>>()

        mapObjectIds.forEach { mapObjectId ->
            val tiles = ArrayList<GeoTile>()
            tileIds.forEach { tileId ->
                tileGeometryCache[mapObjectId, tileId]?.let(tiles::add)
            }
            result[mapObjectId] = tiles
        }

        return result
    }
}