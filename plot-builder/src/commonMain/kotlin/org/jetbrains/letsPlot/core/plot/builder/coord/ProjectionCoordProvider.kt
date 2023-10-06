/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.coord.projectDomain

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
        val bbox = DoubleRectangle(hDomain, vDomain)
            .let { if (flipped) it.flip() else it }
            .let { projectDomain(projection, it) }
            .let { if (flipped) it.flip() else it }

        val domainRatio = bbox.width / bbox.height
        return FixedRatioCoordProvider.reshapeGeom(geomSize, domainRatio)
    }
}
