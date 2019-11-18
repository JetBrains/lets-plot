/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.limit
import jetbrains.datalore.base.spatial.LonLat

object Utils {
    fun calculateBBoxes(v: MapObject): List<Rect<LonLat>> {
        return when (v) {
            is MapGeometry -> calculateGeometryBBoxes(v)
            is MapPointGeometry -> calculatePointBBoxes(v)
            else -> throw IllegalStateException("Unsupported MapObject type: ${v::class}")
        }
    }

    private fun calculateGeometryBBoxes(v: MapGeometry): List<Rect<LonLat>> {
        return v.geometry
            ?.run { asMultipolygon().limit() }
            ?: emptyList()

    }

    private fun calculatePointBBoxes(v: MapPointGeometry): List<Rect<LonLat>> {
        return listOf(Rect(v.point, Vec(0,0)))
    }
}