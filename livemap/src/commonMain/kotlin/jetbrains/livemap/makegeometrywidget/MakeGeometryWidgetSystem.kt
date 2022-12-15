/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.makegeometrywidget

import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.values.Color.Companion.parseHex
import jetbrains.livemap.api.MapEntityFactory
import jetbrains.livemap.api.PathBuilder
import jetbrains.livemap.api.PointBuilder
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.input.InputMouseEvent
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.toClientPoint

class MakeGeometryWidgetSystem(
    private val myComponentManager: EcsComponentManager,
    private val myMapProjection: MapProjection,
    private val myViewport: Viewport
) : AbstractSystem<LiveMapContext>(myComponentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        getWidgetLayer()?.let { widgetLayer ->

            widgetLayer.click()?.let { event ->
                if (!event.isStopped) {
                    event.location
                        .toClientPoint()
                        .run(myViewport::getMapCoord)
                        .run(myMapProjection::invert)
                        ?.let { point ->
                            createVisualEntities(point, widgetLayer)
                            widgetLayer.add(point)
                        }
                }
            }
        }
    }

    private fun createVisualEntities(lonlat: LonLatPoint, widgetLayer: EcsEntity) {
        val factory = MapEntityFactory(widgetLayer)

        PointBuilder(factory)
            .apply {
                point = lonlat
                strokeColor = DARK_ORANGE
                shape = 20
            }
            .build(nonInteractive = true)

        if (widgetLayer.count() > 0) {
            PathBuilder(factory, myMapProjection)
                .apply {
                    sizeScalingRange = null
                    strokeColor = DARK_ORANGE
                    strokeWidth = 1.5
                    points = listOf(widgetLayer.last(), lonlat)
                    flat = true
                }
                .build(true)
        }
    }

    private fun getWidgetLayer(): EcsEntity? = myComponentManager.tryGetSingletonEntity(WIDGET_COMPONENTS)
    private fun EcsEntity.click(): InputMouseEvent? = get<MouseInputComponent>().clickEvent
    private fun EcsEntity.count(): Int = get<MakeGeometryWidgetComponent>().points.size
    private fun EcsEntity.last(): LonLatPoint = get<MakeGeometryWidgetComponent>().points.last()
    private fun EcsEntity.add(point: LonLatPoint) = get<MakeGeometryWidgetComponent>().points.add(point)

    companion object {
        val WIDGET_COMPONENTS = listOf(
            LayerEntitiesComponent::class,
            MouseInputComponent::class,
            MakeGeometryWidgetComponent::class
        )

        val DARK_ORANGE = parseHex("#cc7a00")
    }
}

