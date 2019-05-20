package jetbrains.datalore.visualization.plot.gog.plot.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.scale.BreaksGenerator
import jetbrains.datalore.visualization.plot.base.scale.Scale2
import jetbrains.datalore.visualization.plot.base.scale.ScaleUtil
import jetbrains.datalore.visualization.plot.base.scale.transform.LinearBreaksGen

object AxisBreaksUtil {
    fun createAxisBreaksProvider(scale: Scale2<Double>, axisDomain: ClosedRange<Double>): AxisBreaksProvider {
        val breaksProvider: AxisBreaksProvider
        if (scale.hasBreaks()) {
            breaksProvider = FixedAxisBreaksProvider(scale.breaks, ScaleUtil.breaksTransformed(scale), ScaleUtil.labels(scale))
        } else {
            val breaksGen: BreaksGenerator
            if (scale.hasBreaksGenerator()) {
                breaksGen = scale.breaksGenerator
            } else {
                breaksGen = LinearBreaksGen()
            }
            breaksProvider = AdaptableAxisBreaksProvider(axisDomain, breaksGen)
        }

        return breaksProvider
    }
}
