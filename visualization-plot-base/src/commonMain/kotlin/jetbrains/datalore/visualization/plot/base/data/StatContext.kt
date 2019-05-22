package jetbrains.datalore.visualization.plot.base.data

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface StatContext {
    fun overallXRange(): ClosedRange<Double>?

    fun overallYRange(): ClosedRange<Double>?
}
