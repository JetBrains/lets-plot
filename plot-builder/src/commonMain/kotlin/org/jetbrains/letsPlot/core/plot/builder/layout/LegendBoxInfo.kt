/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.builder.guide.LegendBox

/**
 * The positioning metadata here allows legend boxes to be "detached" from individual plots and collected at the
 * composite figure level.
 */
abstract class LegendBoxInfo(
    internal val size: DoubleVector,
    val position: LegendPosition,
    val justification: LegendJustification
) {
    abstract fun createSvgComponent(): LegendBox
}
