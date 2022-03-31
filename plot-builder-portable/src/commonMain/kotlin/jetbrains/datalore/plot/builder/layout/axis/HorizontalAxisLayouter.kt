/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.axis.label.AxisLabelsLayout

class HorizontalAxisLayouter constructor(
    orientation: Orientation,
    domainRange: DoubleSpan,
    labelsLayout: AxisLabelsLayout
) : AxisLayouter(orientation, domainRange, labelsLayout) {

    override fun toAxisMapper(axisLength: Double): (Double?) -> Double? {
        val scaleMapper = toScaleMapper(axisLength)
        val cartesianX = Coords.toClientOffsetX(DoubleSpan(0.0, axisLength))
        return { v ->
            val mapped = scaleMapper(v)
            if (mapped != null) cartesianX(mapped) else null
        }
    }
}
