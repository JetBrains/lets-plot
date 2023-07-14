/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.mapper

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper

object GuideMappers {
    val IDENTITY: GuideMapper<Double> = GuideMapper(Mappers.IDENTITY, false)
    val NUMERIC_UNDEFINED: GuideMapper<Double> = GuideMapper(Mappers.NUMERIC_UNDEFINED, false)


    fun <TargetT> discreteToDiscrete(
        discreteTransform: DiscreteTransform,
        outputValues: List<TargetT>,
        naValue: TargetT?
    ): ScaleMapper<TargetT> {

        return GuideMapperWithGuideBreaks(
            mapper = Mappers.discrete(discreteTransform, outputValues, naValue),
            breaks = discreteTransform.effectiveDomain,
            formatter = { v: Any -> v.toString() }
        )
    }

    fun <TargetT> continuousToDiscrete(
        domain: DoubleSpan?,
        outputValues: List<TargetT>,
        naValue: TargetT
    ): GuideMapper<TargetT> {
        // quantized
        val mapper = Mappers.quantized(domain, outputValues, naValue)
        return asNotContinuous(mapper)
    }

    fun discreteToContinuous(
        discreteTransform: DiscreteTransform,
        outputRange: DoubleSpan,
        naValue: Double
    ): ScaleMapper<Double> {

        val mapper = Mappers.discreteToContinuous(discreteTransform.effectiveDomainTransformed, outputRange, naValue)
        return GuideMapperWithGuideBreaks(
            mapper,
            discreteTransform.effectiveDomain,
            formatter = { v: Any -> v.toString() }
        )
    }

    fun continuousToContinuous(
        domain: DoubleSpan,
        range: DoubleSpan,
        naValue: Double
    ): GuideMapper<Double> {
        return asContinuous(
            Mappers.linear(
                domain,
                range,
                naValue
            )
        )
    }

    fun <T> asNotContinuous(mapper: ScaleMapper<T>): GuideMapper<T> {
        return GuideMapper(mapper, false)
    }

    fun <T> asContinuous(mapper: ScaleMapper<T>): GuideMapper<T> {
        return GuideMapper(mapper, true)
    }
}
