/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.coord.CoordinatesMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers.IDENTITY
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal class PolarCoordProvider(
    private val thetaFromX: Boolean,
    private val start: Double,
    private val clockwise: Boolean
) : CoordProviderBase(xLim = null, yLim = null, false) {

    override val isLinear: Boolean
        get() = false

    // TODO: polar coord actually does not support flipped and xLim/yLim
    override fun with(xLim: DoubleSpan?, yLim: DoubleSpan?, flipped: Boolean): CoordProvider {
        return PolarCoordProvider(thetaFromX, start, clockwise)
    }

    override fun adjustGeomSize(hDomain: DoubleSpan, vDomain: DoubleSpan, geomSize: DoubleVector): DoubleVector {
        return if (geomSize.x < geomSize.y) {
            DoubleVector(geomSize.x, geomSize.x)
        } else {
            DoubleVector(geomSize.y, geomSize.y)
        }
    }

    override fun createCoordinateMapper(adjustedDomain: DoubleRectangle, clientSize: DoubleVector): CoordinatesMapper {
        val (rDomain, thetaDomain) = when (thetaFromX) {
            true -> adjustedDomain.yRange() to adjustedDomain.xRange()
            false -> adjustedDomain.xRange() to adjustedDomain.yRange()
        }

        val rNorm = 0.0 - rDomain.lowerEnd
        val thetaNorm = 0.0 - thetaDomain.lowerEnd

        val rRangeNorm = DoubleSpan(0.0, rDomain.upperEnd + rNorm)
        val thetaRangeNorm = DoubleSpan(0.0, thetaDomain.upperEnd + thetaNorm)

        val rScaleMapper = Mappers.mul(rRangeNorm, min(clientSize.x, clientSize.y) / 2.0)
        val thetaScaleMapper = Mappers.mul(thetaRangeNorm, 2.0 * PI)
        val center = clientSize.mul(0.5)

        val norm = when (thetaFromX) {
            true -> DoubleVector(thetaNorm, rNorm)
            false -> DoubleVector(rNorm, thetaNorm)
        }

        fun scalerThetaX(v: DoubleVector) = rScaleMapper(v.y) to thetaScaleMapper(v.x)
        fun scalerThetaY(v: DoubleVector) = rScaleMapper(v.x) to thetaScaleMapper(v.y)

        val scaler = when (thetaFromX) {
            true -> ::scalerThetaX
            false -> ::scalerThetaY
        }

        val sign = if (clockwise) -1.0 else 1.0
        val startAngle = PI / 2.0 + sign * start

        val polarProjection = object : Projection {
            override val nonlinear: Boolean = true

            override fun project(v: DoubleVector): DoubleVector {
                val (r, theta) = scaler(v.add(norm))
                checkNotNull(r)
                checkNotNull(theta)

                val x = r * cos(sign * theta + startAngle)
                val y = r * sin(sign * theta + startAngle)
                return center.add(DoubleVector(x, y))
            }

            override fun invert(v: DoubleVector): DoubleVector = TODO("Not yet implemented")
            override fun validDomain(): DoubleRectangle = TODO("Not yet implemented")

        }
        val clientBounds = DoubleRectangle(DoubleVector.ZERO, clientSize)

        return CoordinatesMapper(
            hScaleMapper = IDENTITY,
            vScaleMapper = IDENTITY,
            clientBounds = clientBounds,
            projection = polarProjection,
            flipAxis = false
        )
    }
}
