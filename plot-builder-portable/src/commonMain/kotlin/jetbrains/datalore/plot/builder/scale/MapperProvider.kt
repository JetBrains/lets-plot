/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper

interface MapperProvider<T> {
    /**
     * Create mapper with discrete input (domain)
     */
    fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<T>

    /**
     * Create mapper with continuous (numeric) input (domain)
     */
    fun createContinuousMapper(
        domain: DoubleSpan,
        trans: ContinuousTransform
    ): GuideMapper<T>
}
