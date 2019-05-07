package jetbrains.datalore.visualization.plot.gog.plot.scale.mapper

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.MapperUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.Mappers
import jetbrains.datalore.visualization.plot.gog.core.scale.breaks.QuantitativeTickFormatterFactory
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideBreak
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper

object GuideMappers {
    val IDENTITY: GuideMapper<Double> = GuideMapperAdapter(Mappers.IDENTITY)


    fun <TargetT> discreteToDiscrete(
            data: DataFrame, `var`: DataFrame.Variable, outputValues: List<TargetT>, naValue: TargetT): GuideMapper<TargetT> {
        val domainValues = DataFrameUtil.distinctValues(data, `var`)
        return discreteToDiscrete(domainValues, outputValues, naValue)
    }

    fun <TargetT> discreteToDiscrete(domainValues: Collection<*>, outputValues: List<TargetT>, naValue: TargetT): GuideMapper<TargetT> {
        val f = Mappers.discrete(outputValues, naValue)

        val breaks = ArrayList<GuideBreak<*>>()
        for (domainValue in domainValues) {
            // ToDo: label formatter?
            breaks.add(GuideBreak<Any>(domainValue!!, domainValue.toString()))
        }

        return GuideMapperWithGuideBreaks(f, breaks)
    }

    fun <TargetT> discreteToDiscrete2(domainValues: List<*>, outputValues: List<TargetT>, naValue: TargetT): GuideMapper<TargetT> {
        //Function<Double, TargetT> f = Mappers.discrete(outputValues, naValue);
        // ToDo: this works better with identity scales for 'numeric' input (when indices-based discrete mapper doesn't work)
        // but doesn't map values 'between indices' as does mapper in the method above.
        // Used to create identity mapper for aesthetics: 'shape' and 'linetype'
        //
        // UPDATE: All discrete mappers are index-based again due to: DP-4956 Discrete scale looks strange.
        // See MapperUtil#mapDiscreteDomainValuesToNumbers
        val domainValuesAsNumbers = MapperUtil.mapDiscreteDomainValuesToNumbers(domainValues)
        val mapperMap = HashMap<Double?, TargetT>()
        for (i in domainValues.indices) {
            val domainValue = domainValues[i]
            mapperMap[domainValuesAsNumbers[domainValue]] = outputValues[i]
        }
        val f = { num: Double? ->
            if (num == null) {
                naValue
            } else if (mapperMap.containsKey(num)) {
                mapperMap[num]!!
            } else {
                throw IllegalArgumentException("Failed to map discrete value $num")
            }
        }


        val breaks = ArrayList<GuideBreak<*>>()
        for (domainValue in domainValues) {
            // ToDo: label formatter?
            breaks.add(GuideBreak(domainValue!!, domainValue.toString()))
        }

        return GuideMapperWithGuideBreaks(f, breaks)
    }

    fun <TargetT> continuousToDiscrete(domain: ClosedRange<Double>?, outputValues: List<TargetT>, naValue: TargetT): GuideMapper<TargetT> {
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
                val `val` = domain.lowerEndpoint() + step / 2 + i * step
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

    fun discreteToContinuous(domainValues: Collection<*>, outputRange: ClosedRange<Double>, naValue: Double?): GuideMapper<Double> {
        val f = Mappers.discreteToContinuous(domainValues, outputRange, naValue)

        val breaks = ArrayList<GuideBreak<*>>()
        for (domainValue in domainValues) {
            // ToDo: label formatter?
            breaks.add(GuideBreak<Any>(domainValue!!, domainValue.toString()))
        }

        return GuideMapperWithGuideBreaks(f, breaks)
    }

    fun continuousToContinuous(domain: ClosedRange<Double>, range: ClosedRange<Double>, naValue: Double?): GuideMapper<Double> {
        return adaptContinuous(Mappers.linear(domain, range, naValue!!))
    }

    fun <T> adapt(mapperFun: (Double?) -> T): GuideMapper<T> {
        return GuideMapperAdapter(mapperFun)
    }

    fun <T> adaptContinuous(mapperFun: (Double?) -> T): GuideMapper<T> {
        return GuideMapperAdapter(mapperFun, true)
    }
}
