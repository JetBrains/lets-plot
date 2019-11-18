/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.GeoUtils.FULL_LONGITUDE
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.World
import jetbrains.livemap.projections.WorldPoint
import kotlin.math.pow
import kotlin.math.round

class LiveMapLocation(private val myViewport: Viewport, private val myMapProjection: MapProjection) {

    val viewLonLatRect: DoubleRectangle
        get() {
            val viewRect = myViewport.window

            val nw = worldToLonLat(viewRect.origin)
            val se = worldToLonLat(viewRect.origin + viewRect.dimension)

            return DoubleRectangle(nw.x, se.y, se.x - nw.x, nw.y - se.y)
        }

    private fun worldToLonLat(worldCoord: WorldPoint): LonLatPoint {
        val coord: Vec<World>
        val shift: LonLatPoint
        val worldSize = myMapProjection.mapRect.dimension

        when {
            worldCoord.x > worldSize.x -> {
                shift = explicitVec<LonLat>(FULL_LONGITUDE, 0.0)
                coord = worldCoord.transform(newX = { it - worldSize.scalarX })
            }
            worldCoord.x < 0 -> {
                shift = explicitVec<LonLat>(-FULL_LONGITUDE, 0.0)
                coord = worldSize.transform(newX = { it + worldSize.scalarX })
            }
            else -> {
                shift = explicitVec<LonLat>(0.0, 0.0)
                coord = worldCoord
            }
        }

        return shift + myMapProjection.invert(coord)
    }

    companion object {

        fun getLocationString(viewRect: DoubleRectangle): String {
            val delta = viewRect.dimension.mul(0.05)

            return ("location = ["
                    + (viewRect.left + delta.x).round(6) + ", "
                    + (viewRect.top + delta.y).round(6) + ", "
                    + (viewRect.right - delta.x).round(6) + ", "
                    + (viewRect.bottom - delta.y).round(6) + "]")
        }

        private fun Double.round(digits: Int): Double {
            val a = 10.0.pow(digits)

            return round(this * a) / a
        }
    }
}