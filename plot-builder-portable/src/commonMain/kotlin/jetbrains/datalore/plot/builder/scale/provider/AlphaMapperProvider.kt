/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.plot.builder.scale.DefaultNaValue

class AlphaMapperProvider(
        range: ClosedRange<Double>,
        naValue: Double) :
        LinearNormalizingMapperProvider(range, naValue) {

    companion object {
        private val DEF_RANGE = ClosedRange(0.1, 1.0)

        val DEFAULT = AlphaMapperProvider(
            DEF_RANGE,
            DefaultNaValue[ALPHA]
        )
    }
}