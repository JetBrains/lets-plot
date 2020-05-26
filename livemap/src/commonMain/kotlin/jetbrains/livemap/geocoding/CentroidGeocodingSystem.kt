/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geocoding

import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.reinterpret
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption.CENTROID
import jetbrains.gis.geoprotocol.GeoRequestBuilder
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projection.WorldPoint

class CentroidGeocodingSystem(
    componentManager: EcsComponentManager,
    private val myGeocodingService: GeocodingService
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myProject: (LonLatPoint) -> WorldPoint

    override fun initImpl(context: LiveMapContext) {
        myProject = context.mapProjection::project
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val entities = getMutableEntities(NEED_CENTROID)

        if (entities.isEmpty()) return

        val regionIds = entities
            .map { it.get<RegionIdComponent>().regionId }
            .distinct()

        GeoRequestBuilder.ExplicitRequestBuilder()
            .setIds(regionIds)
            .setFeatures(listOf(CENTROID))
            .build()
            .run(myGeocodingService::execute)
            .map { features -> features.associateBy(GeocodedFeature::request) { it } }
            .map(::parseCentroidMap)

        entities.forEach {
            it.add(WaitCentroidComponent)
            it.remove<NeedCentroidComponent>()
        }
    }

    private fun parseCentroidMap(features: Map<String, GeocodedFeature>) {

        getMutableEntities(WAIT_CENTROID).forEach { entity ->
            features[entity.get<RegionIdComponent>().regionId]
                ?.centroid
                ?.let { coord ->
                    entity.add(LonLatComponent(coord.reinterpret()))
                    entity.remove<WaitCentroidComponent>()
                }
        }
    }

    companion object {
        val NEED_CENTROID = listOf(
            NeedCentroidComponent::class,
            RegionIdComponent::class
        )

        val WAIT_CENTROID = listOf(
            WaitCentroidComponent::class,
            RegionIdComponent::class
        )
    }
}