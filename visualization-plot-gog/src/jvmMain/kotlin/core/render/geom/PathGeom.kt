package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.CoordinateSystem
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext
import jetbrains.datalore.visualization.plot.gog.core.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment
import jetbrains.datalore.visualization.plot.gog.core.render.SvgRoot
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomUtil
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.LinePathConstructor
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.LinesHelper

import java.util.Arrays

internal open class PathGeom : GeomBase() {

    var animation: Any? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_KEY_ELEMENT_FACTORY

    protected open fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
    }

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem,
                             ctx: GeomContext) {
        val dataPoints = dataPoints(aesthetics)
        val targetCollector = getGeomTargetCollector(ctx)
        val linesHelper = LinesHelper(pos, coord, ctx)

        val geomConstructor = LinePathConstructor(targetCollector, dataPoints, linesHelper, false)
        appendNodes(geomConstructor.construct(), root)
    }

    companion object {
        val RENDERS = Arrays.asList<Aes<*>>(
                Aes.X,
                Aes.Y,
                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA,
                Aes.MAP_ID,
                Aes.SPEED,
                Aes.FLOW
        )

        val HANDLES_GROUPS = true
        val LEGEND_KEY_ELEMENT_FACTORY: LegendKeyElementFactory = PathLegendKeyElementFactory()
    }

}
