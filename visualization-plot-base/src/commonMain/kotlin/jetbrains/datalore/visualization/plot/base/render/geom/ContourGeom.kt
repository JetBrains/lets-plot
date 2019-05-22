package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.visualization.plot.base.Aes

open class ContourGeom : PathGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = PathGeom.RENDERS

        const val HANDLES_GROUPS = PathGeom.HANDLES_GROUPS
    }
}
