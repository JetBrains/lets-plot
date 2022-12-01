/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.guide.Orientation

// ToDo: remove <T>
interface ScaleProvider<T> {
    val discreteDomain: Boolean
    val discreteDomainReverse: Boolean
    val breaks: List<Any>?
    val limits: List<Any?>? // when 'continuous' limits, NULL means undefined upper or lower limit.
    val continuousTransform: ContinuousTransform
    val axisOrientation: Orientation?

    /**
     * Create scale for discrete input (domain)
     */
    fun createScale(defaultName: String, discreteTransform: DiscreteTransform): Scale<T>

    /**
     * Create scale for continuous (numeric) input (domain)
     */
    fun createScale(
        defaultName: String,
        continuousTransform: ContinuousTransform,
        continuousRange: Boolean,
        guideBreaks: WithGuideBreaks<Any>?
    ): Scale<T>
}
