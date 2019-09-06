package jetbrains.datalore.visualization.plot.base.geom


import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.geom.util.GeomHelper
import jetbrains.datalore.visualization.plot.base.geom.util.GeomUtil
import jetbrains.datalore.visualization.plot.base.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.visualization.plot.base.geom.util.LinesHelper
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.base.render.SvgRoot

class RibbonGeom : GeomBase() {

    private fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        val data = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.YMIN, Aes.YMAX)
        return GeomUtil.ordered_X(data)
    }

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val dataPoints = dataPoints(aesthetics)
        val helper = LinesHelper(pos, coord, ctx)
        val paths = helper.createBands(dataPoints, GeomUtil.TO_LOCATION_X_YMAX, GeomUtil.TO_LOCATION_X_YMIN)
        appendNodes(paths, root)

        //if you want to retain the side edges of ribbon: comment out the following codes, and switch decorate method in LinesHelper.createbands
        helper.setAlphaEnabled(false)
        val lines = helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_YMAX)
        lines.addAll(helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_YMIN))
        appendNodes(lines, root)

        buildHints(aesthetics, pos, coord, ctx)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val targetCollector = ctx.targetCollector
        val helper = GeomHelper(pos, coord, ctx)

        for (p in aesthetics.dataPoints()) {
            addTarget(p, targetCollector, GeomUtil.TO_LOCATION_X_YMAX, helper)
            addTarget(p, targetCollector, GeomUtil.TO_LOCATION_X_YMIN, helper)
        }
    }

    private fun addTarget(p: DataPointAesthetics, collector: GeomTargetCollector, toLocation: (DataPointAesthetics) -> DoubleVector?,
                          helper: GeomHelper) {
        val coord = toLocation(p)
        if (coord != null) {
            collector.addPoint(p.index(), helper.toClient(coord, p), 0.0, params().setColor(fromColor(p)))
        }
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.YMIN,
//                Aes.YMAX,
//                Aes.SIZE,
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA
//        )

        const val HANDLES_GROUPS = true
    }
}
