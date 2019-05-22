package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.visualization.plot.base.render.Aesthetics
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.geom.util.GeomUtil

open class LineGeom : PathGeom() {

    override fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(super.dataPoints(aesthetics))
    }

    companion object {
        val RENDERS = PathGeom.RENDERS

        val HANDLES_GROUPS = PathGeom.HANDLES_GROUPS
    }
}
