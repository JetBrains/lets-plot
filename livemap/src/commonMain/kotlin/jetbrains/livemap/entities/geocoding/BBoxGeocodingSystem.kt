/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.LIMIT
import jetbrains.gis.geoprotocol.GeoRequestBuilder.ExplicitRequestBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.regions.RegionFragmentsComponent
import jetbrains.livemap.entities.regions.RegionIdComponent

class BBoxGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingService: GeocodingService
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val geocodedRegions = getEntities(FRAGMENT_COMPONENTS)
            .toList()

        val regionIds = geocodedRegions
            .map { it.get<RegionIdComponent>().regionId }
            .distinct()

        if (regionIds.isEmpty()) return

        val request = ExplicitRequestBuilder()
            .setIds(regionIds)
            .setFeatures(listOf(LIMIT))
            .build()

        myGeocodingService
            .execute(request)
            .map(::parseBBoxMap)
    }

    private fun parseBBoxMap(features: List<GeocodedFeature>) {
        val bboxById = getGeocodingDataMap(features, GeocodedFeature::limit)

        getEntities(FRAGMENT_COMPONENTS).toList().forEach { entity ->
            bboxById[entity.regionId]?.let { bbox ->
                entity.add(RegionBBoxComponent(bbox))
            }
        }
    }

    companion object {
        val FRAGMENT_COMPONENTS = listOf(
            RegionIdComponent::class,
            RegionFragmentsComponent::class
        )
    }
}