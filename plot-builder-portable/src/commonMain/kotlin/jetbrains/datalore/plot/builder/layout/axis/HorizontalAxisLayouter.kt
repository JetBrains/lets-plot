/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.axis.label.AxisLabelsLayout

internal class HorizontalAxisLayouter constructor(
    orientation: Orientation,
    domainRange: DoubleSpan,
    labelsLayout: AxisLabelsLayout
) : AxisLayouter(orientation, domainRange, labelsLayout) {

    override fun toAxisMapper(axisLength: Double): (Double?) -> Double? {
        val scaleMapper = toScaleMapper(axisLength)
        return { v -> scaleMapper(v) }
    }
}
