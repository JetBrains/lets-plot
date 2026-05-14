/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

// A full measured text block: immutable block layout plus the maximum line width.
class MeasuredText internal constructor(
    val layout: TextBlockLayout,
    val width: Double,
) {
    init {
        require(width >= 0.0) { "MeasuredText width must be non-negative." }
    }

    val totalHeight: Double get() = layout.blockHeight

    val totalSize: DoubleVector get() = DoubleVector(width, totalHeight)
}
