/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.builder.layout.DetachedLegendBoxInfo

/**
 * Collects detached legends from individual plots when building a composite figure.
 */
class DetachedLegendsCollector(
    val detachOverlayLegends: Boolean = false
) {
    private val _collectedLegends = mutableListOf<DetachedLegendBoxInfo>()
    val collectedLegends: List<DetachedLegendBoxInfo>
        get() = _collectedLegends.toList()

    fun collect(legends: List<DetachedLegendBoxInfo>) {
        _collectedLegends.addAll(legends)
    }

    fun groupedByPosition(): Map<LegendPosition, List<DetachedLegendBoxInfo>> {
        return _collectedLegends.groupBy { it.position }
    }
}