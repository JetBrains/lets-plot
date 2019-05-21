package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.render.Aes

interface AesAutoMapper {
    fun createMapping(data: DataFrame): Map<Aes<*>, DataFrame.Variable>
}
