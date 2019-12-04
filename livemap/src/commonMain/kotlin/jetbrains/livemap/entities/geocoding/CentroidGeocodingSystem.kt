/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.projectionGeometry.reinterpret
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.CENTROID
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.WorldPoint

class CentroidGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingProvider: GeocodingProvider
) : LiveMapSystem(componentManager) {
    private lateinit var myProject: (LonLatPoint) -> WorldPoint

    override fun initImpl(context: LiveMapContext) {
        myProject = context.mapProjection::project
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val regionIds = getEntities(CENTROID_COMPONENTS)
            .map { it.regionId }
            .distinct()
            .toList()

        if (regionIds.isEmpty()) return

        myGeocodingProvider
            .featuresByRegionIds(regionIds, listOf(CENTROID))
            .map(::parseCentroidMap)
    }

    private fun parseCentroidMap(features: List<GeocodedFeature>) {
        val centroidsById = getGeocodingDataMap(features, GeocodedFeature::centroid)

        getMutableEntities(CENTROID_COMPONENTS).forEach { entity ->
            centroidsById[entity.regionId]?.let { coord ->
                entity.add(LonLatComponent(coord.reinterpret()))
                entity.remove<CentroidComponent>()
            }
        }
    }

    companion object {
        val CENTROID_COMPONENTS = listOf(
            CentroidComponent::class,
            RegionIdComponent::class
        )
    }
}