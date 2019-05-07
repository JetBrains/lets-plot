package jetbrains.datalore.visualization.plot.gog.core.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.core.scale.BreaksGenerator
import jetbrains.datalore.visualization.plot.gog.core.scale.ScaleBreaks
import jetbrains.datalore.visualization.plot.gog.core.scale.breaks.LinearBreaksHelper

class LinearBreaksGen : BreaksGenerator {
    override fun generateBreaks(domainAfterTransform: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val helper = LinearBreaksHelper(domainAfterTransform.lowerEndpoint(), domainAfterTransform.upperEndpoint(), targetCount)
        val ticks = helper.breaks
        val labelFormatter = helper.labelFormatter
        val labels = ArrayList<String>()
        for (tick in ticks) {
            labels.add(labelFormatter(tick))
        }
        return ScaleBreaks(ticks, ticks, labels)
    }
}
