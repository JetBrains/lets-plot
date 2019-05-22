package jetbrains.datalore.visualization.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface BreaksGenerator {
    fun generateBreaks(domainAfterTransform: ClosedRange<Double>, targetCount: Int): ScaleBreaks
}
