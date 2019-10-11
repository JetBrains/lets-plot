package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.base.scale.transform.LinearBreaksGen

object AxisBreaksUtil {
    fun createAxisBreaksProvider(scale: Scale<Double>, axisDomain: ClosedRange<Double>): AxisBreaksProvider {
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
