/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks

interface CoordProvider {
    fun createCoordinateSystem(xDomain: ClosedRange<Double>, xAxisLength: Double, yDomain: ClosedRange<Double>, yAxisLength: Double): CoordinateSystem

    fun buildAxisScaleX(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double>

    fun buildAxisScaleY(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double>

    fun adjustDomains(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>, displaySize: DoubleVector): Pair<ClosedRange<Double>, ClosedRange<Double>>
}
