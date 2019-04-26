package jetbrains.datalore.visualization.plot.gog.core.data

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface StatContext {
    fun overallXRange(): ClosedRange<Double>

    fun overallYRange(): ClosedRange<Double>
}
