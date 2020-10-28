/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale

interface ScaleProvider<T> {
    val discreteDomain: Boolean

    /**
     * Create scale for discrete input (domain)
     */
    fun createScale(defaultName: String, discreteDomain: Collection<*>): Scale<T>

    /**
     * Create scale for continuous (numeric) input (domain)
     */
    fun createScale(defaultName: String, continuousDomain: ClosedRange<Double>): Scale<T>
}
