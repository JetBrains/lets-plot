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
 * TODO: merge with regular LegendBoxInfo.
 */
class DetachedLegendBoxInfo(
    val legendBoxInfo: LegendBoxInfo,
    val position: LegendPosition,
    val justification: LegendJustification
)
