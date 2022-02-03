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
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class ChartElementScaleSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (context.initialZoom == null) {
            return
        }

        onEachEntity2<ZoomLevelChangedComponent, ChartElementComponent> { entity, _, chartElementComponent ->
            val zoomDelta = context.camera.zoom.roundToInt() - context.initialZoom!!
            with(chartElementComponent) {
                if (scaleRange != null) {
                    scaleSizeFactor = computeSizeFactor(zoomDelta.coerceIn(scaleRange!!))
                    scaleAlphaValue = when {
                        abs(zoomDelta) == 1 -> 0.7
                        abs(zoomDelta) >= 2 -> 0.5
                        else -> null
                    }
                } else {
                    scaleSizeFactor = 1.0
                    scaleAlphaValue = null
                }
                entity.tryGet<SymbolComponent>()?.let {
                    entity.provide(::ScreenDimensionComponent).dimension = it.size * scaleSizeFactor
                }
            }
        }
    }

    private fun computeSizeFactor(zoomDelta: Int): Double = when {
        zoomDelta == 0 -> 1.0
        zoomDelta > 0 -> 2.0.pow(zoomDelta)
        zoomDelta < 0 -> 1.0 / 2.0.pow(abs(zoomDelta))
        else -> error("Unknown")
    }
}
