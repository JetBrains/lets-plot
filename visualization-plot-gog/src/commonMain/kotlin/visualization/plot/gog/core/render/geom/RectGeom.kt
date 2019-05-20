package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.render.*
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.RectanglesHelper

internal class RectGeom : GeomBase() {

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val rectangles = helper.createRectangles { p: DataPointAesthetics -> rectangleByDataPoint(p) }
        rectangles.forEach { root.add(it) }
    }

    companion object {
        val RENDERS = listOf(
                Aes.XMIN,
                Aes.XMAX,
                Aes.YMIN,
                Aes.YMAX,
                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.MAP_ID
        )
        //rectangle groups are used in geom_livemap
        val HANDLES_GROUPS = true

        private fun rectangleByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
            val xmin = p.xmin()
            val xmax = p.xmax()
            val ymin = p.ymin()
            val ymax = p.ymax()
            return if (!SeriesUtil.allFinite(xmin, xmax, ymin, ymax)) {
                null
            } else DoubleRectangle.span(DoubleVector(xmin!!, ymin!!), DoubleVector(xmax!!, ymax!!))
        }
    }
}
