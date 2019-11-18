/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.fragments

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.livemap.containers.LruCache
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE

internal open class FragmentCache(mapSize: DoubleVector) {

    private val limit = CACHED_ZOOM_COUNT * calculateCachedSideTileCount(mapSize.x) * calculateCachedSideTileCount(mapSize.y)
    private val cache: LruCache<QuadKey, MutableMap<String, GeoTile?>> = LruCache(limit)

    fun contains(mapObjectId: String, tileId: QuadKey): Boolean {
        return cache[tileId]?.containsKey(mapObjectId) ?: false
    }

    operator fun get(mapObjectId: String, tileId: QuadKey): GeoTile? {
        return cache[tileId]?.get(mapObjectId)
    }

    fun putEmpty(mapObjectId: String, tileId: QuadKey) {
        put(mapObjectId, tileId, null)
    }

    fun put(mapObjectId: String, tileId: QuadKey, tile: GeoTile?) {
        cache.getOrPut(tileId, ::HashMap)[mapObjectId] = tile
    }

    companion object {
        private const val CACHED_ZOOM_COUNT = 3
        private const val CACHED_VIEW_COUNT = 2

        private fun calculateCachedSideTileCount(sideLength: Double): Int {
            return (CACHED_VIEW_COUNT * sideLength / TILE_PIXEL_SIZE + 1).toInt()
        }
    }
}