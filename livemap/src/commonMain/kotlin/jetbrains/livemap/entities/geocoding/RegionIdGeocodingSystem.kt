/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeoRequestBuilder.GeocodingRequestBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.entities.regions.MapIdComponent
import jetbrains.livemap.entities.regions.RegionIdComponent

class RegionIdGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingService: GeocodingService,
    private val myFeatureLevel: FeatureLevel?,
    private val myParent: MapRegion?
) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        val requested = getEntities<MapIdComponent>()
            .filterNot { it.contains<WaitingGeocodingComponent>() }

        val names = requested
            .map { it.get<MapIdComponent>().mapId }
            .distinct()
            .toList()

        if (names.isEmpty()) return

        val request = GeocodingRequestBuilder()
            .setLevel(myFeatureLevel)
            .addQuery(
                GeoRequestBuilder.RegionQueryBuilder()
                    .setQueryNames(names)
                    .setParent(myParent)
                    .build()
            )
            .build()

        requested.forEach { it.addComponents { + WaitingGeocodingComponent() } }

        myGeocodingService
            .execute(request)
            .map {
                getGeocodingDataMap(it, GeocodedFeature::id).let { regionIds ->
                    getEntities(GEOCODED_FEATURE_COMPONENTS).toList().forEach { entity ->
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