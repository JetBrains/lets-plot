/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.intern.math.lineSlope
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import kotlin.math.*


// https://svn.r-project.org/R/trunk/src/library/grid/R/curve.R
fun curve(
    start: DoubleVector,
    end: DoubleVector,
    curvature: Double,
    angle: Double,
    ncp: Int
): List<DoubleVector> {
    val controlPoints = calcControlPoints(start, end, curvature, angle, ncp)
    return listOf(start) + controlPoints + listOf(end)
}

private fun calcControlPoints(
    start: DoubleVector,
    end: DoubleVector,
    curvature: Double,
    angle: Double,
    ncp: Int
): List<DoubleVector> {
    // straight line
    if (curvature == 0.0 || abs(angle) !in 1.0..179.0) {
        return emptyList()
    }

    val mid = start.add(end).mul(0.5)
    val d = end.subtract(start)

    val rAngle = toRadians(angle)
    val corner = mid.add(
        start.subtract(mid).rotate(rAngle)
    )

    // Calculate angle to rotate region by to align it with x/y axes
    val beta = -atan(lineSlope(start, corner))

    // Rotate end point about start point to align region with x/y axes
    val new = start.add(
        d.rotate(beta)
    )

    // Calculate x-scale factor to make region "square"
    val scaleX = lineSlope(start, new)

    // Calculate the origin in the "square" region
    // (for rotating start point to produce control points)
    // (depends on 'curvature')
    // 'origin' calculated from 'curvature'
    val ratio = 2 * (sin(atan(curvature)).pow(2))
    val origin = curvature - curvature / ratio

    val ps = DoubleVector(start.x * scaleX, start.y)
    val oxy = calcOrigin(
        ps = ps,
        pe = DoubleVector(new.x * scaleX, new.y),
        origin
    )

    // Direction of rotation
    val dir = sign(curvature)

    // Angle of rotation depends on location of origin
    val maxTheta = PI + sign(origin * dir) * 2 * atan(abs(origin))

    val theta = (0 until (ncp + 2))
        .map { it * dir * maxTheta / (ncp + 1) }
        .drop(1)
        .dropLast(1)

    // May have BOTH multiple end points AND multiple
    // control points to generate (per set of end points)
    // Generate consecutive sets of control points by performing
    // matrix multiplication

    val indices = List(theta.size) { index -> index }

    val p = ps.subtract(oxy)
    val cp = indices.map { index ->
        oxy.add(
            p.rotate(theta[index])
        )
    }
        // Reverse transformations (scaling and rotation) to
        // produce control points in the original space
        .map {
            DoubleVector(
                it.x / scaleX,
                it.y
            )
        }

    return indices.map { index ->
        start.add(
            cp[index].subtract(start).rotate(-beta)
        )
    }
}

private fun calcOrigin(
    ps: DoubleVector,
    pe: DoubleVector,
    origin: Double
): DoubleVector {

    val mid = ps.add(pe).mul(0.5)
    val d = pe.subtract(ps)
    val slope = lineSlope(ps, pe)

    val oSlope = -1 / slope

    // The origin is a point somewhere along the line between
    // the end points, rotated by 90 (or -90) degrees
    // Two special cases:
    // If slope is non-finite then the end points lie on a vertical line, so
    // the origin lies along a horizontal line (oSlope = 0)
    // If oSlope is non-finite then the end points lie on a horizontal line,
    // so the origin lies along a vertical line (oSlope = Inf)
    val tmpOX = when {
        !slope.isFinite() -> 0.0
        !oSlope.isFinite() -> origin * d.x / 2
        else -> origin * d.x / 2
    }

    val tmpOY = when {
        !slope.isFinite() -> origin * d.y / 2
        !oSlope.isFinite() -> 0.0
        else -> origin * d.y / 2
    }

    return DoubleVector(mid.x + tmpOY, mid.y - tmpOX)
}
