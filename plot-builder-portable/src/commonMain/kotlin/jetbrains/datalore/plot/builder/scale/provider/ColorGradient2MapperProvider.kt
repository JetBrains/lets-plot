/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.ContinuousOnlyMapperProvider
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

class ColorGradient2MapperProvider(
    low: Color?, mid: Color?, high: Color?, midpoint: Double?, naValue: Color
) : ContinuousOnlyMapperProvider<Color>(naValue) {

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
        domain: DoubleSpan,
        trans: ContinuousTransform
    ): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)

        val lowDomain = DoubleSpan(domain.lowerEnd, max(myMidpoint!!, domain.lowerEnd))
        val highDomain = DoubleSpan(min(myMidpoint, domain.upperEnd), domain.upperEnd)

        val lowMapper = ColorMapper.gradient(lowDomain, myLow, myMid, naValue)
        val highMapper = ColorMapper.gradient(highDomain, myMid, myHigh, naValue)

        val rangeMap = mapOf(
            lowDomain to lowMapper,
            highDomain to highMapper
        )

        fun getMapper(v: Double?): ((Double?) -> Color)? {
            var f: ((Double?) -> Color)? = null
            if (SeriesUtil.isFinite(v)) {
                var fSpan = Double.NaN
                for (range in rangeMap.keys) {
                    if (range.contains(v!!)) {
                        val span = range.upperEnd - range.lowerEnd
                        // try to avoid 0-length ranges
                        // but prefer shorter ranges
                        if (f == null || fSpan == 0.0) {
                            f = rangeMap.get(range)
                            fSpan = span
                        } else if (span < fSpan && span > 0) {
                            f = rangeMap.get(range)
                            fSpan = span
                        }
                    }
                }
            }
            return f
        }

        val scaleMapper = object : ScaleMapper<Color> {
            override fun invoke(v: Double?): Color {
                val mapper = getMapper(v)
                return mapper?.invoke(v) ?: naValue
            }
        }
        return GuideMappers.asContinuous(scaleMapper)
    }

    companion object {
        // https://ggplot2.tidyverse.org/current/scale_gradient.html
        private val DEF_GRADIENT_LOW = Color.parseHex("#964540") // muted("red")
        private val DEF_GRADIENT_MID = Color.WHITE
        private val DEF_GRADIENT_HIGH = Color.parseHex("#3B3D96") // muted("blue")
    }
}
