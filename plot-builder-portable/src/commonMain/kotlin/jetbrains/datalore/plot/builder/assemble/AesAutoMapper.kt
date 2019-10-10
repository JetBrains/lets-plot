package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame

interface AesAutoMapper {
    fun createMapping(data: DataFrame): Map<Aes<*>, DataFrame.Variable>
}
