/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale

object AxisBreaksUtil {
    fun createAxisBreaksProvider(scale: Scale<Double>, axisDomain: ClosedRange<Double>): AxisBreaksProvider = when {
        scale.hasBreaks() -> {
            FixedAxisBreaksProvider(scale.getScaleBreaks())
        }
        else -> {
            AdaptableAxisBreaksProvider(
                axisDomain,
                scale.getBreaksGenerator()
            )
        }
    }
}
