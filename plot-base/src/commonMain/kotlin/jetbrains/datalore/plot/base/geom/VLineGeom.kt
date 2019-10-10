package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgLineElement

class VLineGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PathGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
                .createSvgElementHelper()

        val viewPort = aesViewPort(aesthetics)

        val lines = ArrayList<SvgLineElement>()
        for (p in aesthetics.dataPoints()) {
            val intercept = p.interceptX()
            if (SeriesUtil.isFinite(intercept)) {
                if (viewPort.xRange().contains(intercept!!)) {
                    val start = DoubleVector(intercept, viewPort.top)
                    val end = DoubleVector(intercept, viewPort.bottom)
                    val line = helper.createLine(start, end, p)
                    lines.add(line)
                }
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
//        val RENDERS = listOf(
//                //Aes.X,
//                //Aes.Y,
//                Aes.XINTERCEPT,
//
//                Aes.SIZE, // path width
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.ALPHA
//        )

        const val HANDLES_GROUPS = false
    }
}
