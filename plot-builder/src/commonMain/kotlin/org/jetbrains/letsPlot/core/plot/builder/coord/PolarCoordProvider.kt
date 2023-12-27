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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal class PolarCoordProvider(
    flipped: Boolean,
    val start: Double,
    val clockwise: Boolean
) : CoordProviderBase(xLim = null, yLim = null, flipped) {

    override val isLinear: Boolean
        get() = false

    // TODO: polar coord actually does not support flipped and xLim/yLim
    override fun with(xLim: DoubleSpan?, yLim: DoubleSpan?, flipped: Boolean): CoordProvider {
        return PolarCoordProvider(!flipped, start, clockwise)
    }

    override fun adjustDomain(domain: DoubleRectangle): DoubleRectangle {
        // Domain of a data withouth any adjustments (i.e. no expand).
        // Keep lower end as is to avoid hole in the center and to keep correct start angle.
        // Extend upper end of the radius domain by 0.15 to make room for labels and axis line.

        val thetaDomain = domain.xRange()
        val rDomain = domain.yRange()
        val adjustedRDomain = DoubleSpan(rDomain.lowerEnd, rDomain.upperEnd + rDomain.length * 0.15)

        return DoubleRectangle(thetaDomain, adjustedRDomain).flipIf(flipped)
    }

    override fun adjustGeomSize(hDomain: DoubleSpan, vDomain: DoubleSpan, geomSize: DoubleVector): DoubleVector {
        return min(geomSize.x, geomSize.y).let { DoubleVector(it, it) }
    }

    override fun createCoordinateMapper(adjustedDomain: DoubleRectangle, clientSize: DoubleVector): CoordinatesMapper {
        val polarProjection = object : Projection {
            val domain = adjustedDomain.flipIf(flipped)

            val thetaDomain = domain.xRange()
            val rDomain = domain.yRange()

            val rNorm = 0.0 - rDomain.lowerEnd
            val thetaNorm = 0.0 - thetaDomain.lowerEnd
            val norm = DoubleVector(thetaNorm, rNorm)

            val rDomainNorm = DoubleSpan(0.0, rDomain.upperEnd + rNorm)
            val thetaDomainNorm = DoubleSpan(0.0, thetaDomain.upperEnd + thetaNorm)

            val rScaleMapper = Mappers.mul(rDomainNorm, min(clientSize.x, clientSize.y) / 2.0)
            val thetaScaleMapper = Mappers.mul(thetaDomainNorm, 2.0 * PI)

            val sign = if (clockwise) -1.0 else 1.0
            val startAngle = PI / 2.0 + sign * start

            val center = clientSize.mul(0.5)

            override val nonlinear: Boolean = true

            override fun project(v: DoubleVector): DoubleVector {
                val normalized = v.add(norm)
                val theta = thetaScaleMapper(normalized.x)
                val r = rScaleMapper(normalized.y)

                checkNotNull(theta)
                checkNotNull(r)

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

    override fun createCoordinateSystem(adjustedDomain: DoubleRectangle, clientSize: DoubleVector): CoordinateSystem {
        val coordMapper = createCoordinateMapper(adjustedDomain, clientSize)
        return PolarCoordinateSystem(Coords.create(coordMapper))
    }
}


class PolarCoordinateSystem(
    private val coordinateSystem: CoordinateSystem
) : CoordinateSystem {
    override val isLinear: Boolean get() = coordinateSystem.isLinear

    override fun toClient(p: DoubleVector): DoubleVector? = coordinateSystem.toClient(p)

    override fun unitSize(p: DoubleVector): DoubleVector = coordinateSystem.unitSize(p)

    override fun flip(): CoordinateSystem = PolarCoordinateSystem(coordinateSystem.flip())
}
