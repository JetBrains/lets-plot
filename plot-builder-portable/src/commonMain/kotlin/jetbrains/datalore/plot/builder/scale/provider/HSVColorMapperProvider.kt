/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.HSV
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.data.SeriesUtil.ensureApplicableRange
import kotlin.math.abs

abstract class HSVColorMapperProvider(naValue: Color) : MapperProviderBase<Color>(naValue) {

    protected fun createDiscreteMapper(
        domainValues: Collection<*>,
        fromHSV: HSV,
        toHSV: HSV
    ): GuideMapper<Color> {
        val domainValuesAsNumbers = MapperUtil.mapDiscreteDomainValuesToNumbers(domainValues)
        val mapperDomain = ensureApplicableRange(SeriesUtil.range(domainValuesAsNumbers.values))

        var newFromHue = fromHSV.h
        var newToHue = toHSV.h
        if (domainValues.size > 1) {
            // if 'from' and 'to' hue are too close - ajust the 'toHue'
            val hueDiff = abs(toHSV.h % 360 - fromHSV.h % 360)
            val step = (toHSV.h - fromHSV.h) / domainValues.size
            if (hueDiff < abs(step) / 2) {
                newFromHue = fromHSV.h + step / 2
                newToHue = toHSV.h - step / 2
            }
        }

        val gradient = ColorMapper.gradientHSV(
            mapperDomain,
            HSV(newFromHue, fromHSV.s, fromHSV.v),
            HSV(newToHue, toHSV.s, toHSV.v),
            false, naValue
        )
        return GuideMappers.adapt(gradient)
    }

    protected fun createContinuousMapper(
        domain: ClosedRange<Double>,
        hsvIntervals: List<Pair<HSV, HSV>>
    ): GuideMapper<Color> {
        val gradientMapper = when (hsvIntervals.size) {
            0 -> throw IllegalArgumentException("Empty HSV intervals.")
            1 -> ColorMapper.gradientHSV(domain, hsvIntervals[0].first, hsvIntervals[0].second, false, naValue)
            else -> createCompositeColorMapper(domain, hsvIntervals, false, naValue)
        }

        return GuideMappers.adaptContinuous(gradientMapper)
    }


    companion object {
        private fun createCompositeColorMapper(
            domain: ClosedRange<Double>,
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
            domain: ClosedRange<Double>,
            hsvIntervals: List<Pair<HSV, HSV>>,
            autoHueDirection: Boolean,
            naColor: Color
        ): List<Pair<ClosedRange<Double>, (Double?) -> Color>> {
            val subDomains = splitContinuousDomain(domain, hsvIntervals)

            return subDomains.zip(hsvIntervals).map {
                @Suppress("NAME_SHADOWING")
                val domain = it.first
                val hsvInterval = it.second
                Pair(
                    domain,
                    ColorMapper.gradientHSV(domain, hsvInterval.first, hsvInterval.second, autoHueDirection, naColor)
                )
            }
        }

        private fun splitContinuousDomain(
            domain: ClosedRange<Double>,
            hsvIntervals: List<Pair<HSV, HSV>>
        ): List<ClosedRange<Double>> {
            val domainSize = domain.upperEnd - domain.lowerEnd
            val hueIntervalSizeList = hsvIntervals.map {
                abs(it.first.h - it.second.h)
            }
            val hueIntervalsTotalSize = hueIntervalSizeList.sum()

            val domainToHueIntevalRatio = domainSize / hueIntervalsTotalSize

            val subDomains = ArrayList<ClosedRange<Double>>()
            var lowerEnd = domain.lowerEnd
            for (hueIntervalSize in hueIntervalSizeList) {
                val upperEnd = lowerEnd + hueIntervalSize * domainToHueIntevalRatio
                subDomains.add(ClosedRange(lowerEnd, upperEnd))
                lowerEnd = upperEnd
            }
            return subDomains
        }
    }
}
