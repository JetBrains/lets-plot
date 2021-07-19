/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geocoding

import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.LIMIT
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.regions.RegionFragmentsComponent

class BBoxGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingService: GeocodingService
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val entities = getMutableEntities(NEED_BBOX)

        if (entities.isEmpty()) return

        val regionIds = entities
            .map { it.get<RegionIdComponent>().regionId }
            .distinct()

        GeoRequestBuilder.ExplicitRequestBuilder()
            .setIds(regionIds)
            .setFeatures(listOf(LIMIT))
            .build()
            .run(myGeocodingService::execute)
            .map { features -> regionIds.zip(features).associate { (id, feature) -> id to feature } }
            .map(::parseBBoxMap)

        entities.forEach {
            it.add(WaitBboxComponent)
            it.remove<NeedBboxComponent>()
        }
    }

    private fun parseBBoxMap(features: Map<String, GeocodedFeature>) {
        getMutableEntities(WAIT_BBOX).forEach { entity ->
            features[entity.get<RegionIdComponent>().regionId]
                ?.limit
                ?.let {
                    entity.add(RegionBBoxComponent(it))
                    entity.remove<WaitBboxComponent>()
                }
        }
    }

    companion object {
        val NEED_BBOX = listOf(
            RegionIdComponent::class,
            NeedBboxComponent::class
        )

        val WAIT_BBOX = listOf(
            RegionIdComponent::class,
            WaitBboxComponent::class,
            RegionFragmentsComponent::class
        )
    }
}
