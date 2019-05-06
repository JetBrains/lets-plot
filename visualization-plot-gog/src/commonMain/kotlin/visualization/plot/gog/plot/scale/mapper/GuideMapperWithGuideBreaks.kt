package jetbrains.datalore.visualization.plot.gog.plot.scale.mapper

import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideBreak
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.WithGuideBreaks

internal class GuideMapperWithGuideBreaks<TargetT>(
        private val myF: (Double) -> TargetT,
        breaks: List<GuideBreak<*>>) : GuideMapper<TargetT>, WithGuideBreaks {

    private val myBreaks: List<GuideBreak<*>> = ArrayList(breaks)

    override val guideBreaks: List<GuideBreak<*>>
        get() = myBreaks

    override val isContinuous = false

    override fun apply(value: Double): TargetT {
        return myF(value)
    }
}
