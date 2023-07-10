/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan

/**
 * A fixed scale coordinate system forces a specified ratio between the physical representation of data units on the axes.
 * The ratio represents the number of units on the y-axis equivalent to one unit on the x-axis.
 * ratio = 1, ensures that one unit on the x-axis is the same length as one unit on the y-axis.
 * Ratios higher than one make units on the y axis longer than units on the x-axis, and vice versa.
 */
internal open class FixedRatioCoordProvider(
    private val ratio: Double,
    xLim: DoubleSpan?,
    yLim: DoubleSpan?,
    flipped: Boolean
) : CoordProviderBase(xLim, yLim, flipped) {
    override fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider {
        return FixedRatioCoordProvider(ratio, xLim, yLim, flipped)
    }

    override fun adjustGeomSize(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        geomSize: DoubleVector
    ): DoubleVector {
        // Adjust geom dimensions ratio.
        val domainRatio = hDomain.length / vDomain.length

        // Account for the 'ratio':
        // ratio == 1 -> X-units equal Y-units
        // ratio > 1 -> Y-units are longer
        // ratio < 1 -> X-units are longer
        val effectiveDomainRatio = domainRatio / ratio
        return reshapeGeom(geomSize, effectiveDomainRatio)
    }


    companion object {

        fun reshapeGeom(
            geomSize: DoubleVector,
            targetWidthToHeightRatio: Double
        ): DoubleVector {
            val geomRatio = geomSize.x / geomSize.y
            val newSize = if (targetWidthToHeightRatio > geomRatio) {
                // adjust geom height
                val h = geomSize.x / targetWidthToHeightRatio
                DoubleVector(geomSize.x, h)
            } else {
                // adjust geom width
                val w = geomSize.y * targetWidthToHeightRatio
//                val hDelta = geomSize.x - w
                DoubleVector(w, geomSize.y)
            }

            return newSize
        }
    }
}
