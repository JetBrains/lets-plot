/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.PointComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.searching.LocatorUtil.distance

class PointLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity): HoverObject? {
        if (REQUIRED_COMPONENTS !in target) {
            return null
        }

        val radius = target.get<PointComponent>().size / 2 * target.get<ChartElementComponent>().scalingSizeFactor
        return when (target.get<ScreenLoopComponent>().origins.any { distance(coord, it) <= radius }) {
            true -> HoverObject(
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index
            )

            false -> null
        }
    }

    companion object {
        val REQUIRED_COMPONENTS =
            listOf(PointComponent::class, ScreenLoopComponent::class, ChartElementComponent::class)
    }
}