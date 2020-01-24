/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgRectElement

object CrossBarHelper {
    fun buildBoxes(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        rectFactory: (DataPointAesthetics) -> DoubleRectangle?
    ) {
        // rectangles
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val rectangles = helper.createRectangles(rectFactory)
        rectangles.forEach { root.add(it) }
    }

    fun buildMidlines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        fatten: Double
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val elementHelper = helper.createSvgElementHelper()

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.WIDTH, Aes.MIDDLE)) {
            val x = p.x()!!
            val middle = p.middle()!!
            val width = GeomUtil.widthPx(p, ctx, 2.0)

            val line = elementHelper.createLine(
                DoubleVector(x - width / 2, middle),
                DoubleVector(x + width / 2, middle),
                p
            )

            // adjust thickness
            val thickness = line.strokeWidth().get()!!
            line.strokeWidth().set(thickness * fatten)

            root.add(line)
        }
    }

    fun legendFactory(): LegendKeyElementFactory = CrossBarLegendKeyElementFactory()

    fun buildTooltips(
        hintAesList: List<Aes<Double>>,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        rectFactory: (DataPointAesthetics) -> DoubleRectangle?
    ) {
        val helper = GeomHelper(pos, coord, ctx)

        for (p in aesthetics.dataPoints()) {
            val rect = rectFactory(p) ?: continue

            val xCoord = rect.center.x
            val width = GeomUtil.widthPx(p, ctx, 2.0)
            val clientRect = helper.toClient(DoubleRectangle(0.0, 0.0, width, 0.0), p)
            val objectRadius = clientRect.width / 2.0

            val hintFactory = HintsCollection.HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultX(xCoord)
                .defaultKind(HORIZONTAL_TOOLTIP)

            val hintConfigs = hintAesList
                .fold(HintsCollection(p, helper)) { acc, aes ->
                    acc.addHint(hintFactory.create(aes))
                }

//            val hintsCollection = HintsCollection(p, helper)
//            val hints = hintsCollection
//                .addHint(hintFactory.create(Aes.YMAX))
//                .addHint(hintFactory.create(Aes.UPPER))
//                .addHint(hintFactory.create(Aes.MIDDLE))
//                .addHint(hintFactory.create(Aes.LOWER))
//                .addHint(hintFactory.create(Aes.YMIN))
//                .hints

            ctx.targetCollector.addRectangle(
                p.index(),
                helper.toClient(rect, p),
                params()
                    .setTipLayoutHints(hintConfigs.hints)
                    .setColor(fromColor(p))
            )
        }
    }
}

private class CrossBarLegendKeyElementFactory : LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val whiskerSize = .2

        val strokeWidth = AesScaling.strokeWidth(p)
        val width = (size.x - strokeWidth) * .8 // a bit narrower
        val height = size.y - strokeWidth
        val x = (size.x - width) / 2
        val y = strokeWidth / 2


        // box
        val rect = SvgRectElement(
            x,
            y + height * whiskerSize,
            width,
            height * (1 - 2 * whiskerSize)
        )
        GeomHelper.decorate(rect, p)

        // lines
        val middleY = y + height * .5
        val middle = SvgLineElement(x, middleY, x + width, middleY)
        GeomHelper.decorate(middle, p)
        val middleX = x + width * .5
        val lowerWhisker =
            SvgLineElement(middleX, y + height * (1 - whiskerSize), middleX, y + height)
        GeomHelper.decorate(lowerWhisker, p)
        val upperWhisker = SvgLineElement(middleX, y, middleX, y + height * whiskerSize)
        GeomHelper.decorate(upperWhisker, p)

        val g = SvgGElement()
        g.children().add(rect)
        g.children().add(middle)
        g.children().add(lowerWhisker)
        g.children().add(upperWhisker)
        return g
    }
}

