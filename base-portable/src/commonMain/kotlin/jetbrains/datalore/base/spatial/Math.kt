/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.projectionGeometry.*
import kotlin.math.max
import kotlin.math.min

fun <GeometryT, QuadT> calculateQuadKeys(
    mapRect: Rect<GeometryT>,
    viewRect: Rect<GeometryT>,
    zoom: Int,
    constructor: (String) -> QuadT
): Set<QuadT> {
    val quadKeys = HashSet<QuadT>()
    val tileCount = GeoUtils.getTileCount(zoom)

    fun calcTileNum(value: Double, range: ClosedRange<Double>, tileCount: Int): Int {
        val position = (value - range.lowerEndpoint()) / (range.upperEndpoint() - range.lowerEndpoint())
        return max(0.0, min(position * tileCount, (tileCount - 1).toDouble())).toInt()
    }

    val xmin = calcTileNum(viewRect.left, mapRect.xRange(), tileCount)
    val xmax = calcTileNum(viewRect.right, mapRect.xRange(), tileCount)
    val ymin = calcTileNum(viewRect.top, mapRect.yRange(), tileCount)
    val ymax = calcTileNum(viewRect.bottom, mapRect.yRange(), tileCount)

    for (x in xmin..xmax) {
        for (y in ymin..ymax) {
            quadKeys.add(constructor(GeoUtils.tileXYToTileID(x, y, zoom)))
        }
    }

    return quadKeys
}

fun calculateQuadKeys(rect: Rect<LonLat>, zoom: Int): Set<QuadKey<LonLat>> {
    val flippedRect = Rect<LonLat>(
        rect.left,
        -rect.bottom,
        rect.width,
        rect.height
    )
    return calculateQuadKeys(EARTH_RECT, flippedRect, zoom, ::QuadKey)
}

