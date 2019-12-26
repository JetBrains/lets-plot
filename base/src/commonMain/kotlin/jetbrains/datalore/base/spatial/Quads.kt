/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.math.ipow
import jetbrains.datalore.base.typedGeometry.*
import kotlin.math.max
import kotlin.math.min

fun calulateQuadsCount(zoom: Int): Int {
    return 2.ipow(zoom).toInt()
}

fun <GeometryT, QuadT> calculateQuadKeys(
    mapRect: Rect<GeometryT>,
    viewRect: Rect<GeometryT>,
    zoom: Int,
    quadKeyFactory: (String) -> QuadT
): Set<QuadT> {
    val quadKeys = HashSet<QuadT>()
    val tileCount = calulateQuadsCount(zoom)

    fun calcQuadNum(value: Double, range: ClosedRange<Double>, tileCount: Int): Int {
        val position = (value - range.lowerEndpoint()) / (range.upperEndpoint() - range.lowerEndpoint())
        return max(0.0, min(position * tileCount, (tileCount - 1).toDouble())).toInt()
    }

    val xmin = calcQuadNum(viewRect.left, mapRect.xRange(), tileCount)
    val xmax = calcQuadNum(viewRect.right, mapRect.xRange(), tileCount)
    val ymin = calcQuadNum(viewRect.top, mapRect.yRange(), tileCount)
    val ymax = calcQuadNum(viewRect.bottom, mapRect.yRange(), tileCount)

    for (x in xmin..xmax) {
        for (y in ymin..ymax) {
            xyToKey(x, y, zoom).run(quadKeyFactory).run(quadKeys::add)
        }
    }

    return quadKeys
}

fun xyToKey(x: Int, y: Int, zoom: Int): String {
    var key = ""

    for (i in zoom downTo 1) {
        var digit = '0'
        val mask = 1 shl i - 1

        if (x and mask != 0) {
            ++digit
        }

        if (y and mask != 0) {
            digit += 2
        }

        key += digit
    }

    return key
}
