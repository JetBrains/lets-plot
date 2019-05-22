package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.data.DataFrame

interface AesAutoMapper {
    fun createMapping(data: DataFrame): Map<Aes<*>, DataFrame.Variable>
}
