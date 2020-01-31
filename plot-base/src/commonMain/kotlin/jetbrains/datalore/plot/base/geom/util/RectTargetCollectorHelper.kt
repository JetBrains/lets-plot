/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params

class RectTargetCollectorHelper(
    private val rectanglesHelper: RectanglesHelper,
    private val rectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle?,
    private val fillByDataPoint: (DataPointAesthetics) -> Color
) {

    fun collectTo(targetCollector: GeomTargetCollector) {
        rectanglesHelper.iterateRectangleGeometry(rectangleByDataPoint)
        { p, rectangle ->
            targetCollector.addRectangle(p.index(), rectangle, tooltipParams(p))
        }
    }

    private fun tooltipParams(p: DataPointAesthetics): TooltipParams {
        val params = params()
        params.setColor(fillByDataPoint(p))
        return params
    }
}
