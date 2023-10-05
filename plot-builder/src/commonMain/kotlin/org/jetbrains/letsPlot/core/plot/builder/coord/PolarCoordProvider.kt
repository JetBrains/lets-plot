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
    private val theta: String,
    private val start: Int,
    private val clockwise: Boolean,
    private val clip: Boolean,
    xLim: DoubleSpan?,
    yLim: DoubleSpan?
) : CoordProviderBase(xLim, yLim, false) {

    override val isLinear: Boolean
        get() = false

    override fun with(xLim: DoubleSpan?, yLim: DoubleSpan?, flipped: Boolean): CoordProvider {
        return PolarCoordProvider(theta, start, clockwise, clip, xLim, yLim)
    }

    override fun adjustGeomSize(hDomain: DoubleSpan, vDomain: DoubleSpan, geomSize: DoubleVector): DoubleVector {
        return if (geomSize.x < geomSize.y) {
            DoubleVector(geomSize.x, geomSize.x)
        } else {
            DoubleVector(geomSize.y, geomSize.y)
        }
    }

    override fun createCoordinateMapper(adjustedDomain: DoubleRectangle, clientSize: DoubleVector): CoordinatesMapper {
        val (rRange, phiRange) = when (theta) {
            "x" -> adjustedDomain.yRange() to adjustedDomain.xRange()
            "y" -> adjustedDomain.xRange() to adjustedDomain.yRange()
            else -> error("Unsupported theta: expected `x` or `y`, but was `$theta`")
        }

        val rNorm = 0.0 - rRange.lowerEnd
        val phiNorm = 0.0 - phiRange.lowerEnd

        val rRangeNorm = DoubleSpan(0.0, rRange.upperEnd + rNorm)
        val phiRangeNorm = DoubleSpan(0.0, phiRange.upperEnd + phiNorm)

        val rScaleMapper = Mappers.mul(rRangeNorm, min(clientSize.x, clientSize.y) / 2.0)
        val phiScaleMapper = Mappers.mul(phiRangeNorm, 2.0 * PI)
        val center = clientSize.mul(0.5)

        val norm = when (theta) {
            "x" -> DoubleVector(phiNorm, rNorm)
            "y" -> DoubleVector(rNorm, phiNorm)
            else -> error("Unsupported theta: expected `x` or `y`, but was `$theta`")
        }

        fun scalerThetaX(v: DoubleVector) = rScaleMapper(v.y) to phiScaleMapper(v.x)
        fun scalerThetaY(v: DoubleVector) = rScaleMapper(v.x) to phiScaleMapper(v.y)

        val scaler = when (theta) {
            "x" -> ::scalerThetaX
            "y" -> ::scalerThetaY
            else -> error("Unsupported theta: expected `x` or `y`, but was `$theta`")
        }

        val polarProjection = object : Projection {
            override val nonlinear: Boolean = true

            override fun project(v: DoubleVector): DoubleVector {
                val (r, phi) = scaler(v.add(norm))
                checkNotNull(r)
                checkNotNull(phi)

                val x = r * cos(-phi + (PI / 2.0))
                val y = r * sin(-phi + (PI / 2.0))
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