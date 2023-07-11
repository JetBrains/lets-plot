/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.fragment

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.Fragment
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.livemap.config.TILE_PIXEL_SIZE
import jetbrains.livemap.containers.LruCache

fun newFragmentProvider(geocodingService: GeocodingService, mapSize: DoubleVector): FragmentProvider {
    return FragmentProvider(
        FragmentProvider.FragmentCache(
            mapSize
        ), geocodingService
    )
}

// open for tests
open class FragmentProvider internal constructor (
    private val fragmentCache: FragmentCache,
    private val geocodingService: GeocodingService
) {

    open fun getFragments(mapObjectIds: List<String>, quads: Collection<QuadKey<LonLat>>): Async<Map<String, List<Fragment>>> {
        if (/*Random.nextBoolean()*/false) {
            return Asyncs.failure(RuntimeException("Test error response"))
        } else {
            val objectsWithMissingFragments = HashMap<String, List<QuadKey<LonLat>>>()

            var isMissing = false
            for (mapObjectId in mapObjectIds) {
                val missingFragments = ArrayList<QuadKey<LonLat>>()
                for (quadKey in quads) {
                    if (!fragmentCache.contains(mapObjectId, quadKey)) {
                        missingFragments.add(quadKey)
                        isMissing = true
                    }
                }
                if (!missingFragments.isEmpty()) {
                    objectsWithMissingFragments[mapObjectId] = missingFragments
                }
            }

            if (!isMissing) {
                return Asyncs.constant(getCachedGeometries(mapObjectIds, quads))
            }

            val request = GeoRequestBuilder.ExplicitRequestBuilder()
                .setIds(mapObjectIds)
                .addFeature(GeoRequest.FeatureOption.FRAGMENTS)
                .setFragments(objectsWithMissingFragments)
                .build()

            return geocodingService
                .execute(request)
                .map { features ->
                    quads.forEach { quadKey ->
                        mapObjectIds.forEach { mapObjectId ->
                            if (!fragmentCache.contains(mapObjectId, quadKey)) {
                                fragmentCache.putEmpty(mapObjectId, quadKey)
                            }
                        }
                    }

                    features.forEach { geocodedFeature ->
                        geocodedFeature.fragments?.forEach {
                            fragmentCache.put(geocodedFeature.id, it.key, it)
                        }
                    }
                    getCachedGeometries(mapObjectIds, quads)
                }
        }
    }

    private fun getCachedGeometries(
        mapObjectIds: List<String>,
        quads: Collection<QuadKey<LonLat>>
    ): Map<String, List<Fragment>> {
        val result = HashMap<String, List<Fragment>>()

        mapObjectIds.forEach { mapObjectId ->
            val fragments = ArrayList<Fragment>()
            quads.forEach { quadKey ->
                fragmentCache[mapObjectId, quadKey]?.let(fragments::add)
            }
            result[mapObjectId] = fragments
        }

        return result
    }

    internal class FragmentCache(mapSize: DoubleVector) {

        private val limit = CACHED_ZOOM_COUNT * calculateCachedSideTileCount(
            mapSize.x
        ) * calculateCachedSideTileCount(
            mapSize.y
        )
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
}