package jetbrains.datalore.visualization.plot.gog.core.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface BreaksGenerator {
    fun generate(domainAfterTransform: ClosedRange<Double>, targetCount: Int): ScaleBreaks
}
