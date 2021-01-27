/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.breaks.QuantitativeTickFormatterFactory
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.common.data.SeriesUtil

object GuideMappers {
    val IDENTITY: GuideMapper<Double> =
        GuideMapperAdapter(Mappers.IDENTITY)
    val UNDEFINED: GuideMapper<Double> =
        GuideMapperAdapter(Mappers.undefined())

    fun <TargetT> discreteToDiscrete(
        data: DataFrame,
        variable: DataFrame.Variable,
        outputValues: List<TargetT>,
        naValue: TargetT
    ): GuideMapper<TargetT> {

        val domainValues = data.distinctValues(variable)
        return discreteToDiscrete(
            domainValues,
            outputValues,
            naValue
        )
    }

    fun <TargetT> discreteToDiscrete(
        domainValues: Collection<*>,
        outputValues: List<TargetT>,
        naValue: TargetT
    ): GuideMapper<TargetT> {

        val mapper = Mappers.discrete(outputValues, naValue)
        return GuideMapperWithGuideBreaks(
            mapper,
            domainValues.mapNotNull { it },
            { v: Any -> v.toString() }
        )
    }

    fun <TargetT> continuousToDiscrete(
        domain: ClosedRange<Double>?,
        outputValues: List<TargetT>,
        naValue: TargetT
    ): GuideMapper<TargetT> {
        // quantized
        val f = Mappers.quantized(domain, outputValues, naValue)
        var formatter: (Double) -> String = { v: Double -> v.toString() }

        val breakCount = outputValues.size
        val breaks = ArrayList<Double>()
        if (domain != null && breakCount != 0) {
            val span = SeriesUtil.span(domain)
            val step = span / breakCount
            formatter = QuantitativeTickFormatterFactory.forLinearScale().getFormatter(domain, step)

            for (i in 0 until breakCount) {
                val value = domain.lowerEnd + step / 2 + i * step
                breaks.add(value)
            }
        }

        return GuideMapperWithGuideBreaks(f, breaks, formatter)
    }

    fun discreteToContinuous(
        domainValues: Collection<*>,
        outputRange: ClosedRange<Double>,
        naValue: Double
    ): GuideMapper<Double> {

        val mapper = Mappers.discreteToContinuous(domainValues, outputRange, naValue)
        return GuideMapperWithGuideBreaks(
            mapper,
            domainValues.mapNotNull { it },
            { v: Any -> v.toString() }
        )
    }

    fun continuousToContinuous(
        domain: ClosedRange<Double>,
        range: ClosedRange<Double>,
        naValue: Double?
    ): GuideMapper<Double> {
        return adaptContinuous(
            Mappers.linear(
                domain,
                range,
                naValue!!
            )
        )
    }

    fun <T> adapt(mapperFun: (Double?) -> T): GuideMapper<T> {
        return GuideMapperAdapter(mapperFun)
    }

    fun <T> adaptContinuous(mapper: (Double?) -> T?): GuideMapper<T> {
        return GuideMapperAdapter(mapper, true)
    }
}
