/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.contains
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.Client
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent

class BarLocatorHelper : LocatorHelper {

    override fun getColor(target: EcsEntity): Color? {
        return target.get<ChartElementComponent>().fillColor
    }

    override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean {
        val dimension = target.get<ScreenDimensionComponent>().dimension
        target.get<ScreenLoopComponent>().origins.forEach { origin ->
            if(Rect(origin, dimension).contains(coord)) {
                return true
            }
        }

        return false
    }
}