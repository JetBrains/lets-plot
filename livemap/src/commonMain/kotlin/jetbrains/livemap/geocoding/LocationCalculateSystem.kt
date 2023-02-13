/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geocoding

import jetbrains.datalore.base.typedGeometry.GeometryType.MULTI_POLYGON
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementLocationComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.projections.MapRuler
import jetbrains.livemap.fragment.RegionBBoxComponent
import jetbrains.livemap.geocoding.MapLocationGeocoder.Companion.convertToWorldRects
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

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
                    entity.contains<ChartElementLocationComponent>() -> {
                        with(entity.get<ChartElementLocationComponent>().geometry) {
                            when (type) {
                                MULTI_POLYGON -> mapRuler.calculateBoundingBox(listOfNotNull(multiPolygon.bbox))
                                else -> error("Unsupported geometry: $type")
                            }
                        }
                    }
                    entity.contains<WorldGeometryComponent>() -> {
                        entity.get<WorldGeometryComponent>().geometry
                            ?.let { mapRuler.calculateBoundingBox(listOfNotNull(it.bbox)) }
                            ?: error("Unexpected - no geometry")
                    }
                    entity.contains<WorldOriginComponent>() -> {
                        Rect.XYWH(
                            entity.get<WorldOriginComponent>().origin,
                            entity.tryGet<WorldDimensionComponent>()?.dimension ?: World.ZERO_VEC
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
