/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangles.boundingBox
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.interval.DoubleSpan

internal class ProjectionCoordProvider(
    projection: Projection,
    xLim: DoubleSpan?,
    yLim: DoubleSpan?,
    flipped: Boolean
) : CoordProviderBase(xLim, yLim, flipped, projection) {

    override fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider {
        return ProjectionCoordProvider(projection, xLim, yLim, flipped)
    }

    override fun adjustGeomSize(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        geomSize: DoubleVector
    ): DoubleVector {
        // Adjust geom dimensions ratio.
        val bbox = boundingBox(
            listOf(
                DoubleVector(hDomain.lowerEnd, vDomain.lowerEnd),
                DoubleVector(hDomain.lowerEnd, vDomain.upperEnd),
                DoubleVector(hDomain.upperEnd, vDomain.lowerEnd),
                DoubleVector(hDomain.upperEnd, vDomain.upperEnd)
            )
                .map {
                    if (flipped) it.flip() else it
                }.mapNotNull(projection::project)
                .map {
                    if (flipped) it.flip() else it
                }
        ) ?: error("adjustGeomSize() - can't compute bbox")

        val domainRatio = bbox.width / bbox.height
        return FixedRatioCoordProvider.reshapeGeom(geomSize, domainRatio)
    }
}
