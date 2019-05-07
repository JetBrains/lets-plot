package jetbrains.datalore.visualization.plotDemo.model

import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector
import jetbrains.datalore.visualization.plot.gog.core.event3.NullGeomTargetCollector
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext

class EmptyGeomContext : GeomContext {
    override val targetCollector: GeomTargetCollector = NullGeomTargetCollector()

    override fun getResolution(aes: Aes<Double>): Double {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun getUnitResolution(aes: Aes<Double>): Double {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
        throw IllegalStateException("Not available in an empty geom context")
    }
}