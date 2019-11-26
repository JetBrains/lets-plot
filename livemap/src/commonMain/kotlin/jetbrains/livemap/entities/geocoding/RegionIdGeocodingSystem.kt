/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.*
import jetbrains.gis.geoprotocol.GeoRequestBuilder.GeocodingRequestBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
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
        val all = getEntities<MapIdComponent>()
        val requested = all.filter { it.tryGet<RegionIdComponent>() == null }

        val names = requested.map { it.get<MapIdComponent>().mapId }.toSet().toList()

        if (names.isNotEmpty()) {
            // do request
            val request = GeocodingRequestBuilder()
                .setLevel(myFeatureLevel)
                .addQuery(
                    GeoRequestBuilder.RegionQueryBuilder()
                        .setQueryNames(names)
                        .setParent(myParent)
                        .build()
                )
                .build()

            requested.forEach { it.addComponents { + RegionIdComponent() } }

            myGeocodingService
                .execute(request)
                .map {
                    getGeocodingDataMap(it, GeocodedFeature::id).let { regionIds ->
                        getEntities(GEOCODED_FEATURE_COMPONENTS).forEach { entity ->
                            entity.get<RegionIdComponent>().regionId = regionIds[entity.get<MapIdComponent>().mapId]
                            entity.removeComponent(MapIdComponent::class)
                        }
                    }
                }
        }
    }

    private fun <T> getGeocodingDataMap(
        features: List<GeocodedFeature>,
        getData: (GeocodedFeature) -> T
    ): MutableMap<String, T> {
        val dataMap = HashMap<String, T>(features.size)
        for (feature in features) {
            dataMap[feature.request] = getData(feature)
        }
        return dataMap
    }

    companion object {
        val GEOCODED_FEATURE_COMPONENTS = listOf(
            MapIdComponent::class,
            RegionIdComponent::class
        )
    }
}