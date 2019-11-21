/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.datalore.base.values.Color.Companion.parseHex
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.api.PathBuilder
import jetbrains.livemap.api.PointBuilder
import jetbrains.livemap.api.geometry
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.core.animation.Animations.AnimationBuilder
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.input.InputMouseEvent
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.toClientPoint

class MakeGeometryWidgetSystem(
    private val myComponentManager: EcsComponentManager,
    private val myMapProjection: MapProjection,
    private val myViewport: Viewport
) : LiveMapSystem(myComponentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        getWidgetLayer()?.let { widgetLayer ->

            widgetLayer.click()?.let { event ->
                if (!event.isStopped) {
                    event.location
                        .toClientPoint()
                        .run(myViewport::getMapCoord)
                        .run(myMapProjection::invert)
                        .let { point ->
                            createVisualEntities(point, widgetLayer)
                            widgetLayer.add(point)
                        }
                }
            }
        }
    }

    private fun createVisualEntities(lonlat: LonLatPoint, widgetLayer: EcsEntity) {
        val factory = MapEntityFactory(widgetLayer)

        PointBuilder(factory, myMapProjection)
            .apply {
                point = lonlat
                strokeColor = DARK_ORANGE
                shape = 20
            }
            .build(pointScaling = false, animationBuilder = AnimationBuilder(500.0))

        if (widgetLayer.count() > 0) {
            PathBuilder(factory, myMapProjection)
                .apply {
                    geometry(listOf(widgetLayer.last(), lonlat), false)
                    strokeColor = DARK_ORANGE
                    strokeWidth = 1.5
                }
                .build()
        }
    }

    private fun getWidgetLayer(): EcsEntity? =
        myComponentManager.tryGetSingletonEntity(WIDGET_COMPONENTS)

    private fun EcsEntity.click(): InputMouseEvent? =
        get<MouseInputComponent>().click

    private fun EcsEntity.count(): Int =
        get<MakeGeometryWidgetComponent>().points.count()

    private fun EcsEntity.last(): LonLatPoint =
        get<MakeGeometryWidgetComponent>().points.last()

    private fun EcsEntity.add(point: LonLatPoint) =
        get<MakeGeometryWidgetComponent>().points.add(point)

    companion object {
        val WIDGET_COMPONENTS = listOf(
            LayerEntitiesComponent::class,
            MouseInputComponent::class,
            MakeGeometryWidgetComponent::class
        )

        val DARK_ORANGE = parseHex("#cc7a00")
    }
}

