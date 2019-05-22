package jetbrains.datalore.visualization.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.scale.Scale2


object ScaleBreaksUtil {
    fun <TargetT> withBreaks(scale: Scale2<TargetT>, scaleDomain: ClosedRange<Double>, breakCount: Int): Scale2<TargetT> {
        if (scale.hasBreaksGenerator()) {
            val breaksHelper = scale.breaksGenerator.generateBreaks(scaleDomain, breakCount)
            val breaks = breaksHelper.domainValues
            val labels = breaksHelper.labels
            return scale.with()
                    .breaks(breaks)
                    .labels(labels)
                    .build()
        }
        return withLinearBreaks(scale, scaleDomain, breakCount)
    }

    private fun <TargetT> withLinearBreaks(scale: Scale2<TargetT>, scaleDomain: ClosedRange<Double>, breakCount: Int): Scale2<TargetT> {
        val breaksHelper = LinearBreaksHelper(scaleDomain.lowerEndpoint(), scaleDomain.upperEndpoint(), breakCount)
        val breaks = breaksHelper.breaks
        val labels = ArrayList<String>()
        for (br in breaks) {
            labels.add(breaksHelper.labelFormatter(br))
        }

        return scale.with()
                .breaks(breaks)
                .labels(labels)
                .build()
    }
}
