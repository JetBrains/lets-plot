package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.render.*
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.ArrowSpec
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomHelper
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomHelper.Companion.decorate
import kotlin.math.PI
import kotlin.math.atan2

internal class SegmentGeom : GeomBase() {

    var arrowSpec: ArrowSpec? = null
    var animation: Any? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PathGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
                .createSvgElementHelper()

        for (p in aesthetics.dataPoints()) {
            if (SeriesUtil.allFinite(p.x(), p.y(), p.xend(), p.yend())) {
                val start = DoubleVector(p.x()!!, p.y()!!)
                val end = DoubleVector(p.xend()!!, p.yend()!!)
                val line = helper.createLine(start, end, p)
                root.add(line)

                if (arrowSpec != null) {
                    val clientX1 = line.x1().get()!!
                    val clientY1 = line.y1().get()!!
                    val clientX2 = line.x2().get()!!
                    val clientY2 = line.y2().get()!!

                    val abscissa = clientX2 - clientX1
                    val ordinate = clientY2 - clientY1
                    if (abscissa != 0.0 || ordinate != 0.0) {
                        // Compute the angle that the vector defined by this segment makes with the
                        // X-axis (radians)
                        val polarAngle = atan2(ordinate, abscissa)

                        val arrowAes = arrowSpec!!.toArrowAes(p)
                        if (arrowSpec!!.isOnLastEnd) {
                            val arrow = arrowSpec!!.createElement(polarAngle, clientX2, clientY2)
                            decorate(arrow, arrowAes)
                            root.add(arrow)
                        }
                        if (arrowSpec!!.isOnFirstEnd) {
                            val arrow = arrowSpec!!.createElement(polarAngle + PI, clientX1, clientY1)
                            decorate(arrow, arrowAes)
                            root.add(arrow)
                        }
                    }
                }
            }
        }
    }

    companion object {
        val RENDERS = listOf(
                Aes.X,
                Aes.Y,
                Aes.XEND,
                Aes.YEND,
                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA,
                Aes.SPEED,
                Aes.FLOW
        )

        val HANDLES_GROUPS = false
    }
}
