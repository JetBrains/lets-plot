/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager

class RegionIdGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingProvider: GeocodingProvider
) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        val requested = getEntities<MapIdComponent>()
            .filterNot { it.contains<WaitingGeocodingComponent>() }

        val names = requested
            .map { it.get<MapIdComponent>().mapId }
            .distinct()
            .toList()

        if (names.isEmpty()) return

        requested.forEach { it.add(WaitingGeocodingComponent()) }

        myGeocodingProvider.getRegionsIdByNames(names)
            .map {
                getGeocodingDataMap(it, GeocodedFeature::id).let { regionIds ->
                    getMutableEntities(GEOCODED_FEATURE_COMPONENTS).forEach { entity ->
                        entity.add(RegionIdComponent(regionIds[entity.get<MapIdComponent>().mapId]!!))
                        entity.remove<MapIdComponent>()
                    }
                }
            }
    }

    companion object {
        val GEOCODED_FEATURE_COMPONENTS = listOf(
            MapIdComponent::class,
            WaitingGeocodingComponent::class
        )
    }
}