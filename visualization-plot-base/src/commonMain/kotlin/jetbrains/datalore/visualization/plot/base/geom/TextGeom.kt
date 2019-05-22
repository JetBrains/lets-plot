package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.geom.util.GeomHelper
import jetbrains.datalore.visualization.plot.base.render.*
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

class TextGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory()

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
        for (p in aesthetics.dataPoints()) {
            val x = p.x()
            val y = p.y()
            val text = p.label()
            if (SeriesUtil.allFinite(x, y) && !Strings.isNullOrEmpty(text)) {
                val label = TextLabel(text)
                GeomHelper.decorate(label, p)

                val loc = helper.toClient(x, y, p)
                label.moveTo(loc)
                root.add(label.rootGroup)
            }
        }
    }

    companion object {
        val RENDERS = listOf(
                Aes.X,
                Aes.Y,
                Aes.SIZE,
                Aes.COLOR,
                Aes.ALPHA,

                Aes.LABEL,
                Aes.FAMILY,
                Aes.FONTFACE,
                Aes.HJUST,
                Aes.VJUST,
                Aes.ANGLE
        )

        val HANDLES_GROUPS = false
    }
}// How 'just' and 'angle' works together
// https://stackoverflow.com/questions/7263849/what-do-hjust-and-vjust-do-when-making-a-plot-using-ggplot
// ToDo: lineheight (aes)
// ToDo: nudge_x, nudge_y

