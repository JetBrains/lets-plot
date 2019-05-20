package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers

internal open class IdentityDiscreteMapperProvider<T>(
        private val inputConverter: (Any?) -> T?, naValue: T) :
        MapperProviderBase<T>(naValue) {

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<T> {
        val inputValues = ArrayList(DataFrameUtil.distinctValues(data, variable))
        val outputValues = ArrayList<T>()
        for (inputValue in inputValues) {
            if (inputValue == null) {
                outputValues.add(naValue)
            } else {
                val outputValue = inputConverter(inputValue)
                        ?: throw IllegalStateException("Can't map input value $inputValue to output type")
                outputValues.add(outputValue)
            }
        }
        // ToDo: get rid of xxx2
        return GuideMappers.discreteToDiscrete2(inputValues, outputValues, naValue)
    }
}
