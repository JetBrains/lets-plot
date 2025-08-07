/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart

import org.jetbrains.letsPlot.commons.intern.typedGeometry.newVec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.times
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.onEachEntity
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.placement.ScreenDimensionComponent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

class ChartElementScalingSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private val alphaScalingStartingLevel = 3
    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (context.initialZoom == null) {
            return
        }

        if (context.camera.isZoomLevelChanged) {
            onEachEntity<ChartElementComponent> { entity, chartElementComponent ->
                with(chartElementComponent) {
                    sizeScalingRange?.let {
                        val zoomDelta = context.camera.zoom.roundToInt() - context.initialZoom!!
                        val scalingLevel = zoomDelta.coerceIn(it)

                        val alphaScalingLevel = scalingLevel - alphaScalingStartingLevel + 1
                        scalingAlphaValue = when {
                            alphaScalingEnabled && alphaScalingLevel > 0 -> (max(
                                0.1,
                                1.0 - 0.2 * (alphaScalingLevel)
                            ) * 255).roundToInt()

                            else -> null
                        }

                        scalingSizeFactor = when {
                            scalingLevel == 0 -> 1.0
                            scalingLevel > 0 -> 2.0.pow(scalingLevel)
                            scalingLevel < 0 -> 1.0 / 2.0.pow(abs(scalingLevel))
                            else -> error("Unknown")
                        }
                    }

                    entity.tryGet<PointComponent>()?.let {
                        entity.provide(::ScreenDimensionComponent).dimension =
                            newVec(it.size, it.size) * scalingSizeFactor
                    }
                }
            }
        }
    }

}
