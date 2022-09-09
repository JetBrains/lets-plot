/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.base.spatial.projections.identity
import jetbrains.datalore.plot.base.coord.CoordinatesMapper

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

    /**
     * Reshape and flip the domain if necessary.
     */
    final override fun adjustDomain(domain: DoubleRectangle): DoubleRectangle {
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

    final override fun createCoordinateMapper(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinatesMapper {
        return CoordinatesMapper.create(adjustedDomain, clientSize, projection, flipped)
    }
}
