package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers
import java.util.*

internal open class IdentityDiscreteMapperProvider<T>(private val myInputConverter: (Any) -> T, naValue: T) : MapperProviderBase<T>(naValue) {

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<T> {
        val inputValues = ArrayList(DataFrameUtil.distinctValues(data, variable))
        val outputValues = ArrayList<T>()
        for (inputValue in inputValues) {
            if (inputValue == null) {
                outputValues.add(naValue)
            } else {
                outputValues.add(myInputConverter(inputValue))
            }
        }
        // ToDo: get rid of xxx2
        return GuideMappers.discreteToDiscrete2(inputValues, outputValues, naValue)
    }
}
