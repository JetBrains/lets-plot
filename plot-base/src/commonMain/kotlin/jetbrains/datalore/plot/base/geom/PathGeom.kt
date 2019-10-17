package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.LinePathConstructor
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot

open class PathGeom : GeomBase() {

    var animation: Any? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_KEY_ELEMENT_FACTORY

    protected open fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
    }

    override fun buildIntern(root: SvgRoot,
                             aesthetics: Aesthetics,
                             pos: PositionAdjustment,
                             coord: CoordinateSystem,
                             ctx: GeomContext
    ) {

        val dataPoints = dataPoints(aesthetics)
        val targetCollector = getGeomTargetCollector(ctx)
        val linesHelper = LinesHelper(pos, coord, ctx)

        val geomConstructor = LinePathConstructor(targetCollector, dataPoints, linesHelper, false)
        appendNodes(geomConstructor.construct(), root)
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.Y,
//                Aes.SIZE, // path width
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.ALPHA,
//                Aes.MAP_ID,
//                Aes.SPEED,
//                Aes.FLOW
//        )

        const val HANDLES_GROUPS = true
        val LEGEND_KEY_ELEMENT_FACTORY: LegendKeyElementFactory =
            PathLegendKeyElementFactory()
    }

}
