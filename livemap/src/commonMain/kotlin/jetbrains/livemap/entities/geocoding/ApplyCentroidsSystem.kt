/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.api.addScreenOffsetAndDimension
import jetbrains.livemap.api.createLineBBox
import jetbrains.livemap.api.createLineGeometry
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.placement.ScreenOriginComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.TextComponent
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.WorldPoint

class ApplyCentroidsSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    private lateinit var myMapProjection: MapProjection

    override fun initImpl(context: LiveMapContext) {
        myMapProjection = context.mapProjection
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getEntities(CentroidComponent::class)
            .toList()
            .forEach { entity ->
                val point = entity.get<CentroidComponent>().point

                when {
                    entity.contains(LineOrientationComponent::class) -> applyToLine(entity, point)
                    else -> applyToPoint(entity, point)
                }

                entity.remove<CentroidComponent>()
            }
    }

    private fun applyToPoint(entity: EcsEntity, point: WorldPoint) {
        entity.addComponents {
            + WorldOriginComponent(point)
            + ScreenLoopComponent()
            + ScreenOriginComponent()
        }
    }

    private fun applyToLine(entity: EcsEntity, point: WorldPoint) {
        val horizontal = entity.get<LineOrientationComponent>().horizontal
        val strokeWidth = entity.get<StyleComponent>().strokeWidth
        val line = createLineGeometry(point, horizontal, myMapProjection.mapRect)
        val bbox = createLineBBox(point, strokeWidth, horizontal, myMapProjection.mapRect)

        entity.addComponents {
            + WorldOriginComponent(bbox.origin)
            + WorldDimensionComponent(bbox.dimension)
            + WorldGeometryComponent().apply { geometry = line }
            + ScreenLoopComponent()
            + ScreenOriginComponent()
        }
    }
}