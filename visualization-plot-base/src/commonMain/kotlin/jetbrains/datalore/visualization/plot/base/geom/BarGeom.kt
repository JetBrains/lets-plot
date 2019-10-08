package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.geom.util.GeomUtil
import jetbrains.datalore.visualization.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.visualization.plot.base.geom.util.RectTargetCollectorHelper
import jetbrains.datalore.visualization.plot.base.geom.util.RectanglesHelper
import jetbrains.datalore.visualization.plot.base.render.SvgRoot

/**
 * TODO: position adjustment (identity, dodge, stack, fill)
 */
open class BarGeom : GeomBase() {

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val rectangles = helper.createRectangles(rectangleByDataPoint(ctx))
        rectangles.reverse()
        rectangles.forEach { root.add(it) }

        RectTargetCollectorHelper(helper, rectangleByDataPoint(ctx), { HintColorUtil.fromFill(it) })
                .collectTo(ctx.targetCollector)
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.Y,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA,
//                Aes.WIDTH,
//                Aes.SIZE
//        )

        const val HANDLES_GROUPS = false

        private fun rectangleByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val x = p.x()
                val y = p.y()
                val w = p.width()
                if (!SeriesUtil.allFinite(x, y, w))
                    null
                else
                    GeomUtil.rectangleByDataPoint(p, ctx)
            }
        }
    }
}
