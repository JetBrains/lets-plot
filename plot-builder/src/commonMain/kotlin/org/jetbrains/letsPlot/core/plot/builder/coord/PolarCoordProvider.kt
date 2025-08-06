/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.coord.CoordinatesMapper
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers.IDENTITY
import kotlin.math.*

const val R_EXPAND = 0.15
const val R_PADDING = 0.06

class PolarCoordProvider(
    xLim: Pair<Double?, Double?>,
    yLim: Pair<Double?, Double?>,
    xReversed: Boolean,
    yReversed: Boolean,
    flipped: Boolean,
    val start: Double,
    val clockwise: Boolean,
    val transformBkgr: Boolean,
    val isHScaleContinuous: Boolean = true,
    val isTest: Boolean = false
) : CoordProviderBase(
    xLim,
    yLim,
    xReversed = xReversed,
    yReversed = yReversed,
    flipped = flipped
) {

    override val isLinear: Boolean = false
    override val isPolar: Boolean = true

    override fun with(
        xLim: Pair<Double?, Double?>,
        yLim: Pair<Double?, Double?>,
        xReversed: Boolean,
        yReversed: Boolean,
        flipped: Boolean
    ): CoordProvider {
        return PolarCoordProvider(
            xLim,
            yLim,
            xReversed = xReversed,
            yReversed = yReversed,
            flipped = flipped,
            start = start,
            clockwise = clockwise,
            transformBkgr = transformBkgr,
            isHScaleContinuous = isHScaleContinuous,
            isTest = isTest,
        )
    }

    fun withHScaleContinuous(b: Boolean): PolarCoordProvider {
        return PolarCoordProvider(
            xLim,
            yLim,
            xReversed = xReversed,
            yReversed = yReversed,
            flipped = flipped,
            start = start,
            clockwise = clockwise,
            transformBkgr = transformBkgr,
            isHScaleContinuous = b,
            isTest = isTest,
        )
    }

    override fun adjustXYDomains(xDomain: DoubleSpan, yDomain: DoubleSpan): DoubleRectangle {
        val dataDomain = DoubleRectangle(xDomain, yDomain)

        // Data space -> View space
        val hvDomain = dataDomain.flipIf(flipped)

        // Domain of data without any adjustments (i.e., no expand).
        // For theta, leave the lower end as it is to avoid a hole in the centre and to maintain the correct start angle.
        // Extend the upper end of the radius by 0.15 to allow space for labels and axis line.

        val adjustedHDomain = hvDomain.xRange().let { hDomain ->
            // For discrete scale add an extra segment by increasing domain by 1
            // so that the last point won't overlap with the first one
            // in contrast to the continuous scale where the last point
            // has the same coordinate as the first one
            // i.e. ['a', 'b', 'c'] instead of [360/0, 180]
            val upperExpand = if (isHScaleContinuous) 0.0 else 1.0
            DoubleSpan.withLowerEnd(hDomain.lowerEnd, hDomain.length + upperExpand)
        }

        val adjustedVDomain = hvDomain.yRange().let { vDomain ->
            val radiusScaler = if (isTest) 1.0 else 1 + R_EXPAND + R_PADDING
            DoubleSpan.withLowerEnd(vDomain.lowerEnd, vDomain.length * radiusScaler)
        }

        return DoubleRectangle(
            adjustedHDomain, //theta
            adjustedVDomain // r
        )
            // View space -> Data space
            // adjustXYDomains() must return bounds in "data space".
            .flipIf(flipped)
    }

    override fun adjustGeomSize(hDomain: DoubleSpan, vDomain: DoubleSpan, geomSize: DoubleVector): DoubleVector {
        return min(geomSize.x, geomSize.y).let { DoubleVector(it, it) }
    }

    override fun createCoordinateMapper(adjustedDomain: DoubleRectangle, clientSize: DoubleVector): CoordinatesMapper {
        val normOffset = DoubleVector.ZERO.subtract(adjustedDomain.origin)
        val normDomain = DoubleRectangle(DoubleVector.ZERO, adjustedDomain.dimension)

        val thetaScaleMapper = Mappers.mul(normDomain.xRange(), 2.0 * PI)
        val rScaleMapper = Mappers.mul(normDomain.yRange(), min(clientSize.x, clientSize.y) / 2.0)

        val inversedThetaScaleMapper = Mappers.mul((1 / (thetaScaleMapper(1.0)!!)).takeIf { it.isFinite() } ?: 0.0)
        val inversedRScaleMapper = Mappers.mul((1 / rScaleMapper(1.0)!!).takeIf { it.isFinite() } ?: 0.0)

        val sign = if (clockwise) -1.0 else 1.0
        val startAngle = PI / 2.0 + sign * start
        val center = clientSize.mul(0.5)

        val polarProjection = object : Projection {
            override val nonlinear: Boolean = true

            override fun project(v: DoubleVector): DoubleVector {
                // FixMe: polar hack: a `Projection` must not do "flip"
                val normV = v.flipIf(flipped).add(normOffset)

                val theta = thetaScaleMapper(normV.x) ?: error("Unexpected: theta is null")
                val r = rScaleMapper(normV.y) ?: error("Unexpected: r is null")

                val x = r * cos(sign * theta + startAngle)
                val y = r * sin(sign * theta + startAngle)
                return center.add(DoubleVector(x, y))
            }

            override fun invert(v: DoubleVector): DoubleVector {
                val normV = v.subtract(center)

                val r = normV.length()
                val theta = atan2(normV.y, normV.x)

                val adjustedTheta = (theta - startAngle) * sign

                val x = inversedThetaScaleMapper(adjustedTheta) ?: error("Unexpected: x is null")
                val y = inversedRScaleMapper(r) ?: error("Unexpected: y is null")

                return DoubleVector(x, y).subtract(normOffset).flipIf(flipped)
            }

            override fun validDomain(): DoubleRectangle = adjustedDomain
        }
        val clientBounds = DoubleRectangle(DoubleVector.ZERO, clientSize)

        return CoordinatesMapper(
            hScaleMapper = IDENTITY,
            hScaleInverseMapper = IDENTITY,
            vScaleMapper = IDENTITY,
            vScaleInverseMapper = IDENTITY,
            clientBounds = clientBounds,
            projection = polarProjection,
            flipAxis = false
        )
    }

    override fun createCoordinateSystem(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector
    ): PolarCoordinateSystem {
        val sign = if (clockwise) -1.0 else 1.0
        val coordMapper = createCoordinateMapper(adjustedDomain, clientSize)
        return PolarCoordinateSystem(Coords.create(coordMapper), start, sign, transformBkgr)
    }


    fun gridDomain(adjustedDomain: DoubleRectangle): DoubleRectangle {
        val xRange = adjustedDomain.xRange() // either xLim or domain.xRange() with adjustments
        val yRange = adjustedDomain.yRange().let { DoubleSpan.withLowerEnd(it.lowerEnd, it.length / (1 + R_EXPAND)) }

        return DoubleRectangle(xRange, yRange)
    }
}


class PolarCoordinateSystem internal constructor(
    private val coordinateSystem: CoordinateSystem,
    val startAngle: Double,
    val direction: Double,
    val transformBkgr: Boolean
) : CoordinateSystem {
    override val isLinear: Boolean get() = false
    override val isPolar: Boolean get() = true

    override fun toClient(p: DoubleVector): DoubleVector? = coordinateSystem.toClient(p)
    override fun fromClient(p: DoubleVector): DoubleVector? = coordinateSystem.fromClient(p)

    override fun unitSize(p: DoubleVector): DoubleVector = coordinateSystem.unitSize(p)

    override fun flip(): CoordinateSystem =
        PolarCoordinateSystem(coordinateSystem.flip(), startAngle, direction, transformBkgr)
}
