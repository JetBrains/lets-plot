package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.visualization.plot.base.render.Aes

class Density2dfGeom : ContourfGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = ContourfGeom.RENDERS

        val HANDLES_GROUPS = ContourfGeom.HANDLES_GROUPS
    }
}
