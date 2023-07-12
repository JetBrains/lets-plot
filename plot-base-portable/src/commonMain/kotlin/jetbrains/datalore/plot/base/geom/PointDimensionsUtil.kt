/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

object PointDimensionsUtil {
    fun dimensionSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        sizeAes: Aes<Double>,
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