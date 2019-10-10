package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface Projection {
    fun apply(v: Double): Double

    fun toValidDomain(domain: ClosedRange<Double>): ClosedRange<Double>
}
