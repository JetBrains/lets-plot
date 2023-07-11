/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.scalarY
import org.jetbrains.letsPlot.commons.intern.typedGeometry.transform
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sin
import kotlin.math.tan

object Geodesic {
    private const val LONGITUDE_EPS = 1.0
    private const val FULL_ANGLE = 360.0

    fun createArcPath(path: List<LonLatPoint>): List<LonLatPoint> {
        val arcPath = ArrayList<LonLatPoint>()
        if (path.isEmpty()) {
            return arcPath
        }

        arcPath.add(path[0])
        for (i in 1 until path.size) {
            addArcPointsToPath(arcPath, path[i - 1], path[i])
            arcPath.add(path[i])
        }

        return arcPath
    }

    private fun addArcPointsToPath(path: MutableList<LonLatPoint>, start: LonLatPoint, finish: LonLatPoint) {
        val lonDelta = abs(start.x - finish.x)
        if (lonDelta <= LONGITUDE_EPS) {
            //the shortest path along a meridian
            return
        }

        if (abs(180.0 - lonDelta) < LONGITUDE_EPS) {
            //the shortest path through North/South pole
            val sign = if (start.scalarY + finish.scalarY >= 0) +1.0 else -1.0
            val latitude = sign * 180.0 / 2.0
            path.add(start.transform(newY = { Scalar(latitude) }))
            path.add(finish.transform(newY = { Scalar(latitude) }))
            return
        }

        //calculate longitude direction
        val directionSign: Double =
            if (calculateIncreasingDistance(start.x, finish.x)
                <= calculateIncreasingDistance(finish.x, start.x)
            )
                +1.0
            else
                -1.0

        //calculate auxiliary constants
        val startLatTan = tan(toRadians(start.y))
        val finishLatTan = tan(toRadians(finish.y))
        val deltaLonSin = sin(toRadians(finish.x - start.x))

        //calculate path
        var longitude = start.x
        while (abs(longitude - finish.x) > LONGITUDE_EPS) {
            longitude += directionSign * LONGITUDE_EPS
            longitude = normalizeLon(longitude)

            val latitude = toDegrees(
                atan(
                    (finishLatTan * sin(toRadians(longitude - start.x)) + startLatTan * sin(toRadians(finish.x - longitude))) / deltaLonSin
                )
            )

            path.add(explicitVec(longitude, latitude))
        }
    }

    private fun calculateIncreasingDistance(start: Double, finish: Double): Double {
        val dist = finish - start
        return dist + if (dist < 0.0) FULL_ANGLE else 0.0
    }
}
