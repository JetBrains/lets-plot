package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgLineElement
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.aes.AestheticsUtil
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.base.event3.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.visualization.plot.base.render.*
import jetbrains.datalore.visualization.plot.base.render.geom.util.GeomHelper
import jetbrains.datalore.visualization.plot.base.render.geom.util.GeomUtil
import jetbrains.datalore.visualization.plot.base.render.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.visualization.plot.base.render.geom.util.HintsCollection
import jetbrains.datalore.visualization.plot.base.render.geom.util.HintsCollection.HintConfigFactory
import jetbrains.datalore.visualization.plot.base.render.geom.util.LinesHelper
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

class ErrorBarGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = MyLegendKeyElementFactory()

    private fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
    }

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = LinesHelper(pos, coord, ctx)
        val dataPoints = dataPoints(aesthetics)
        val geomHelper = GeomHelper(pos, coord, ctx)

        for (p in dataPoints) {
            val x = p.x()
            val ymin = p.ymin()
            val ymax = p.ymax()
            if (!SeriesUtil.allFinite(x, ymin, ymax)) {
                continue
            }
            var width = p.width()!!
            width *= ctx.getResolution(Aes.X)
            val height = ymax!! - ymin!!

            val r = DoubleRectangle(x!! - width / 2, ymin, width, height)

            val g = errorBarShape(helper.toClient(r, p), p)
            root.add(g)

            buildHints(r, p, ctx, geomHelper)
        }
    }

    private fun buildHints(rect: DoubleRectangle, p: DataPointAesthetics, ctx: GeomContext, geomHelper: GeomHelper) {
        val clientRect = geomHelper.toClient(rect, p)

        val hint = HintConfigFactory()
                .defaultObjectRadius(clientRect.width / 2.0)
                .defaultX(p.x()!!)
                .defaultKind(HORIZONTAL_TOOLTIP)

        val hints = HintsCollection(p, geomHelper)
                .addHint(hint.create(Aes.YMAX))
                .addHint(hint.create(Aes.YMIN))
                .hints

        ctx.targetCollector.addRectangle(p.index(), clientRect,
                params()
                        .setTipLayoutHints(hints)
                        .setColor(fromColor(p))
        )
    }

    private class MyLegendKeyElementFactory : LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val strokeWidth = AestheticsUtil.strokeWidth(p)

            val width = p.width()!! * (size.x - strokeWidth)
            val height = size.y - strokeWidth
            val x = (size.x - width) / 2
            val y = strokeWidth / 2
            return errorBarShape(DoubleRectangle(x, y, width, height), p)
        }
    }

    companion object {
        val RENDERS = listOf(
                Aes.X,
                Aes.YMIN,
                Aes.YMAX,
                Aes.WIDTH,

                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA
        )

        const val HANDLES_GROUPS = false

        private fun errorBarShape(r: DoubleRectangle, p: DataPointAesthetics): SvgGElement {
            val left = r.left
            val top = r.top
            val right = r.right
            val bottom = r.bottom
            val center = left + r.width / 2
            val shapeLines = ArrayList<SvgLineElement>()
            shapeLines.add(SvgLineElement(left, top, right, top))
            shapeLines.add(SvgLineElement(left, bottom, right, bottom))
            shapeLines.add(SvgLineElement(center, top, center, bottom))

            val g = SvgGElement()
            for (shapeLine in shapeLines) {
                GeomHelper.decorate(shapeLine, p)
                g.children().add(shapeLine)
            }
            return g
        }
    }

}
