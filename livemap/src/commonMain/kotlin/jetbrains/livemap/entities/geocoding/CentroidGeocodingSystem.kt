/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.projectionGeometry.reinterpret
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.CENTROID
import jetbrains.gis.geoprotocol.GeoRequestBuilder.ExplicitRequestBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.regions.RegionIdComponent
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.WorldPoint

class CentroidGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingService: GeocodingService
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

        val request = ExplicitRequestBuilder()
            .setIds(regionIds)
            .setFeatures(listOf(CENTROID))
            .build()

        myGeocodingService
            .execute(request)
            .map(::parseCentroidMap)
    }

    private fun parseCentroidMap(features: List<GeocodedFeature>) {
        val centroidsById = getGeocodingDataMap(features, GeocodedFeature::centroid)

        getEntities(CENTROID_COMPONENTS).toList().forEach { entity ->
            centroidsById[entity.regionId]?.let { coord ->
                entity.add(CentroidComponent(myProject(coord.reinterpret())))
                entity.remove<CentroidTag>()
            }
        }
    }

    companion object {
        val CENTROID_COMPONENTS = listOf(
            CentroidTag::class,
            RegionIdComponent::class
        )
    }
}