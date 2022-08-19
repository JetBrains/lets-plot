/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.CoordinatesMapper
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.scale.ScaleBreaks

interface CoordProvider {
    val xLim: DoubleSpan?
    val yLim: DoubleSpan?
    val flipAxis: Boolean
    val projection: Projection

    fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider

    /**
     * Reshape and flip the domain if necessary
     */
    fun adjustDomain(domain: DoubleRectangle): DoubleRectangle {
        val validDomain = domain.let {
            val withLims = DoubleRectangle(
                xLim ?: domain.xRange(),
                yLim ?: domain.yRange(),
            )
            projection.validDomain().intersect(withLims)
        }

        return if (validDomain != null && validDomain.height > 0.0 && validDomain.width > 0.0) {
            if (flipAxis) validDomain.flip() else validDomain
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

    fun createCoordinateMapper(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinatesMapper {
        val geomSize = adjustGeomSize(
            hDomain = adjustedDomain.xRange(),
            vDomain = adjustedDomain.yRange(),
            geomSize = clientSize
        )

        return CoordinatesMapper(adjustedDomain, geomSize, projection)
    }

    fun createCoordinateSystem(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinateSystem {
        val coordMapper = createCoordinateMapper(adjustedDomain, clientSize)
        return Coords.create(coordMapper)
    }

    fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        yDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        xDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun adjustGeomSize(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        geomSize: DoubleVector
    ): DoubleVector
}
