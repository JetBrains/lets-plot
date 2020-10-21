/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

class ColorGradient2MapperProvider(low: Color?, mid: Color?, high: Color?, midpoint: Double?, naValue: Color) :
    MapperProviderBase<Color>(naValue) {

    private val myLow: Color
    private val myMid: Color
    private val myHigh: Color
    private val myMidpoint: Double?

    init {
        myLow = low ?: DEF_GRADIENT_LOW
        myMid = mid ?: DEF_GRADIENT_MID
        myHigh = high ?: DEF_GRADIENT_HIGH
        myMidpoint = midpoint ?: 0.0
    }

    override fun createContinuousMapper(
        domain: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: Transform?
    ): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, lowerLimit, upperLimit, trans)

        val lowDomain = ClosedRange(domain.lowerEnd, max(myMidpoint!!, domain.lowerEnd))
        val highDomain = ClosedRange(min(myMidpoint, domain.upperEnd), domain.upperEnd)

        val lowMapper = ColorMapper.gradient(lowDomain, myLow, myMid, naValue)
        val highMapper = ColorMapper.gradient(highDomain, myMid, myHigh, naValue)

        val rangeMap = mapOf(
            lowDomain to lowMapper,
            highDomain to highMapper
        )

        fun getMapper(v: Double?): ((Double?) -> Color)? {
            var f_: ((Double?) -> Color)? = null
            if (SeriesUtil.isFinite(v)) {
                var f_span = Double.NaN
                for (range in rangeMap.keys) {
                    if (range.contains(v!!)) {
                        val span = range.upperEnd - range.lowerEnd
                        // try to avoid 0-length ranges
                        // but prefer shorter ranges
                        if (f_ == null || f_span == 0.0) {
                            f_ = rangeMap.get(range)
                            f_span = span
                        } else if (span < f_span && span > 0) {
                            f_ = rangeMap.get(range)
                            f_span = span
                        }
                    }
                }
            }
            return f_
        }

        val mapperFun: (Double?) -> Color = { input: Double? ->
            val mapper = getMapper(input)
            mapper?.invoke(input) ?: naValue
        }

        return GuideMappers.adaptContinuous(mapperFun)
    }

    companion object {
        // https://ggplot2.tidyverse.org/current/scale_gradient.html
        private val DEF_GRADIENT_LOW = Color.parseHex("#964540") // muted("red")
        private val DEF_GRADIENT_MID = Color.WHITE
        private val DEF_GRADIENT_HIGH = Color.parseHex("#3B3D96") // muted("blue")
    }
}
