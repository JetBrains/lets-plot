/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager

class RegionIdGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingProvider: GeocodingProvider
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        val requested = getMutableEntities<MapIdComponent>()
            .filterNot { it.contains<WaitingGeocodingComponent>() }

        requested.forEach {
            val mapId = it.get<MapIdComponent>().mapId
            if (isMapIdWithOsmId(mapId)) {
                it.add(RegionIdComponent(mapId))
                it.remove<MapIdComponent>()
            }
        }

        val names = requested
            .filter { it.contains<MapIdComponent>() }
            .map { it.get<MapIdComponent>().mapId }
            .distinct()
            .toList()

        if (names.isEmpty()) return

        requested.forEach { it.add(WaitingGeocodingComponent()) }

        myGeocodingProvider.geocodeRegions(names)
            .apply {
                onSuccess { regionIdByNames ->
                    getMutableEntities(GEOCODED_FEATURE_COMPONENTS).forEach { entity ->
                        entity.add(RegionIdComponent(regionIdByNames.getValue(entity.get<MapIdComponent>().mapId)))
                        entity.remove<MapIdComponent>()
                    }
                }
                onFailure { context.showError(it) }
            }
    }

    companion object {
        val GEOCODED_FEATURE_COMPONENTS = listOf(
            MapIdComponent::class,
            WaitingGeocodingComponent::class
        )

        fun isMapIdWithOsmId(mapId: String): Boolean {
            return mapId.toIntOrNull() != null
        }
    }
}