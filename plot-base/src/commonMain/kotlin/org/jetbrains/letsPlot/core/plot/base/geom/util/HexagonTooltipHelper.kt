/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil.createColorMarkerMapper
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.*

class HexagonTooltipHelper(
    private val ctx: GeomContext,
) {
    fun addTarget(p: DataPointAesthetics, hex: List<DoubleVector>) {
        ctx.targetCollector.addPolygon(
            hex,
            p.index(),
            GeomTargetCollector.TooltipParams(
                markerColors = createColorMarkerMapper(null, ctx)(p)
            ),
            tooltipKind = CURSOR_TOOLTIP
        )
    }
}