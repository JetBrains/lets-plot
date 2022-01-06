/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.scale.GuideMapper

object GuideMappers {
    val IDENTITY: GuideMapper<Double> =
        GuideMapperAdapter(Mappers.IDENTITY, isContinuous = false) // ToDo: why isContinuous = false?
    val UNDEFINED: GuideMapper<Double> =
        GuideMapperAdapter(Mappers.undefined(), false)

    fun <TargetT> discreteToDiscrete(
        discreteTransform: DiscreteTransform,
        outputValues: List<TargetT>,
        naValue: TargetT
    ): GuideMapper<TargetT> {

        return GuideMapperWithGuideBreaks(
            mapper = Mappers.discrete(discreteTransform, outputValues, naValue),
            breaks = discreteTransform.effectiveDomain
        ) { v: Any -> v.toString() }
    }

    fun <TargetT> continuousToDiscrete(
        domain: ClosedRange<Double>?,
        outputValues: List<TargetT>,
        naValue: TargetT
    ): GuideMapper<TargetT> {
        // quantized
        val mapper = Mappers.quantized(domain, outputValues, naValue)
        return asNotContinuous(mapper)
    }

    fun discreteToContinuous(
        discreteTransform: DiscreteTransform,
        outputRange: ClosedRange<Double>,
        naValue: Double
    ): GuideMapper<Double> {

        val mapper = Mappers.discreteToContinuous(discreteTransform.effectiveDomainTransformed, outputRange, naValue)
        return GuideMapperWithGuideBreaks(
            mapper,
            discreteTransform.effectiveDomain
        ) { v: Any -> v.toString() }
    }

    fun continuousToContinuous(
        domain: ClosedRange<Double>,
        range: ClosedRange<Double>,
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

    fun <T> asNotContinuous(mapper: (Double?) -> T): GuideMapper<T> {
        return GuideMapperAdapter(mapper, false)
    }

    fun <T> asContinuous(mapper: (Double?) -> T?): GuideMapper<T> {
        return GuideMapperAdapter(mapper, true)
    }
}
