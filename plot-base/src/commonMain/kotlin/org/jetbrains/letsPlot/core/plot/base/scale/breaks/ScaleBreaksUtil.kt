/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Scale


object ScaleBreaksUtil {
    fun withBreaks(
        scale: Scale,
        transformedDomain: DoubleSpan,
        breakCount: Int
    ): Scale {
        val scaleBreaks = scale.getBreaksGenerator().generateBreaks(transformedDomain, breakCount)
        val breaks = scaleBreaks.domainValues
        val labels = scaleBreaks.labels
        return scale.with()
            .breaks(breaks)
            .labels(labels)
            .build()
    }
}
