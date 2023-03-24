/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.HintsCollection
import jetbrains.datalore.plot.base.geom.util.HintsCollection.HintConfigFactory
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement

class ErrorBarGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = MyLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.ERROR_BAR, ctx)

        for (p in GeomUtil.withDefined(
            aesthetics.dataPoints(),
            Aes.X,
            Aes.YMIN,
            Aes.YMAX,
            Aes.WIDTH
        )) {
            val x = p.x()!!
            val ymin = p.ymin()!!
            val ymax = p.ymax()!!
            val width = p.width()!! * ctx.getResolution(Aes.X)
            val height = ymax - ymin

            val r = DoubleRectangle(x - width / 2, ymin, width, height)
            val g = errorBarShape(r, p, geomHelper)
            root.add(g)

            buildHints(
                DoubleRectangle(r.left, r.center.y, r.width, 0.0),
                p,
                ctx,
                geomHelper,
                colorsByDataPoint
            )
        }
    }

    private fun buildHints(
        rect: DoubleRectangle,
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>
    ) {
        val clientRect = geomHelper.toClient(rect, p)
        val objectRadius = clientRect?.run {
            if (ctx.flipped) {
                height / 2.0
            } else {
                width / 2.0
            }
        }!!

        val hint = HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultX(p.x()!!)
            .defaultKind(
                if (ctx.flipped) {
                    TipLayoutHint.Kind.ROTATED_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )

        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(Aes.YMAX))
            .addHint(hint.create(Aes.YMIN))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(),
            clientRect,
            GeomTargetCollector.TooltipParams(
                tipLayoutHints = hints,
                markerColors = colorsByDataPoint(p)
            ),
            tooltipKind = if (ctx.flipped) {
                TipLayoutHint.Kind.VERTICAL_TOOLTIP
            } else {
                TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
            }
        )
    }

    private class MyLegendKeyElementFactory :
        LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val strokeWidth = AesScaling.lineStrokeWidth(p)

            val width = p.width()!! * (size.x - strokeWidth)
            val height = size.y - strokeWidth
            val x = (size.x - width) / 2
            val y = strokeWidth / 2
            return errorBarLegendShape(DoubleRectangle(x, y, width, height), p)
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun errorBarLegendShape(r: DoubleRectangle, p: DataPointAesthetics): SvgGElement {
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

        private fun errorBarShape(r: DoubleRectangle, p: DataPointAesthetics, geomHelper: GeomHelper): SvgGElement {
            val left = r.left
            val top = r.top
            val right = r.right
            val bottom = r.bottom
            val center = left + r.width / 2

            val g = SvgGElement()
            val elementHelper = geomHelper.createSvgElementHelper()
            elementHelper.setStrokeAlphaEnabled(true)
            with(g.children()) {
                add(elementHelper.createLine(DoubleVector(left, top), DoubleVector(right, top), p)!!)
                add(elementHelper.createLine(DoubleVector(left, bottom), DoubleVector(right, bottom), p)!!)
                add(elementHelper.createLine(DoubleVector(center, top), DoubleVector(center, bottom), p)!!)
            }
            return g
        }
    }
}
