/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.intern.spatial.projections.identity
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.coord.CoordinatesMapper

internal abstract class CoordProviderBase(
    protected val xLim: Pair<Double?, Double?>,
    protected val yLim: Pair<Double?, Double?>,
    override val flipped: Boolean,
    protected val projection: Projection = identity(),
) : CoordProvider {

    init {
        require(
            xLim.first == null || xLim.second == null ||
                    xLim.second!! > xLim.first!!
        ) { "Invalid coord x-limits: $xLim " }
        require(
            yLim.first == null || yLim.second == null ||
                    yLim.second!! > yLim.first!!
        ) { "Invalid coord y-limits: $yLim" }
    }

    override val isLinear: Boolean = !projection.nonlinear
    override val isPolar: Boolean = false

    /**
     * Reshape and flip the domain if necessary.
     */
    final override fun adjustDomain(domain: DoubleRectangle): DoubleRectangle {
        val xSpan = DoubleSpan(
            xLim.first ?: domain.left,
            xLim.second ?: domain.right
        )

        val ySpan = DoubleSpan(
            yLim.first ?: domain.top,
            yLim.second ?: domain.bottom
        )

        return adjustXYDomains(xSpan, ySpan)
    }

    protected open fun adjustXYDomains(xRange: DoubleSpan, yRange: DoubleSpan): DoubleRectangle {
        val domain = DoubleRectangle(xRange, yRange)
        val validDomain = domain.let {
            projection.validDomain().intersect(it)
        }

        return if (validDomain != null && validDomain.height > 0.0 && validDomain.width > 0.0) {
            if (flipped) validDomain.flip() else validDomain
        } else {
            throw IllegalArgumentException(
                """Can't create a valid domain.
                |  data bbox: $domain
                |  x-lim: $xLim
                |  y-lim: $yLim
            """.trimMargin()
            )
        }
    }

    override fun createCoordinateMapper(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinatesMapper {
        return CoordinatesMapper.create(adjustedDomain, clientSize, projection, flipped)
    }
}
