/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleBreaks

interface CoordProvider {
    val flipAxis: Boolean

    fun with(
        xLim: ClosedRange<Double>?,
        yLim: ClosedRange<Double>?,
        flipped: Boolean
    ): CoordProvider

    fun createCoordinateSystem(
        xDomain: ClosedRange<Double>,
        xAxisLength: Double,
        yDomain: ClosedRange<Double>,
        yAxisLength: Double
    ): CoordinateSystem

    fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: ClosedRange<Double>,
        axisLength: Double,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: ClosedRange<Double>,
        axisLength: Double,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun adjustDomains(
        hDomain: ClosedRange<Double>,
        vDomain: ClosedRange<Double>,
    ): Pair<ClosedRange<Double>, ClosedRange<Double>>

    fun adjustGeomSize(
        hDomain: ClosedRange<Double>,
        vDomain: ClosedRange<Double>,
        geomSize: DoubleVector
    ): DoubleVector
}
