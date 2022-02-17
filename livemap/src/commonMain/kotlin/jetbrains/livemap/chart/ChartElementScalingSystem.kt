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
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

class ChartElementScalingSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (context.initialZoom == null) {
            return
        }

        onEachEntity2<ZoomLevelChangedComponent, ChartElementComponent> { entity, _, chartElementComponent ->
            val zoomDelta = context.camera.zoom.roundToInt() - context.initialZoom!!
            with(chartElementComponent) {
                scalingSizeFactor = sizeScalingRange?.let {
                    val scalingLevel = zoomDelta.coerceIn(it)
                    when {
                        scalingLevel == 0 -> 1.0
                        scalingLevel > 0 -> 2.0.pow(scalingLevel)
                        scalingLevel < 0 -> 1.0 / 2.0.pow(abs(scalingLevel))
                        else -> error("Unknown")
                    }
                } ?: 1.0

                scalingAlphaValue = when {
                    alphaScalingEnabled && zoomDelta > 2 -> max(0.1, 1.0 - 0.2 * (zoomDelta - 2))
                    else -> null
                }

                entity.tryGet<SymbolComponent>()?.let {
                    entity.provide(::ScreenDimensionComponent).dimension = it.size * scalingSizeFactor
                }
            }
        }
    }

}
