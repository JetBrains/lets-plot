/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.parseRGB
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.projection.Client
import jetbrains.livemap.rendering.PieSectorComponent
import jetbrains.livemap.rendering.StyleComponent
import kotlin.math.PI

class PieLocatorHelper : LocatorHelper {
    override fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean {
        val pieSector = target.get<PieSectorComponent>()

        target.get<ScreenLoopComponent>().origins.forEach { origin ->
            if (isCoordinateInPieSector(coord, origin, pieSector)) {
                return true
            }
        }

        return false
    }

    override fun getColor(target: EcsEntity): Color? {
        return target.get<StyleComponent>().fillColor?.let(::parseRGB)
    }

    private fun isCoordinateInPieSector(coord: Vec<Client>, origin: Vec<Client>, pieSector: PieSectorComponent): Boolean {
        if (LocatorUtil.distance(coord, origin) > pieSector.radius) {
            return false
        }

        var angle = LocatorUtil.calculateAngle(origin, coord)
        if (angle < - PI / 2) {
            angle += 2 * PI
        }

        return pieSector.startAngle <= angle && angle < pieSector.endAngle
    }
}