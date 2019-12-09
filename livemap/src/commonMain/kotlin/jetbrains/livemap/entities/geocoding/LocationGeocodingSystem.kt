/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.POSITION
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager

class LocationGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingProvider: GeocodingProvider,
    private val myNeedLocation: Boolean
) : LiveMapSystem(componentManager) {
    
    private val myLocation = LocationComponent()

    override fun initImpl(context: LiveMapContext) {
        createEntity("LocationSingleton").add(myLocation)
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val entities = getMutableEntities(NEED_LOCATION)

        if (entities.isEmpty()) return

        if (myNeedLocation) {
            val regionIds = entities
                .map { it.get<RegionIdComponent>().regionId }
                .distinct()

            myLocation.wait(regionIds.size)

            myGeocodingProvider
                .featuresByRegionIds(regionIds, listOf(POSITION))
                .map(::parseLocationMap)
        } else {
            entities.forEach {
                it.remove<NeedGeocodeLocationComponent>()
            }
        }
    }

    private fun parseLocationMap(features: Map<String, GeocodedFeature>) {

        getMutableEntities(NEED_LOCATION).forEach { entity ->
            features[entity.get<RegionIdComponent>().regionId]
                ?.position
                ?.let { rect ->
                    myLocation.add(rect)
                    entity.remove<NeedGeocodeLocationComponent>()
                }
        }
    }

    companion object {
        val NEED_LOCATION = listOf(
            RegionIdComponent::class,
            NeedGeocodeLocationComponent::class
        )
    }
}