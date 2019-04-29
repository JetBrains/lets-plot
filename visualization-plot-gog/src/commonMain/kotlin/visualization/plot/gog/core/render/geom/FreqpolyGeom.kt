package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

internal class FreqpolyGeom : LineGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = LineGeom.RENDERS

        val HANDLES_GROUPS = LineGeom.HANDLES_GROUPS
    }
}
