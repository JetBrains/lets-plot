/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.times
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.onEachEntity2
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.camera.ZoomLevelChangedComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent

class ChartElementScaleSystem(
    private val zoomInMultiplier: Double,
    private val zoomOutMultiplier: Double,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    override fun updateImpl(context: LiveMapContext, dt: Double) {
        onEachEntity2<ZoomLevelChangedComponent, ChartElementComponent> { entity, _, chartElementComponent ->
            with(chartElementComponent) {
                if (scalable && context.initialZoom != null) {
                    scaleFactor = computeScaleFactor(context.camera.zoom, context.initialZoom!!.toDouble())

                    entity.tryGet<SymbolComponent>()?.let {
                        entity.provide(::ScreenDimensionComponent).dimension = it.size * scaleFactor
                    }
                }
            }
        }
    }

    private fun computeScaleFactor(currentZoom: Double, baseZoom: Double): Double = when {
        currentZoom == baseZoom -> 1.0
        currentZoom > baseZoom -> (currentZoom - baseZoom + 1.0).toDouble() * zoomInMultiplier
        currentZoom < baseZoom -> currentZoom / baseZoom * zoomOutMultiplier
        else -> error("Unexpected")
    }
}
