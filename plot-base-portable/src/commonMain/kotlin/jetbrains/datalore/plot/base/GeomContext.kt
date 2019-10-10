package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.interact.GeomTargetCollector

interface GeomContext {
    val targetCollector: GeomTargetCollector

    fun getResolution(aes: Aes<Double>): Double

    fun getUnitResolution(aes: Aes<Double>): Double

    fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext
}
