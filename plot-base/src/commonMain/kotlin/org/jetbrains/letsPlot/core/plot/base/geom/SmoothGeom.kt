/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom.Companion.BASELINE_TEXT_WIDTH
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.ordered_X
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.with_X_Y
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintsCollection.HintConfigFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil.fontFamily
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils
import kotlin.math.max

class SmoothGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val dataPoints = ordered_X(with_X_Y(aesthetics.dataPoints()))
        val helper = LinesHelper(pos, coord, ctx)

        helper.setAlphaEnabled(false)

        // Confidence interval
        val bands = helper.createBands(dataPoints, GeomUtil.TO_LOCATION_X_YMAX, GeomUtil.TO_LOCATION_X_YMIN)
        root.appendNodes(bands)

        // Regression line
        val regressionLines = helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_Y)
        root.appendNodes(regressionLines)

        // Draw annotation
        dataPoints.groupBy { it.group() }.values.forEachIndexed { i, it ->
            val dp = it.first()
            val sizeUnitRatio = AesScaling.sizeUnitRatio(DoubleVector.ZERO, coord, null, BASELINE_TEXT_WIDTH)

            val label = buildTextComponent(i, dp, toString(dp.label(), ctx), sizeUnitRatio, coord, ctx)
            root.add(label)
        }

        buildHints(dataPoints, pos, coord, ctx)
    }

    private fun buildHints(
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.SMOOTH, ctx)
        for (p in dataPoints) {
            val pX = p.x()!!
            val pY = p.y()!!
            val objectRadius = 0.0

            val hint = HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultCoord(pX)
                .defaultKind(
                    if (ctx.flipped) VERTICAL_TOOLTIP else HORIZONTAL_TOOLTIP
                )
                .defaultColor(
                    p.fill()!!,
                    p.alpha()
                )

            val hintsCollection = HintsCollection(p, helper)
                .addHint(hint.create(Aes.YMAX))
                .addHint(hint.create(Aes.YMIN))
                .addHint(hint.create(Aes.Y).color(p.color()!!))

            val clientCoord = helper.toClient(pX, pY, p)!!
            ctx.targetCollector.addPoint(
                p.index(), clientCoord, objectRadius,
                GeomTargetCollector.TooltipParams(
                    tipLayoutHints = hintsCollection.hints,
                    markerColors = colorsByDataPoint(p)
                )
            )
        }
    }

    fun toString(label: Any?, geomContext: GeomContext): String {
        if (label == null) return ""

        val formatter = geomContext.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    private fun buildTextComponent(
        index: Int,
        p: DataPointAesthetics,
        text: String,
        sizeUnitRatio: Double,
        coord: CoordinateSystem,
        ctx: GeomContext,
    ): SvgGElement {
        val viewPort = coord.toClient(overallAesBounds(ctx))!!

        val label = Label(text)
        TextUtil.decorate(label, p, sizeUnitRatio, applyAlpha = false)

        val measure = measure(text, p, ctx, 10.0)

        label.setFontSize(10.0)
        label.setLineHeight(12.0)
        label.setHorizontalAnchor(Text.HorizontalAnchor.RIGHT)
        label.setVerticalAnchor(Text.VerticalAnchor.BOTTOM)

        val location = DoubleVector(
            viewPort.right,
            viewPort.bottom - measure.y * index
        )

        label.moveTo(location)

        val g = SvgGElement()
        g.children().add(label.rootGroup)
        SvgUtils.transformRotate(g, TextUtil.angle(p), location.x, location.y)
        return g
    }

    companion object {
        const val HANDLES_GROUPS = true

        fun measure(text: String, p: DataPointAesthetics, ctx: GeomContext, fontSize: Double = 10.0): DoubleVector {
            val lines = Label.splitLines(text)
            val lineHeight = 12.0
            val fontFamily = fontFamily(p)
            val fontFace = FontFace.fromString(p.fontface())

            val estimated = lines.map { line ->
                ctx.estimateTextSize(line, fontFamily, fontSize, fontFace.bold, fontFace.italic)
            }.fold(DoubleVector.ZERO) { acc, sz ->
                DoubleVector(
                    x = max(acc.x, sz.x),
                    y = acc.y + sz.y
                )
            }
            val lineInterval = lineHeight - fontSize
            val textHeight = estimated.y + lineInterval * (lines.size - 1)
            return DoubleVector(estimated.x, textHeight)
        }
    }
}
