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
    private val xLim: DoubleSpan?,
    private val yLim: DoubleSpan?,
    override val flipped: Boolean,
    protected val projection: Projection = identity(),
) : CoordProvider {

    init {
        require(xLim == null || xLim.length > 0.0) { "Coord x-limits range should be > 0.0" }
        require(yLim == null || yLim.length > 0.0) { "Coord y-limits range should be > 0.0" }
    }

    override val isLinear: Boolean = !projection.nonlinear

    /**
     * Reshape and flip the domain if necessary.
     */
    override fun adjustDomain(domain: DoubleRectangle): DoubleRectangle {
        val validDomain = domain.let {
            val withLims = DoubleRectangle(
                xLim ?: domain.xRange(),
                yLim ?: domain.yRange(),
            )
            projection.validDomain().intersect(withLims)
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
