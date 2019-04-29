package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.*
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.HintColorUtil
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.RectTargetCollectorHelper
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.RectanglesHelper

/**
 * geom_tile uses the center of the tile and its size (x, y, width, height).
 */
internal class TileGeom : GeomBase() {

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val slimGroup = helper.createSlimRectangles(rectangleByDataPoint(ctx))
        root.add(wrap(slimGroup))

        RectTargetCollectorHelper(helper, rectangleByDataPoint(ctx), { p: DataPointAesthetics -> HintColorUtil.fromFill(p) })
                .collectTo(ctx.targetCollector)
    }

    companion object {
        val RENDERS = listOf(
                Aes.X,
                Aes.Y,
                Aes.WIDTH,
                Aes.HEIGHT,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.LINETYPE,
                Aes.SIZE
        )

        val HANDLES_GROUPS = false

        private fun rectangleByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle {
            return { p ->
                val x = p.x()
                val y = p.y()
                val w = p.width()
                val h = p.height()

                val width = w!! * ctx.getResolution(Aes.X)
                val height = h!! * ctx.getResolution(Aes.Y)

                val origin = DoubleVector(x!! - width / 2, y!! - height / 2)
                val dimensions = DoubleVector(width, height)
                DoubleRectangle(origin, dimensions)
            }
        }
    }
}
