/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.scale.BreaksGenerator

internal class AdaptableAxisBreaksProvider(
    private val domainAfterTransform: ClosedRange<Double>,
    private val breaksGenerator: BreaksGenerator
) : AxisBreaksProvider {

    override val isFixedBreaks: Boolean
        get() = false

    override val fixedBreaks: GuideBreaks
        get() = throw IllegalStateException("Not a fixed breaks provider")

    override fun getBreaks(targetCount: Int, axisLength: Double): GuideBreaks {
        val scaleBreaks = breaksGenerator.generateBreaks(domainAfterTransform, targetCount)
        return GuideBreaks(
            scaleBreaks.domainValues,
            scaleBreaks.transformValues,
            scaleBreaks.labels
        )
    }
}
