/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.core.plot.base.ScaleMapper

/**
 * `isContinuous` is TRUE if both, domain and range are continuous.
 */
open class GuideMapper<TargetT>(
    private val mapper: ScaleMapper<TargetT>,
    val isContinuous: Boolean
) : ScaleMapper<TargetT> {
    override fun invoke(v: Double?): TargetT? = mapper(v)
}
