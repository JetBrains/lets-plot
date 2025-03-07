/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

// TODO: should be removed. Geom should implement WithWidth, flipping should handled by orientation_y
interface WithFlippableWidth {
    val isVertical: Boolean
    fun widthSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan?
}
