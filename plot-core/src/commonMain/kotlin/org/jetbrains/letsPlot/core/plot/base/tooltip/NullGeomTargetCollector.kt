/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector.TooltipParams

class NullGeomTargetCollector : GeomTargetCollector {
    override fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
    }

    override fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
    }

    override fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
    }

    override fun addPolygon(
        points: List<DoubleVector>,
        index: Int,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
    }

    override fun withFlippedAxis(): GeomTargetCollector {
        return this
    }

    override fun withYOrientation(): GeomTargetCollector {
        return this
    }
}
