/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform

interface MapperProvider<T> {
    /**
     * Create mapper with discrete input (domain)
     */
    fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<T>

    /**
     * Create mapper with continuous (numeric) input (domain)
     */
    fun createContinuousMapper(
        domain: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: ContinuousTransform
    ): GuideMapper<T>
}
