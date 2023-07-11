/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.HSV
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper

class GreyscaleLightnessMapperProvider(
    start: Double?,
    end: Double?,
    naValue: Color
) : HSVColorMapperProvider(naValue) {

    private val myFromHSV: HSV
    private val myToHSV: HSV

    init {
        val value0 = start ?: DEF_START
        val value1 = end ?: DEF_END

        require(value0 in (0.0..1.0)) { "Value of 'start' must be in range: [0,1]: $start" }
        require(value1 in (0.0..1.0)) { "Value of 'end' must be in range: [0,1]: $end" }

        myFromHSV = HSV(0.0, 0.0, value0)
        myToHSV = HSV(0.0, 0.0, value1)
    }

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        return createDiscreteMapper(discreteTransform.effectiveDomainTransformed, myFromHSV, myToHSV)
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        return createContinuousMapper(
            domain,
            listOf(myFromHSV to myToHSV)
        )
    }

    companion object {
        private const val DEF_START = 0.2
        private const val DEF_END = 0.8
    }
}
