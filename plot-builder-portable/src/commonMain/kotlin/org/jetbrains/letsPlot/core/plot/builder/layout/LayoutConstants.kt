/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets

internal object LayoutConstants {
    val GEOM_MIN_SIZE = DoubleVector(50.0, 50.0)

    const val GEOM_AREA_PADDING = 10.0

    val LIVE_MAP_PLOT_PADDING = DoubleVector(10.0, 0.0)
    val LIVE_MAP_PLOT_MARGIN = DoubleVector(10.0, 10.0)

    // Horizontal axis labels
    const val H_AXIS_LABELS_EXPAND = 10.0  // Value by which labels can go beyond the axis bounds (px).

    // Facets layout
    // Allow axis labels to exceed dimensions of the panel.
    val FACET_PANEL_AXIS_EXPAND = Insets(5.0, 5.0, 5.0, 5.0)
}