/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.contains
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.parseRGB
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.placement.ScreenDimensionComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.projection.Client
import jetbrains.livemap.rendering.StyleComponent

class BarLocatorHelper : LocatorHelper {

    override fun getColor(target: EcsEntity): Color? {
        return target.get<StyleComponent>().fillColor?.let(::parseRGB)
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