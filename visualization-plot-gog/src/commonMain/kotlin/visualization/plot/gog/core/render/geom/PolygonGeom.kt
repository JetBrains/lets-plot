package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.*
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomUtil
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.LinePathConstructor
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.LinesHelper

internal open class PolygonGeom : GeomBase() {

    protected fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
    }

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem,
                             ctx: GeomContext) {
        val dataPoints = dataPoints(aesthetics)
        val targetCollector = getGeomTargetCollector(ctx)
        val linesHelper = LinesHelper(pos, coord, ctx)

        val geomConstructor = LinePathConstructor(targetCollector, dataPoints, linesHelper, true)
        appendNodes(geomConstructor.construct(), root)
    }

    companion object {
        val RENDERS = listOf(
                Aes.X,
                Aes.Y,

                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.MAP_ID
        )

        val HANDLES_GROUPS = true
    }
}
