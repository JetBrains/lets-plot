/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

class RectTargetCollectorHelper(
    private val rectanglesHelper: RectanglesHelper,
    private val tooltipKind: TipLayoutHint.Kind,
    private val colorsByDataPoint: (DataPointAesthetics) -> List<Color>
) {
    fun collectTo(targetCollector: GeomTargetCollector) {
        rectanglesHelper.iterateRectangleGeometry()
        { p, rectangle ->
            targetCollector.addRectangle(
                p.index(),
                rectangle,
                GeomTargetCollector.TooltipParams(
                    markerColors = colorsByDataPoint(p)
                ),
                tooltipKind
            )
        }
    }
}
