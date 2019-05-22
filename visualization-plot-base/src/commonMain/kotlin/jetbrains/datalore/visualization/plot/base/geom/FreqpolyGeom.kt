package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.visualization.plot.base.Aes

class FreqpolyGeom : LineGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = LineGeom.RENDERS

        val HANDLES_GROUPS = LineGeom.HANDLES_GROUPS
    }
}
