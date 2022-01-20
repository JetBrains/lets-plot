/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.Client
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.SymbolComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent

class PointLocatorHelper : LocatorHelper {

    override fun getColor(target: EcsEntity): Color? {
        return target.get<ChartElementComponent>().run { fillColor ?: strokeColor }
    }

    override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean {
        if (!target.contains(LOCATABLE_COMPONENTS)) {
            return false
        }

        val radius = target.get<SymbolComponent>().size.x / 2
        return target.get<ScreenLoopComponent>().origins.any { LocatorUtil.distance(coord, it) <= radius }
    }

    companion object {
        val LOCATABLE_COMPONENTS = listOf(SymbolComponent::class, ScreenLoopComponent::class)
    }
}