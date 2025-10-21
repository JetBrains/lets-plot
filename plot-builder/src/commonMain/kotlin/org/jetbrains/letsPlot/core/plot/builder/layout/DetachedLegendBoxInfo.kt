/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition

/**
 * A wrapper around LegendBoxInfo that includes position and justification information.
 * This allows legend boxes to be "detached" from individual plots and collected at the
 * composite figure level, while preserving their positioning metadata.
 *
 * @param legendBoxInfo The actual legend box (content and rendering)
 * @param position Where the legend should be positioned (LEFT, RIGHT, TOP, BOTTOM)
 * @param justification How the legend should be aligned at that position
 */
class DetachedLegendBoxInfo(
    val legendBoxInfo: LegendBoxInfo,
    val position: LegendPosition,
    val justification: LegendJustification
)
