package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgLineElement
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.render.*
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomHelper

internal class HLineGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PathGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
                .createSvgElementHelper()

        val viewPort = aesViewPort(aesthetics)

        val lines = ArrayList<SvgLineElement>()
        for (p in aesthetics.dataPoints()) {
            val intercept = p.interceptY()
            if (SeriesUtil.isFinite(intercept)) {
                if (viewPort.yRange().contains(intercept!!)) {
                    val start = DoubleVector(viewPort.left, intercept)
                    val end = DoubleVector(viewPort.right, intercept)
                    val line = helper.createLine(start, end, p)
                    lines.add(line)
                }
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
        val RENDERS = listOf(
                //Aes.X,
                //Aes.Y,
                Aes.YINTERCEPT,

                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA
        )

        val HANDLES_GROUPS = false
    }
}
