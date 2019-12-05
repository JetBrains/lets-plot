/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.LIMIT
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.regions.RegionFragmentsComponent

class BBoxGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingProvider: GeocodingProvider
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val regionIds = getEntities(BBOX_COMPONENTS)
            .map { it.get<RegionIdComponent>().regionId }
            .distinct()
            .toList()

        if (regionIds.isEmpty()) return

        myGeocodingProvider
            .featuresByRegionIds(regionIds, listOf(LIMIT))
            .map(::parseBBoxMap)
    }

    private fun parseBBoxMap(features: Map<String, GeocodedFeature>) {
        getMutableEntities(BBOX_COMPONENTS).forEach { entity ->
            features[entity.get<RegionIdComponent>().regionId]
                ?.limit
                ?.run(::RegionBBoxComponent)
                ?.run(entity::add)
        }
    }

    companion object {
        val BBOX_COMPONENTS = listOf(
            RegionIdComponent::class,
            RegionFragmentsComponent::class
        )
    }
}