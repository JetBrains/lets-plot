/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.camera.isIntegerZoom
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.map
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent.Companion.tagDirtyParentLayer
import jetbrains.livemap.entities.placement.ScreenOriginComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.entities.scaling.ScaleComponent
import jetbrains.livemap.projections.WorldProjection


class WorldGeometry2ScreenUpdateSystem(
    private val myQuantIterations: Int,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    private fun createScalingTask(entity: EcsEntity, zoom: Int): MicroTask<Unit> {

        // Fix ghosting after zoom by removing outdated screen geometry.
        if (!entity.contains(ScaleComponent::class)) {
            entity.remove<ScreenGeometryComponent>()
        }

        val worldOrigin = entity.get<WorldOriginComponent>().origin
        val zoomProjection = WorldProjection(zoom)
        return GeometryTransform
            .simple(entity.get<WorldGeometryComponent>().geometry!!) {
                zoomProjection.project(it - worldOrigin)
            }
            .map { screenMultipolygon ->
                runLaterBySystem(entity) { theEntity ->
                    tagDirtyParentLayer(theEntity)
                    theEntity.provide(::ScreenGeometryComponent).apply {
                        geometry = screenMultipolygon
                        this.zoom = zoom
                    }
                    
                    theEntity.tryGet<ScaleComponent>()?.let {
                        it.zoom = zoom
                        it.scale = 1.0
                    }
                }
            }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewport = context.mapRenderContext.viewport

        if (context.camera.isIntegerZoom) {
            getEntities(COMPONENT_TYPES).forEach {
                it.setComponent(
                    MicroThreadComponent(
                        createScalingTask(it, viewport.zoom),
                        myQuantIterations
                    )
                )
            }
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            ZoomChangedComponent::class,
            WorldOriginComponent::class,
            WorldGeometryComponent::class,
            ScreenOriginComponent::class,
            ParentLayerComponent::class
        )
    }
}
