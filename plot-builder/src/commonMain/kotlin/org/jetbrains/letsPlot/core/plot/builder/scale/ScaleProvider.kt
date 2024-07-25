/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.Scale

interface ScaleProvider {
    val discreteDomain: Boolean
    val discreteDomainReverse: Boolean
    val breaks: List<Any>?
    val limits: List<Any?>? // when 'continuous' limits, NULL means undefined upper or lower limit.
    val continuousTransform: ContinuousTransform
    val axisPosition: AxisPosition

    /**
     * Create scale for discrete input (domain)
     */
    fun createScale(defaultName: String, discreteTransform: DiscreteTransform, guideTitle: String?): Scale

    /**
     * Create scale for continuous (numeric) input (domain)
     */
    fun createScale(
        defaultName: String,
        continuousTransform: ContinuousTransform,
        continuousRange: Boolean,
        guideBreaks: WithGuideBreaks<Any>?,
        guideTitle: String?
    ): Scale
}
