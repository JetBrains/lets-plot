/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.color.GradientUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.scale.ContinuousOnlyMapperProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.PaletteGenerator
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.ColorMapperDefaults.Gradient2
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.GuideMappers
import kotlin.math.max
import kotlin.math.min

class ColorGradient2MapperProvider(
    low: Color?,
    mid: Color?,
    high: Color?,
    midpoint: Double?,
    naValue: Color
) : ContinuousOnlyMapperProvider<Color>(naValue),
    PaletteGenerator {

    private val myLow: Color
    private val myMid: Color
    private val myHigh: Color
    private val myMidpoint: Double?

    init {
        myLow = low ?: Gradient2.DEF_LOW
        myMid = mid ?: Gradient2.DEF_MID
        myHigh = high ?: Gradient2.DEF_HIGH
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

        val lowMapper = GradientUtil.gradient(lowDomain, myLow, myMid, naValue)
        val highMapper = GradientUtil.gradient(highDomain, myMid, myHigh, naValue)

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

    override fun createPaletteGeneratorScaleMapper(colorCount: Int): ScaleMapper<Color> {
        val domain = DoubleSpan(0.0, (colorCount - 1).toDouble())
        return createContinuousMapper(domain, Transforms.IDENTITY)
    }
}
