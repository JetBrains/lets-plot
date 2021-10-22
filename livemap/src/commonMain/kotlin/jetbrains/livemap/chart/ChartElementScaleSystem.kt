/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.times
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.onEachEntity2
import jetbrains.livemap.core.util.EasingFunction
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.camera.ZoomLevelChangedComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent

class ChartElementScaleSystem(
    private val minScale: Double,
    private val maxScale: Double,
    private val zoomInEasing: EasingFunction,
    private val zoomOutEasing: EasingFunction,
    private val minZoom: Int,
    private val maxZoom: Int,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    init {
        require(minScale <= 1.0) { "minScale should be less or equal 1.0 but was $minScale" }
        require(maxScale >= 1.0) { "maxScale should be greater or equal 1.0 but was $maxScale" }
    }

    @kotlinx.coroutines.ObsoleteCoroutinesApi
    override fun updateImpl(context: LiveMapContext, dt: Double) {
        onEachEntity2<ZoomLevelChangedComponent, ChartElementComponent> { entity, _, chartElementComponent ->
            with(chartElementComponent) {
                if (scalable && context.initialZoom != null) {
                    scaleFactor = computeScaleFactor(context.camera.zoom.toInt(), context.initialZoom!!)

                    entity.tryGet<SymbolComponent>()?.let {
                        entity.provide(::ScreenDimensionComponent).dimension = it.size * scaleFactor
                    }
                }
            }
        }
    }

    private fun computeScaleFactor(currentZoom: Int, chartElementBaseZoom: Int): Double = when {
        currentZoom == chartElementBaseZoom -> 1.0
        currentZoom < chartElementBaseZoom -> {
            val zoomOuts = chartElementBaseZoom - minZoom
            val progress = (chartElementBaseZoom - currentZoom).toDouble() / zoomOuts
            1.0 - zoomOutEasing(progress) * (1.0 - minScale)
        }
        else -> {
            val zoomIns = maxZoom - chartElementBaseZoom
            val progress = (currentZoom - chartElementBaseZoom).toDouble() / zoomIns
            1.0 + zoomInEasing(progress) * (maxScale - 1.0)
        }
    }

    private fun traceScales(baseZoom: Int): String {
        return IntRange(minZoom, maxZoom)
            .map { computeScaleFactor(it, baseZoom) }
            .joinToString(transform = Double::toString)
    }
}
