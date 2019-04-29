package jetbrains.datalore.visualization.plot.gog.plot.scale.mapper

import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper

internal class GuideMapperAdapter<T> @JvmOverloads constructor(private val myF: (Double) -> T, override val isContinuous: Boolean = false) : GuideMapper<T> {

    override fun apply(value: Double): T {
        return myF(value)
    }
}
