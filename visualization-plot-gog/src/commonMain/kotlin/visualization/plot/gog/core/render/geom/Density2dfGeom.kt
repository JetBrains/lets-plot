package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

internal class Density2dfGeom : ContourfGeom() {
    companion object {
        val RENDERS: List<Aes<*>> = ContourfGeom.RENDERS

        val HANDLES_GROUPS = ContourfGeom.HANDLES_GROUPS
    }
}
