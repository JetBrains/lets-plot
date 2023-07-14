/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.builder.scale.DefaultNaValue

class SizeMapperProvider(
    range: DoubleSpan,
    naValue: Double
) : LinearNormalizingMapperProvider(range, naValue) {

    companion object {
        private val DEF_RANGE = DoubleSpan(
            AesScaling.sizeFromCircleDiameter(3.0),
            AesScaling.sizeFromCircleDiameter(21.0)
        )

        val DEFAULT = SizeMapperProvider(
            DEF_RANGE,
            DefaultNaValue[SIZE]
        )
    }
}