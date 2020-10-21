/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.breaks.QuantitativeTickFormatterFactory
import jetbrains.datalore.plot.builder.scale.GuideBreak
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

        val domainValues = DataFrameUtil.distinctValues(data, variable)
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

        // ToDo: duplicates discreteToDiscrete2 (?)
        val breaks = ArrayList<GuideBreak<*>>()
        for (domainValue in domainValues) {
            breaks.add(GuideBreak(domainValue, domainValue?.toString() ?: "n/a"))
        }

        return GuideMapperWithGuideBreaks(mapper, breaks)
    }

    fun <TargetT> continuousToDiscrete(
        domain: ClosedRange<Double>?,
        outputValues: List<TargetT>,
        naValue: TargetT
    ): GuideMapper<TargetT> {
        // quantized
        val f = Mappers.quantized(domain, outputValues, naValue)

        val breakCount = outputValues.size
        val breakValues = ArrayList<Double>()
        val breakLabels = ArrayList<String>()
        if (domain != null && breakCount != 0) {
            val span = SeriesUtil.span(domain)
            val step = span / breakCount
            val formatter = QuantitativeTickFormatterFactory.forLinearScale().getFormatter(domain, step)

            for (i in 0 until breakCount) {
                val `val` = domain.lowerEnd + step / 2 + i * step
                breakValues.add(`val`)
                breakLabels.add(formatter(`val`))
            }
        }

        val breaks = ArrayList<GuideBreak<*>>()
        val breakLabel = breakLabels.iterator()
        for (breakValue in breakValues) {
            breaks.add(GuideBreak(breakValue, breakLabel.next()))
        }

        return GuideMapperWithGuideBreaks(f, breaks)
    }

    fun discreteToContinuous(
        domainValues: Collection<*>,
        outputRange: ClosedRange<Double>,
        naValue: Double
    ): GuideMapper<Double> {

        val mapper = Mappers.discreteToContinuous(domainValues, outputRange, naValue)
        val breaks = ArrayList<GuideBreak<*>>()
        for (domainValue in domainValues) {
            // ToDo: label formatter?
            breaks.add(GuideBreak(domainValue!!, domainValue.toString()))
        }

        return GuideMapperWithGuideBreaks(mapper, breaks)
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
