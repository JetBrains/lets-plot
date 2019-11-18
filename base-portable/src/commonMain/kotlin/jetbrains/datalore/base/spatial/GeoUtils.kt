/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.projectionGeometry.*


object GeoUtils {

    val BBOX_CALCULATOR = GeoBoundingBoxCalculator(EARTH_RECT, true, false)


    fun convertToGeoRectangle(rect: Rect<LonLat>): GeoRectangle {
        val left: Double
        val right: Double

        if (rect.width < EARTH_RECT.width) {
            left = normalizeLon(rect.left)
            right = normalizeLon(rect.right)
        } else {
            left = EARTH_RECT.left
            right = EARTH_RECT.right
        }

        return GeoRectangle(left, limitLat(rect.top), right, limitLat(rect.bottom))
    }

    fun <TypeT> getQuadOrigin(mapRect: Rect<TypeT>, quadKey: String): Vec<TypeT> {
        var left = mapRect.scalarLeft
        var top = mapRect.scalarTop
        var width = mapRect.scalarWidth
        var height = mapRect.scalarHeight

        for (quadrant in quadKey) {
            width /= 2.0
            height /= 2.0

            if (quadrant == '1' || quadrant == '3') {
                left += width
            }
            if (quadrant == '2' || quadrant == '3') {
                top += height
            }
        }
        return newVec(left, top)
    }


    fun tileXYToTileID(tileX: Int, tileY: Int, zoom: Int): String {
        var tileID = ""

        for (i in zoom downTo 1) {
            var digit = '0'
            val mask = 1 shl i - 1

            if (tileX and mask != 0) {
                ++digit
            }

            if (tileY and mask != 0) {
                digit += 2
            }

            tileID += digit
        }

        return tileID
    }

    fun getTileCount(zoom: Int): Int {
        return 1 shl zoom
    }
}
