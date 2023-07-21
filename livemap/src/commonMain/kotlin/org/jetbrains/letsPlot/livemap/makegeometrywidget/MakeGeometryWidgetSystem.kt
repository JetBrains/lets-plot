/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.makegeometrywidget

import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.values.Color.Companion.parseHex
import org.jetbrains.letsPlot.livemap.api.FeatureEntityFactory
import org.jetbrains.letsPlot.livemap.api.PathEntityBuilder
import org.jetbrains.letsPlot.livemap.api.PointEntityBuilder
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.input.InputMouseEvent
import org.jetbrains.letsPlot.livemap.core.input.MouseInputComponent
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.toClientPoint

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
        val factory = FeatureEntityFactory(widgetLayer, 1000)

        PointEntityBuilder(factory)
            .apply {
                point = lonlat
                strokeColor = DARK_ORANGE
                shape = 20
            }
            .build(nonInteractive = true)

        if (widgetLayer.count() > 0) {
            PathEntityBuilder(factory, myMapProjection)
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

