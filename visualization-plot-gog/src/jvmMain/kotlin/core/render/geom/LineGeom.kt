package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomUtil

internal open class LineGeom : PathGeom() {

    protected override fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(super.dataPoints(aesthetics))
    }

    companion object {
        val RENDERS = PathGeom.RENDERS

        val HANDLES_GROUPS = PathGeom.HANDLES_GROUPS
    }
}
