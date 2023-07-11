/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.plot.builder.scale.DefaultNaValue

class AlphaMapperProvider(
    range: DoubleSpan,
    naValue: Double
) : LinearNormalizingMapperProvider(range, naValue) {

    companion object {
        private val DEF_RANGE = DoubleSpan(0.1, 1.0)

        val DEFAULT = AlphaMapperProvider(
            DEF_RANGE,
            DefaultNaValue[ALPHA]
        )
    }
}