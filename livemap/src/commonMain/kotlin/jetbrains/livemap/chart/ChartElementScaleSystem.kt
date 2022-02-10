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
                if (scalingRange != null) {
                    val scalingLevel = zoomDelta.coerceIn(scalingRange!!)
                    scalingSizeFactor = computeSizeFactor(scalingLevel)
                    scalingAlphaValue = computeAlphaValue(zoomDelta)
                } else {
                    scalingSizeFactor = 1.0
                    scalingAlphaValue = null
                }
                entity.tryGet<SymbolComponent>()?.let {
                    entity.provide(::ScreenDimensionComponent).dimension = it.size * scalingSizeFactor
                }
            }
        }
    }

    private fun computeSizeFactor(scalingLevel: Int): Double = when {
        scalingLevel == 0 -> 1.0
        scalingLevel > 0 -> 2.0.pow(scalingLevel)
        scalingLevel < 0 -> 1.0 / 2.0.pow(abs(scalingLevel))
        else -> error("Unknown")
    }

    private fun computeAlphaValue(scalingLevel: Int): Double? = when {
        scalingLevel <= 2 -> null
        else -> max(0.1, 1.0 - 0.2 * (scalingLevel - 2))
    }
}
