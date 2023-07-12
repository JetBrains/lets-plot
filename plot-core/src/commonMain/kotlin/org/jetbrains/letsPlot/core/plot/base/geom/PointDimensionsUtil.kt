/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

object PointDimensionsUtil {
    fun dimensionSpan(
        p: DataPointAesthetics,
        coordAes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>,
        sizeAes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>,
        resolution: Double
    ): DoubleSpan? {

        val loc = p[coordAes]
        val size = p[sizeAes]

        return if (SeriesUtil.allFinite(loc, size)) {
            loc!!
            val expand = resolution * size!! / 2
            DoubleSpan(
                loc - expand,
                loc + expand
            )
        } else {
            null
        }
    }
}