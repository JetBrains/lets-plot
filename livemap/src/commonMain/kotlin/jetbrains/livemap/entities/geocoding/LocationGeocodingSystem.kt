/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.POSITION
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.MapLocationGeocoder.Companion.convertToWorldRects
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projections.MapProjection

class LocationGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingProvider: GeocodingProvider
) : AbstractSystem<LiveMapContext>(componentManager) {

    private lateinit var myLocation: LocationComponent
    private lateinit var myMapProjection: MapProjection

    override fun initImpl(context: LiveMapContext) {
        myLocation = getSingleton()
        myMapProjection = context.mapProjection

    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val entities = getMutableEntities(NEED_LOCATION)

        if (entities.isEmpty()) return

        val regionIds = entities
            .map { it.get<RegionIdComponent>().regionId }
            .distinct()

        myGeocodingProvider
            .featuresByRegionIds(regionIds, listOf(POSITION))
            .map(::parseLocationMap)

        entities.forEach {
            it.add(WaitGeocodeLocationComponent())
            it.remove<NeedGeocodeLocationComponent>()
        }
    }

    private fun parseLocationMap(features: Map<String, GeocodedFeature>) {

        getMutableEntities(WAIT_LOCATION).forEach { entity ->
            features[entity.get<RegionIdComponent>().regionId]
                ?.position
                ?.let { rect ->
                    rect
                        .convertToWorldRects(myMapProjection)
                        .forEach(myLocation::add)
                    entity.remove<WaitGeocodeLocationComponent>()
                }
        }
    }

    companion object {
        val NEED_LOCATION = listOf(
            RegionIdComponent::class,
            NeedGeocodeLocationComponent::class
        )

        val WAIT_LOCATION = listOf(
            RegionIdComponent::class,
            WaitGeocodeLocationComponent::class
        )
    }
}