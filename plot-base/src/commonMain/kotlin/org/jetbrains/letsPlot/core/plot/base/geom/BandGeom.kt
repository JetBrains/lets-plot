/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

/*
  For this geometry 'isVertical' means that it has vertical bounds: ymin and ymax.
*/
class BandGeom(private val isVertical: Boolean) : GeomBase() {
    private val flipHelper = FlippableGeomHelper(isVertical)

    private val minAes = flipHelper.getEffectiveAes(Aes.YMIN)
    private val maxAes = flipHelper.getEffectiveAes(Aes.YMAX)

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.XMIN, Aes.XMAX).map(flipHelper::getEffectiveAes)
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx).createSvgElementHelper()
            .setStrokeAlphaEnabled(true)
            .setResamplingEnabled(!coord.isLinear)
        val linesHelper = LinesHelper(pos, coord, ctx)
        val viewPort = overallAesBounds(ctx).flipIf(!isVertical)

        val toOrientedStrip = { p: DataPointAesthetics -> toStrip(viewPort)(p)?.flipIf(!isVertical) }
        linesHelper.createStrips(aesthetics.dataPoints(), toOrientedStrip).forEach { linePath ->
            root.appendNodes(listOf(linePath))
        }

        val toOrientedTopBorder = { p: DataPointAesthetics -> toBorder(viewPort) { rect -> rect.top }(p)?.flipIf(!isVertical) }
        createStripBorders(aesthetics.dataPoints(), toOrientedTopBorder, helper).forEach { svgNode ->
            root.add(svgNode)
        }
        val toOrientedBottomBorder = { p: DataPointAesthetics -> toBorder(viewPort) { rect -> rect.bottom }(p)?.flipIf(!isVertical) }
        createStripBorders(aesthetics.dataPoints(), toOrientedBottomBorder, helper).forEach { svgNode ->
            root.add(svgNode)
        }

        buildHints(aesthetics, pos, coord, ctx, viewPort)
    }

    private fun toStrip(viewPort: DoubleRectangle): (DataPointAesthetics) -> DoubleRectangle? {
        val mainRange = viewPort.yRange()
        val secondaryRange = viewPort.xRange()
        fun stripRectByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
            val minValue = p.finiteOrNull(minAes) ?: return null
            val maxValue = p.finiteOrNull(maxAes) ?: return null
            if (minValue > maxValue) return null
            if (minValue !in mainRange && maxValue !in mainRange) return null
            return DoubleRectangle.LTRB(secondaryRange.lowerEnd, maxValue, secondaryRange.upperEnd, minValue)
        }
        return ::stripRectByDataPoint
    }

    private fun toBorder(
        viewPort: DoubleRectangle,
        select: (DoubleRectangle) -> Double
    ): (DataPointAesthetics) -> Border? {
        return { p ->
            toStrip(viewPort)(p)?.let(select)?.let { intercept ->
                Border(
                    DoubleVector(viewPort.left, intercept),
                    DoubleVector(viewPort.right, intercept)
                )
            }
        }
    }

    private fun createStripBorders(
        dataPoints: Iterable<DataPointAesthetics>,
        toBorder: (DataPointAesthetics) -> Border?,
        helper: GeomHelper.SvgElementHelper
    ): List<SvgNode> {
        return dataPoints
            .mapNotNull { p ->
                toBorder(p)?.let { border ->
                    helper.createLine(border.start, border.end, p)?.first
                }
            }
    }

    private fun buildHints(
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        viewPort: DoubleRectangle
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorMapper = HintColorUtil.createColorMarkerMapper(GeomKind.BAND, ctx)
        val isVerticallyOriented = when (isVertical) {
            true -> !ctx.flipped
            false -> ctx.flipped
        }
        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(0.0)
            .defaultKind(VERTICAL_TOOLTIP.takeIf { isVerticallyOriented } ?: HORIZONTAL_TOOLTIP)

        for (p in aesthetics.dataPoints()) {
            for (x in resample(viewPort.xRange())) {
                for (aes in listOf(minAes, maxAes)) {
                    val value = p[aes] ?: continue
                    val defaultColor = p.fill() ?: continue

                    hint.defaultCoord(x)
                        .defaultColor(defaultColor, alpha = null)

                    val hintsCollection = HintsCollection(p, helper)
                        .addHint(hint.create(aes))
                    val tooltipParams = GeomTargetCollector.TooltipParams(
                        tipLayoutHints = hintsCollection.hints,
                        markerColors = colorMapper(p)
                    )
                    helper.toClient(DoubleVector(x, value).flipIf(!isVertical), p)?.let { point ->
                        ctx.targetCollector.addPoint(p.index(), point, 0.0, tooltipParams)
                    }
                }
            }
        }
    }

    private fun resample(range: DoubleSpan): List<Double> {
        return (0 until TOOLTIP_SAMPLE_SIZE).map { i ->
            range.lowerEnd + (i.toDouble() / (TOOLTIP_SAMPLE_SIZE - 1)) * (range.upperEnd - range.lowerEnd)
        }
    }

    data class Border(val start: DoubleVector, val end: DoubleVector) {
        fun flipIf(flipped: Boolean): Border {
            return Border(start.flipIf(flipped), end.flipIf(flipped))
        }
    }

    companion object {
        const val TOOLTIP_SAMPLE_SIZE = 512
        const val HANDLES_GROUPS = false
    }
}