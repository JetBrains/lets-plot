package jetbrains.datalore.visualization.plot.base.render

import jetbrains.datalore.visualization.plot.base.event3.GeomTargetCollector

interface GeomContext {
    val targetCollector: GeomTargetCollector

    fun getResolution(aes: Aes<Double>): Double

    fun getUnitResolution(aes: Aes<Double>): Double

    fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext
}
