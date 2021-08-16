/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale


object ScaleBreaksUtil {
    fun <TargetT> withBreaks(
        scale: Scale<TargetT>,
        transformedDomain: ClosedRange<Double>,
        breakCount: Int
    ): Scale<TargetT> {
        val scaleBreaks = scale.getBreaksGenerator().generateBreaks(transformedDomain, breakCount)
        val breaks = scaleBreaks.domainValues
        val labels = scaleBreaks.labels
        return scale.with()
            .breaks(breaks)
            .labels(labels)
            .build()
    }
}
