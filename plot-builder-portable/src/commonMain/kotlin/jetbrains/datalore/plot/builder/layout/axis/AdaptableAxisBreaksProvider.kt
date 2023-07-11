/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks

internal class AdaptableAxisBreaksProvider(
    private val domainAfterTransform: DoubleSpan,
    private val breaksGenerator: BreaksGenerator
) : AxisBreaksProvider {

    override val isFixedBreaks: Boolean
        get() = false

    override val fixedBreaks: ScaleBreaks
        get() = throw IllegalStateException("Not a fixed breaks provider")

    override fun getBreaks(targetCount: Int, axisLength: Double): ScaleBreaks {
        @Suppress("UnnecessaryVariable")
        val scaleBreaks = breaksGenerator.generateBreaks(domainAfterTransform, targetCount)
        return scaleBreaks
    }
}
