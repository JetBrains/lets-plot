/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.HSV
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.ensureApplicableRange
import kotlin.math.abs

abstract class HSVColorMapperProvider(naValue: Color) : MapperProviderBase<Color>(naValue) {

    protected fun createDiscreteMapper(
        transformedDomain: List<Double>,
        fromHSV: HSV,
        toHSV: HSV
    ): ScaleMapper<Color> {
        val mapperDomain = ensureApplicableRange(SeriesUtil.range(transformedDomain))
        val n = transformedDomain.size

        var newFromHue = fromHSV.h
        var newToHue = toHSV.h
        if (n > 1) {
            // if 'from' and 'to' hue are too close - ajust the 'toHue'
            val hueDiff = abs(toHSV.h % 360 - fromHSV.h % 360)
            val step = (toHSV.h - fromHSV.h) / n
            if (hueDiff < abs(step) / 2) {
                newFromHue = fromHSV.h + step / 2
                newToHue = toHSV.h - step / 2
            }
        }

        val gradient = ColorMapper.gradientHSV(
            mapperDomain,
            HSV(newFromHue, fromHSV.s, fromHSV.v),
            HSV(newToHue, toHSV.s, toHSV.v),
            false, naValue, alpha = 1.0
        )
        return GuideMappers.asNotContinuous(ScaleMapper.wrap(gradient))
    }

    protected fun createContinuousMapper(
        domain: DoubleSpan,
        hsvIntervals: List<Pair<HSV, HSV>>
    ): GuideMapper<Color> {
        val gradientMapper = when (hsvIntervals.size) {
            0 -> throw IllegalArgumentException("Empty HSV intervals.")
            1 -> ColorMapper.gradientHSV(
                domain,
                hsvIntervals[0].first,
                hsvIntervals[0].second,
                false,
                naValue,
                alpha = 1.0
            )
            else -> createCompositeColorMapper(domain, hsvIntervals, false, naValue)
        }

        return GuideMappers.asContinuous(ScaleMapper.wrap(gradientMapper))
    }


    companion object {
        private fun createCompositeColorMapper(
            domain: DoubleSpan,
            hsvIntervals: List<Pair<HSV, HSV>>,
            autoHueDirection: Boolean,
            naColor: Color
        ): (Double?) -> Color {
            val colorMappersPerSubDomain =
                createColorMappersPerSubDomain(domain, hsvIntervals, autoHueDirection, naColor)

            return { v: Double? ->
                if (v == null) {
                    naColor
                } else {
                    val mapper = colorMappersPerSubDomain.find { it.first.contains(v) }?.second
                    mapper?.invoke(v) ?: naColor
                }
            }
        }

        private fun createColorMappersPerSubDomain(
            domain: DoubleSpan,
            hsvIntervals: List<Pair<HSV, HSV>>,
            autoHueDirection: Boolean,
            naColor: Color
        ): List<Pair<DoubleSpan, (Double?) -> Color>> {
            val subDomains = splitContinuousDomain(domain, hsvIntervals)

            return subDomains.zip(hsvIntervals).map {
                @Suppress("NAME_SHADOWING")
                val domain = it.first
                val hsvInterval = it.second
                Pair(
                    domain,
                    ColorMapper.gradientHSV(
                        domain,
                        hsvInterval.first,
                        hsvInterval.second,
                        autoHueDirection,
                        naColor,
                        alpha = 1.0
                    )
                )
            }
        }

        private fun splitContinuousDomain(
            domain: DoubleSpan,
            hsvIntervals: List<Pair<HSV, HSV>>
        ): List<DoubleSpan> {
            val domainSize = domain.upperEnd - domain.lowerEnd
            val hueIntervalSizeList = hsvIntervals.map {
                abs(it.first.h - it.second.h)
            }
            val hueIntervalsTotalSize = hueIntervalSizeList.sum()

            val domainToHueIntevalRatio = domainSize / hueIntervalsTotalSize

            val subDomains = ArrayList<DoubleSpan>()
            var lowerEnd = domain.lowerEnd
            for (hueIntervalSize in hueIntervalSizeList) {
                val upperEnd = lowerEnd + hueIntervalSize * domainToHueIntevalRatio
                subDomains.add(DoubleSpan(lowerEnd, upperEnd))
                lowerEnd = upperEnd
            }
            return subDomains
        }
    }
}
