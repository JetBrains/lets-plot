/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxInfo

/**
 * Collects detached legends from individual plots when building a composite figure.
 */
class DetachedLegendsCollector(
    val detachOverlayLegends: Boolean = false
) {
    val collectedLegends: List<LegendBoxInfo> = mutableListOf<LegendBoxInfo>()

    fun collect(legends: List<LegendBoxInfo>) {
        (collectedLegends as MutableList<LegendBoxInfo>).addAll(legends)
    }
}