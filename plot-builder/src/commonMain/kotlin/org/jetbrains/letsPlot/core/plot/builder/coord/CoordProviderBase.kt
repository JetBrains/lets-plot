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

abstract class CoordProviderBase(
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


    override fun withXlimOverride(xlimOverride: Pair<Double?, Double?>): CoordProvider {
        if (xlimOverride.first == null && xlimOverride.second == null) return this
        val newXLim = Pair(
            xlimOverride.first ?: xLim.first,
            xlimOverride.second ?: xLim.second,
        )
        return with(newXLim, yLim, flipped)
    }

    override fun withYlimOverride(ylimOverride: Pair<Double?, Double?>): CoordProvider {
        if (ylimOverride.first == null && ylimOverride.second == null) return this
        val newYLim = Pair(
            ylimOverride.first ?: xLim.first,
            ylimOverride.second ?: xLim.second,
        )
        return with(xLim, newYLim, flipped)
    }

    final override fun adjustDomain(dataDomain: DoubleRectangle): DoubleRectangle {
        val xSpan = DoubleSpan(
            xLim.first ?: dataDomain.left,
            xLim.second ?: dataDomain.right
        )

        val ySpan = DoubleSpan(
            yLim.first ?: dataDomain.top,
            yLim.second ?: dataDomain.bottom
        )

        return adjustXYDomains(xSpan, ySpan)
    }

    protected open fun adjustXYDomains(xDomain: DoubleSpan, yDomain: DoubleSpan): DoubleRectangle {
        val dataDomain = DoubleRectangle(xDomain, yDomain)
        val validDomain = projection.validDomain().intersect(dataDomain)

        return if (validDomain != null && validDomain.height > 0.0 && validDomain.width > 0.0) {
            validDomain
        } else {
            throw IllegalArgumentException(
                """Can't create a valid domain.
                |  data bbox: $dataDomain
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
