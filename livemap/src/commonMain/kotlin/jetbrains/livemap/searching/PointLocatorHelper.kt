/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.parseRGB
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.placement.ScreenDimensionComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.projection.Client
import jetbrains.livemap.rendering.StyleComponent

class PointLocatorHelper : LocatorHelper {

    override fun getColor(target: EcsEntity): Color? {
        return target.get<StyleComponent>().run {
            (strokeColor ?: fillColor)?.let(::parseRGB)
        }
    }

    override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean {
        val origins = target.get<ScreenLoopComponent>().origins
        val radius = target.get<ScreenDimensionComponent>().dimension.x / 2

        origins.forEach {
            if (LocatorUtil.distance(coord, it) <= radius) {
                return true
            }
        }

        return false
    }
}