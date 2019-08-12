package jetbrains.livemap.tilegeometry

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE

internal open class TileGeometryCache(mapSize: DoubleVector) {

    private val myGeometryMap: MutableMap<QuadKey, MutableMap<String, GeoTile?>>

    init {
        val cacheLimit =
            CACHED_ZOOM_COUNT * calculateCachedSideTileCount(mapSize.x) * calculateCachedSideTileCount(mapSize.y)
        myGeometryMap = createCacheMap(cacheLimit)
    }

    fun contains(mapObjectId: String, tileId: QuadKey): Boolean {
        return myGeometryMap[tileId]?.containsKey(mapObjectId) ?: false
    }

    operator fun get(mapObjectId: String, tileId: QuadKey): GeoTile? {
        return myGeometryMap[tileId]?.get(mapObjectId)
    }

    fun putEmpty(mapObjectId: String, tileId: QuadKey) {

        put(mapObjectId, tileId, null)
    }

    fun put(mapObjectId: String, tileId: QuadKey, tile: GeoTile?) {
        myGeometryMap.getOrPut(tileId, ::HashMap)[mapObjectId] = tile
    }

    companion object {
        private const val CACHED_ZOOM_COUNT = 3
        private const val CACHED_VIEW_COUNT = 2

        private fun calculateCachedSideTileCount(sideLength: Double): Int {
            return (CACHED_VIEW_COUNT * sideLength / TILE_PIXEL_SIZE + 1).toInt()
        }

        private fun <K, V> createCacheMap(limit: Int): MutableMap<K, V> {
            return LinkedHashMap(limit, 0.75f) // todo: check LinkedHashMap implementation
        }
    }
}