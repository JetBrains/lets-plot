/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.scale.DefaultNaValue

class LinewidthMapperProvider(
    range: DoubleSpan,
    naValue: Double
) : LinearNormalizingMapperProvider(range, naValue) {
    companion object {
        private val DEF_RANGE = DoubleSpan(1.0, 7.0)

        val DEFAULT = LinewidthMapperProvider(
            DEF_RANGE,
            DefaultNaValue[Aes.LINEWIDTH]
        )
    }
}