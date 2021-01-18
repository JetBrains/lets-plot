/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

class FixedAxisBreaksProvider(domainBreaks: List<Any>, transformedBreaks: List<Double>, labels: List<String>) :
    AxisBreaksProvider {
    override val fixedBreaks: GuideBreaks =
        GuideBreaks(
            domainBreaks,
            transformedBreaks,
            labels
        )

    override val isFixedBreaks: Boolean
        get() = true

    override fun getBreaks(targetCount: Int, axisLength: Double): GuideBreaks {
        return fixedBreaks
    }
}
