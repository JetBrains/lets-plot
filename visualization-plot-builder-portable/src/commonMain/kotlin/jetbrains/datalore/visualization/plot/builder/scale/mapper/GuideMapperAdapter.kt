package jetbrains.datalore.visualization.plot.builder.scale.mapper

import jetbrains.datalore.visualization.plot.builder.scale.GuideMapper
import kotlin.jvm.JvmOverloads

class GuideMapperAdapter<T> @JvmOverloads constructor(
    private val myF: (Double?) -> T?,
    override val isContinuous: Boolean = false
) :
    GuideMapper<T> {

    override fun apply(value: Double?): T? {
        return myF(value)
    }
}
