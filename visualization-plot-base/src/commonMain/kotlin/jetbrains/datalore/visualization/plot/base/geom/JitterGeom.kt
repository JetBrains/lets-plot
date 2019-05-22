package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.visualization.plot.base.Aes

class JitterGeom : PointGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = PointGeom.RENDERS

        const val HANDLES_GROUPS = PointGeom.HANDLES_GROUPS
    }
}
