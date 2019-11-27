/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.LIMIT
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.regions.EmptinessChecker.BBoxEmptinessChecker
import jetbrains.livemap.entities.regions.RegionFragmentsComponent
import jetbrains.livemap.entities.regions.RegionIdComponent

class BBoxGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingService: GeocodingService,
    private val myEmptinessChecker: BBoxEmptinessChecker
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val geocodedRegions = getEntities(FRAGMENT_COMPONENTS)
            .filter { it.get<RegionIdComponent>().regionId != null }
            .toList()

        val regionIds = geocodedRegions
            .mapNotNull { it.get<RegionIdComponent>().regionId }
            .toSet()
            .toList()

        // apply geocoded regionIds to RegionFragmentsComponents
        geocodedRegions
            .forEach {
                it.get<RegionFragmentsComponent>().id = it.get<RegionIdComponent>().regionId
                it.removeComponent(RegionIdComponent::class)
            }

        if (regionIds.isNotEmpty()) {
            val request = GeoRequestBuilder.ExplicitRequestBuilder()
                .setIds(regionIds)
                .setFeatures(FEATURE_OPTIONS)
                .build()

            myGeocodingService
                .execute(request)
                .map(::parseBBoxMap)
        }
    }

    private fun parseBBoxMap(features: List<GeocodedFeature>) {
        getGeocodingDataMap(features, GeocodedFeature::limit)
            .forEach { (regionId, limit) ->
                limit?.let {
                    myEmptinessChecker.addBbox(regionId, it)
                }
            }
    }

    companion object {
        val FEATURE_OPTIONS = listOf(LIMIT)

        val FRAGMENT_COMPONENTS = listOf(
            RegionIdComponent::class,
            RegionFragmentsComponent::class
        )
    }
}