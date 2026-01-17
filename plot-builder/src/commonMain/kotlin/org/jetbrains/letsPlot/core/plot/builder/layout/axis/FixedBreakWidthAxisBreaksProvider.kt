/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks

internal class FixedBreakWidthAxisBreaksProvider(
    private val domainAfterTransform: DoubleSpan,
    private val breaksGenerator: BreaksGenerator
) : AxisBreaksProvider {

    override val isFixedBreaks: Boolean = true
    override val fixedBreaks: ScaleBreaks

    init {
        fixedBreaks = breaksGenerator.generateBreaks(domainAfterTransform, targetCount = -1)
        check(fixedBreaks.fixed) { "Expected 'fixed' scale breaks." }
    }

    override fun getBreaks(targetCount: Int): ScaleBreaks {
        return fixedBreaks
    }
}
