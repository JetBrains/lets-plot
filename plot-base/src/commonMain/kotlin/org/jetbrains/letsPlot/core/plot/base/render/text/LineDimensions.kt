/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

data class LineDimensions(
    val width: Double,
    val metrics: LineMetrics
) {
    val height: Double get() = metrics.height
    val extent: DoubleVector get() = DoubleVector(width, height)
}