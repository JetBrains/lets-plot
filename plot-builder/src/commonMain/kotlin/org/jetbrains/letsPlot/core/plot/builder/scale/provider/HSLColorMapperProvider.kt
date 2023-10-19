/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.HSL
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.ensureApplicableRange
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.ColorMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.GuideMappers

abstract class HSLColorMapperProvider(naValue: Color) : MapperProviderBase<Color>(naValue) {

    protected fun createDiscreteMapper(transformedDomain: List<Double>, from: HSL, to: HSL): ScaleMapper<Color> {
        val mapperDomain = ensureApplicableRange(SeriesUtil.range(transformedDomain))

        val gradientMapper = ColorMapper.gradientHSL(mapperDomain, from, to, naValue, alpha = 1.0)
        return GuideMappers.asNotContinuous(ScaleMapper.wrap(gradientMapper))
    }

    protected fun createContinuousMapper(domain: DoubleSpan, from: HSL, to: HSL): GuideMapper<Color> {
        val gradientMapper = ColorMapper.gradientHSL(domain, from, to, naValue, alpha = 1.0)
        return GuideMappers.asContinuous(ScaleMapper.wrap(gradientMapper))
    }
}
