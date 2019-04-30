package jetbrains.datalore.visualization.plot.gog.plot.scale.mapper

import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideBreak
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.WithGuideBreaks
import observable.collections.Collections.unmodifiableList

internal class GuideMapperWithGuideBreaks<TargetT>(private val myF: (Double) -> TargetT, breaks: List<GuideBreak<*>>) : GuideMapper<TargetT>, WithGuideBreaks {
    private val myBreaks: List<GuideBreak<*>> = unmodifiableList(ArrayList(breaks))

    override val guideBreaks: List<GuideBreak<*>>
        get() = myBreaks

    override val isContinuous: Boolean
        get() = false

    override fun apply(value: Double): TargetT {
        return myF(value)
    }
}
