package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.base.svg.SvgLineElement

class ABLineGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PathGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
                .createSvgElementHelper()

        val viewPort = aesViewPort(aesthetics)
        val boundaries = Iterables.toList(viewPort.parts)

        val lines = ArrayList<SvgLineElement>()
        for (p in aesthetics.dataPoints()) {
            val intercept = p.intercept()
            val slope = p.slope()
            if (SeriesUtil.allFinite(intercept, slope)) {
                val p1 = DoubleVector(viewPort.left, intercept!! + viewPort.left * slope!!)
                val p2 = DoubleVector(viewPort.right, p1.y + viewPort.dimension.x * slope)
                val s = DoubleSegment(p1, p2)

                val lineEnds = HashSet<DoubleVector>(2)
                for (boundary in boundaries) {
                    val intersection = boundary.intersection(s)
                    if (intersection != null) {
                        lineEnds.add(intersection)
                        if (lineEnds.size == 2) {
                            break
                        }
                    }
                }

                if (lineEnds.size == 2) {
                    val it = lineEnds.iterator()
                    val line = helper.createLine(it.next(), it.next(), p)
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
//                Aes.INTERCEPT,
//                Aes.SLOPE,
//
//                Aes.SIZE, // path width
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.ALPHA
//        )

        const val HANDLES_GROUPS = false
    }
}
