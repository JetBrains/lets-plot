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

class PointLocatorHelper : LocatorHelper {

    override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean {
        if (REQUIRED_COMPONENTS !in target) {
            return false
        }

        val radius = target.get<PointComponent>().size / 2 * target.get<ChartElementComponent>().scalingSizeFactor
        return target.get<ScreenLoopComponent>().origins.any { distance(coord, it) <= radius }
    }

    companion object {
        val REQUIRED_COMPONENTS = listOf(PointComponent::class, ScreenLoopComponent::class, ChartElementComponent::class)
    }
}