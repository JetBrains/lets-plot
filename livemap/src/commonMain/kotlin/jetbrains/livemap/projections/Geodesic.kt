/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.GeoUtils.normalizeLon
import kotlin.math.*

private const val LONGITUDE_EPS = 1.0
private const val FULL_ANGLE = 360.0
private const val STRAIGHT_ANGLE = 180.0

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

    if (abs(STRAIGHT_ANGLE - lonDelta) < LONGITUDE_EPS) {
        //the shortest path through North/South pole
        val sign = if (start.scalarY + finish.scalarY >= 0) +1.0 else -1.0
        val latitude = sign * STRAIGHT_ANGLE / 2.0
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
    val startLatTan = tan(degreeToRad(start.y))
    val finishLatTan = tan(degreeToRad(finish.y))
    val deltaLonSin = sin(degreeToRad(finish.x - start.x))

    //calculate path
    var longitude = start.x
    while (abs(longitude - finish.x) > LONGITUDE_EPS) {
        longitude += directionSign * LONGITUDE_EPS
        longitude = normalizeLon(longitude)

        val latitude = radToDegree(
            atan(
                (finishLatTan * sin(degreeToRad(longitude - start.x)) + startLatTan * sin(degreeToRad(finish.x - longitude))) / deltaLonSin
            )
        )

        path.add(explicitVec(longitude, latitude))
    }
}


private fun calculateIncreasingDistance(start: Double, finish: Double): Double {
    val dist = finish - start
    return dist + if (dist < 0.0) FULL_ANGLE else 0.0
}

private fun degreeToRad(degree: Double): Double {
    return PI * degree / STRAIGHT_ANGLE
}

private fun radToDegree(rad: Double): Double {
    return STRAIGHT_ANGLE * rad / PI
}

