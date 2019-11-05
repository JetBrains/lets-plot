/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale


object ScaleBreaksUtil {
    fun <TargetT> withBreaks(scale: Scale<TargetT>, scaleDomain: ClosedRange<Double>, breakCount: Int): Scale<TargetT> {
        if (scale.hasBreaksGenerator()) {
            val breaksHelper = scale.breaksGenerator.generateBreaks(scaleDomain, breakCount)
            val breaks = breaksHelper.domainValues
            val labels = breaksHelper.labels
            return scale.with()
                    .breaks(breaks)
                    .labels(labels)
                    .build()
        }
        return withLinearBreaks(
            scale,
            scaleDomain,
            breakCount
        )
    }

    private fun <TargetT> withLinearBreaks(scale: Scale<TargetT>, scaleDomain: ClosedRange<Double>, breakCount: Int): Scale<TargetT> {
        val breaksHelper = LinearBreaksHelper(
            scaleDomain.lowerEndpoint(),
            scaleDomain.upperEndpoint(),
            breakCount
        )
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
