/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.fragments

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.Fragment
import jetbrains.livemap.containers.LruCache
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE

internal class FragmentCache(mapSize: DoubleVector) {

    private val limit = CACHED_ZOOM_COUNT * calculateCachedSideTileCount(mapSize.x) * calculateCachedSideTileCount(mapSize.y)
    private val cache: LruCache<QuadKey<LonLat>, MutableMap<String, Fragment?>> = LruCache(limit)

    fun contains(mapObjectId: String, quadKey: QuadKey<LonLat>): Boolean {
        return cache[quadKey]?.containsKey(mapObjectId) ?: false
    }

    operator fun get(mapObjectId: String, quadKey: QuadKey<LonLat>): Fragment? {
        return cache[quadKey]?.get(mapObjectId)
    }

    fun putEmpty(mapObjectId: String, quadKey: QuadKey<LonLat>) {
        put(mapObjectId, quadKey, null)
    }

    fun put(mapObjectId: String, quadKey: QuadKey<LonLat>, fragment: Fragment?) {
        cache.getOrPut(quadKey, ::HashMap)[mapObjectId] = fragment
    }

    companion object {
        private const val CACHED_ZOOM_COUNT = 3
        private const val CACHED_VIEW_COUNT = 2

        private fun calculateCachedSideTileCount(sideLength: Double): Int {
            return (CACHED_VIEW_COUNT * sideLength / TILE_PIXEL_SIZE + 1).toInt()
        }
    }
}