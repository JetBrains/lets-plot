/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.scale.BreaksGenerator

class AdaptableAxisBreaksProvider internal constructor(private val myDomainAfterTransform: ClosedRange<Double>, private val myBreaksGenerator: BreaksGenerator) : AxisBreaksProvider {

    override val isFixedBreaks: Boolean
        get() = false

    override val fixedBreaks: GuideBreaks
        get() = throw IllegalStateException("Not a fixed breaks provider")

    override fun getBreaks(targetCount: Int, axisLength: Double): GuideBreaks {
        val scaleBreaks = myBreaksGenerator.generateBreaks(myDomainAfterTransform, targetCount)
        return GuideBreaks(scaleBreaks.domainValues, scaleBreaks.transformValues, scaleBreaks.labels)
    }
}
