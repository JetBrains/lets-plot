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
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.toClientPoint

class MakeGeometryWidgetSystem(
    private val myComponentManager: EcsComponentManager,
    private val myMapProjection: MapProjection,
    private val myViewport: Viewport
) : LiveMapSystem(myComponentManager) {
    override fun updateImpl(context: LiveMapContext, dt: Double) {
        myComponentManager.tryGetSingletonEntity(WIDGET_COMPONENTS)?.let { widgetLayer ->
            val input = widgetLayer.get<MouseInputComponent>()
            input.click?.let {
                if (!it.isStopped) {
                    val lonlat = it.location
                        .toClientPoint()
                        .run(myViewport::getMapCoord)
                        .run(myMapProjection::invert)
                    val points = widgetLayer.get<MakeGeometryWidgetComponent>().points
                    val factory = MapEntityFactory(widgetLayer)

                    PointBuilder(factory, myMapProjection)
                        .apply {
                            point = lonlat
                            strokeColor = parseHex("#cc7a00")
                            shape = 20
                        }
                        .build(pointScaling = false, animationBuilder = AnimationBuilder(500.0))

                    if (points.count() > 0) {
                        PathBuilder(factory, myMapProjection)
                            .apply {
                                geometry(listOf(points.last(), lonlat), false)
                                strokeColor = parseHex("#cc7a00")
                                strokeWidth = 1.5
                            }
                            .build()
                    }

                    points.add(lonlat)
                }
            }
        }
    }

    companion object {
        val WIDGET_COMPONENTS = listOf(
            LayerEntitiesComponent::class,
            MouseInputComponent::class,
            MakeGeometryWidgetComponent::class
        )
    }
}

