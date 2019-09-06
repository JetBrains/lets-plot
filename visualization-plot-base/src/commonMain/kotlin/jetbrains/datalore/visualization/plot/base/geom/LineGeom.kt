package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.geom.util.GeomUtil

open class LineGeom : PathGeom() {

    override fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(super.dataPoints(aesthetics))
    }

    companion object {
//        val RENDERS = PathGeom.RENDERS

        const val HANDLES_GROUPS = PathGeom.HANDLES_GROUPS
    }
}
