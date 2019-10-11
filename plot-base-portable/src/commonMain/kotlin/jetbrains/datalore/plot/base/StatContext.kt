package jetbrains.datalore.plot.base

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface StatContext {
    fun overallXRange(): ClosedRange<Double>?

    fun overallYRange(): ClosedRange<Double>?
}
