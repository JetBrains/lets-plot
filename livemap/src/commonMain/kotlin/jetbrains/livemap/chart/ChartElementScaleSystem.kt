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
import kotlin.math.min
import kotlin.math.pow

class ChartElementScaleSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        onEachEntity2<ZoomLevelChangedComponent, ChartElementComponent> { entity, _, chartElementComponent ->
            with(chartElementComponent) {
                if (scalable && context.initialZoom != null) {
                    scaleSizeFactor = zoomScale(context.camera.zoom, context.initialZoom!!.toDouble())
                    scaleAlphaValue = when(context.camera.zoom - context.initialZoom!!.toDouble()) {
                        1.0, -1.0 -> 0.7
                        2.0, -2.0 -> 0.5
                        else -> null
                    }

                    entity.tryGet<SymbolComponent>()?.let {
                        entity.provide(::ScreenDimensionComponent).dimension = it.size * scaleSizeFactor
                    }
                }
            }
        }
    }

    private fun zoomScale(currentZoom: Double, baseZoom: Double): Double = when {
        currentZoom == baseZoom -> 1.0
        currentZoom > baseZoom -> 2.0.pow(min(MAX_ZOOM_IN_FACTOR, currentZoom - baseZoom))
        currentZoom < baseZoom -> 1.0 / 2.0.pow(min(MAX_ZOOM_OUT_FACTOR, baseZoom - currentZoom))
        else -> error("Unexpected")
    }

    companion object {
        const val MAX_ZOOM_IN_FACTOR = 2.0
        const val MAX_ZOOM_OUT_FACTOR = 2.0
    }
}
