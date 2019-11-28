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
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.entities.regions.RegionIdComponent
import jetbrains.livemap.projections.MapProjection

class PointGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingService: GeocodingService
    ) : LiveMapSystem(componentManager) {
    private lateinit var myMapProjection: MapProjection

    override fun initImpl(context: LiveMapContext) {
        myMapProjection = context.mapProjection
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val regionIds = getEntities(POINT_COMPONENTS)
            .mapNotNull { it.get<RegionIdComponent>().regionId }
            .toSet()
            .toList()

        if (regionIds.isNotEmpty()) {
            val request = ExplicitRequestBuilder()
                .setIds(regionIds)
                .setFeatures(FEATURE_OPTIONS)
                .build()

            myGeocodingService
                .execute(request)
                .map(::parseCentroidMap)
        }
    }

    private fun parseCentroidMap(features: List<GeocodedFeature>) {
        val centroidsById = getGeocodingDataMap(features, GeocodedFeature::centroid)

        getEntities(POINT_COMPONENTS).toList().forEach { entity ->
            entity.get<RegionIdComponent>().regionId?.let { regionId ->
                centroidsById[regionId]?.let { coord ->
                    entity.removeComponent(RegionIdComponent::class)
                    entity.addComponents { + CentroidComponent(myMapProjection.project(coord.reinterpret())) }
                }
            }
        }
    }

    companion object {
        val FEATURE_OPTIONS = listOf(CENTROID)

        val POINT_COMPONENTS = listOf(
            PointTag::class,
            RegionIdComponent::class
        )
    }
}