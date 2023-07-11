/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.coord.CoordinatesMapper
import jetbrains.datalore.plot.base.coord.Coords

interface CoordProvider {
    val flipped: Boolean

    fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider

    /**
     * Reshape and flip the domain if necessary.
     */
    fun adjustDomain(domain: DoubleRectangle): DoubleRectangle

    fun adjustGeomSize(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        geomSize: DoubleVector
    ): DoubleVector

    fun createCoordinateMapper(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinatesMapper

    fun createCoordinateSystem(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinateSystem {
        val coordMapper = createCoordinateMapper(adjustedDomain, clientSize)
        return Coords.create(coordMapper)
    }
}
