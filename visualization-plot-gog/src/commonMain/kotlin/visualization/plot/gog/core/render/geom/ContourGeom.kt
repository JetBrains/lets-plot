package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

internal open class ContourGeom : PathGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = PathGeom.RENDERS

        val HANDLES_GROUPS = PathGeom.HANDLES_GROUPS
    }
}
