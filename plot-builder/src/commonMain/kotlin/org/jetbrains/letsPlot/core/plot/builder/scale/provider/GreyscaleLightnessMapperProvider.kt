/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.colormodel.HCL
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper

class GreyscaleLightnessMapperProvider(
    start: Double,
    end: Double,
    naValue: Color
) : HclColorMapperProvider(naValue) {

    private val from: HCL
    private val to: HCL

    init {
        require(start in (0.0..1.0)) { "Value of 'start' must be in range: [0,1]: $start" }
        require(end in (0.0..1.0)) { "Value of 'end' must be in range: [0,1]: $end" }

        from = HCL(h = 0.0, c = 0.0, l = start * 100.0)
        to = HCL(h = 0.0, c = 0.0, l = end * 100.0)
    }

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        return createDiscreteMapper(discreteTransform.effectiveDomainTransformed, from, to)
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        return createContinuousMapper(
            domain,
            from,
            to
        )
    }

    companion object {
        const val DEF_START = 0.2
        const val DEF_END = 0.8
    }
}
