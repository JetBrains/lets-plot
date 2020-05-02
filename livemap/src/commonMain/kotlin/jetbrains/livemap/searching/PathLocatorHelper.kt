/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.limit
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.projection.Client
import jetbrains.livemap.rendering.StyleComponent

class PathLocatorHelper : LocatorHelper {
    override fun getColor(target: EcsEntity): Color? {
        return target.get<StyleComponent>().strokeColor?.let(Color.Companion::parseRGB)
    }

    override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean {
        val strokeRadius: Double = target.get<StyleComponent>().strokeWidth / 2
        target.get<ScreenLoopComponent>().origins.forEach { origin ->
            if (isCoordinateInPath(coord - origin, strokeRadius, target.get<ScreenGeometryComponent>().geometry)) {
                return true
            }
        }

        return false
    }

    private fun isCoordinateInPath(coord: Vec<Client>, strokeRadius: Double, multiPath: MultiPolygon<Client>): Boolean {

        for (path in multiPath) {
            if (!LocatorUtil.coordInExtendedRect(coord, path.limit(), strokeRadius)) {
                continue
            }
            for (ring in path) {
                if (LocatorUtil.pathContainsCoordinate(coord, ring.toList(), strokeRadius)) {
                    return true
                }
            }
        }
        return false
    }
}