/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

object DimensionsUtil {
    fun dimensionSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        sizeAes: Aes<Double>,
        resolution: Double,
        unit: DimensionUnit
    ): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val size = p.finiteOrNull(sizeAes) ?: return null
        val expand = when (unit) {
            DimensionUnit.RESOLUTION -> size * resolution / 2.0
            DimensionUnit.IDENTITY -> size / 2.0
            else -> 0.0 // If the units are "absolute" (e.g. pixels), we don't use expand
        }
        return DoubleSpan(
            loc - expand,
            loc + expand
        )
    }
}