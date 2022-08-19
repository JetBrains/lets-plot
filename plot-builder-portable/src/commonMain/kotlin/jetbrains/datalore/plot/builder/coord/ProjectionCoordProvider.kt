/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangles.boundingBox
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection

internal class ProjectionCoordProvider(
    override val projection: Projection,
    xLim: DoubleSpan?,
    yLim: DoubleSpan?,
    flipped: Boolean
) : CoordProviderBase(xLim, yLim, flipped) {

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
            ).mapNotNull(projection::project)
        )
            ?: error("adjustGeomSize() - can't compute bbox")

        val domainRatio = bbox.width / bbox.height

        return FixedRatioCoordProvider.reshapeGeom(geomSize, domainRatio)
    }
}
