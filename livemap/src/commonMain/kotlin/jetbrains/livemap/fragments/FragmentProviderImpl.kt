/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.fragments

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.Fragment
import jetbrains.gis.geoprotocol.GeocodingService

internal class FragmentProviderImpl(
    private val fragmentCache: FragmentCache,
    private val geocodingService: GeocodingService
) : FragmentProvider {

    override fun getFragments(mapObjectIds: List<String>, quads: Collection<QuadKey<LonLat>>): Async<Map<String, List<Fragment>>> {
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
}