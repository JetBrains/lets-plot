package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

internal class JitterGeom : PointGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = PointGeom.RENDERS

        val HANDLES_GROUPS = PointGeom.HANDLES_GROUPS
    }
}
