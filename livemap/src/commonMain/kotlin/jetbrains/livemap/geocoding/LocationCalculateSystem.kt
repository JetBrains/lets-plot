/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geocoding

import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.limit
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.projections.MapRuler
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.placement.WorldDimensionComponent
import jetbrains.livemap.placement.WorldOriginComponent
import jetbrains.livemap.projection.Coordinates.ZERO_WORLD_POINT
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.projection.World
import jetbrains.livemap.regions.RegionBBoxComponent
import jetbrains.livemap.services.MapLocationGeocoder.Companion.convertToWorldRects

class LocationCalculateSystem(
    private val mapRuler: MapRuler<World>,
    private val mapProjection: MapProjection,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myLocation: LocationComponent

    override fun initImpl(context: LiveMapContext) {
        myLocation = getSingleton()
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getMutableEntities(READY_CALCULATE)
            .forEach { entity ->
                when {
                    entity.contains<WorldGeometryComponent>() -> {
                        entity.get<WorldGeometryComponent>().geometry
                            ?.let { mapRuler.calculateBoundingBox(it.limit()) }
                            ?: error("Unexpected - no geometry")
                    }
                    entity.contains<WorldOriginComponent>() -> {
                        Rect(
                            entity.get<WorldOriginComponent>().origin,
                            entity.tryGet<WorldDimensionComponent>()?.dimension ?: ZERO_WORLD_POINT
                        )
                    }
                    entity.contains<RegionBBoxComponent>() -> {
                        val worldRects = entity.get<RegionBBoxComponent>().bbox.convertToWorldRects(mapProjection)
                        mapRuler.calculateBoundingBox(worldRects)
                    }
                    else -> null
                }?.let { entityLocation ->
                    myLocation.add(entityLocation)
                    entity.remove<NeedCalculateLocationComponent>()
                }
            }
    }

    companion object {
        val READY_CALCULATE = listOf(
            NeedCalculateLocationComponent::class
        )
    }
}
