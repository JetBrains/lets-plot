/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleUtil.breaksTransformed
import jetbrains.datalore.plot.base.scale.ScaleUtil.getBreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleUtil.labels

object AxisBreaksUtil {
    fun createAxisBreaksProvider(scale: Scale<Double>, axisDomain: ClosedRange<Double>): AxisBreaksProvider = when {
        scale.hasBreaks() -> FixedAxisBreaksProvider(scale.breaks, breaksTransformed(scale), labels(scale))
        else -> AdaptableAxisBreaksProvider(axisDomain, getBreaksGenerator(scale))
    }
}
