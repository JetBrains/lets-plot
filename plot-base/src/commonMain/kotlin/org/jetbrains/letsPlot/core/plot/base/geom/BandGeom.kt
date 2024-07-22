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

        linesHelper.createStrips(aesthetics.dataPoints(), toStrip(viewPort), coord.isLinear).forEach { linePath ->
            root.appendNodes(listOf(linePath))
        }
        getStripBorders(aesthetics.dataPoints(), viewPort, helper).forEach { svg ->
            root.add(svg)
        }
        buildHints(aesthetics, pos, coord, ctx, viewPort)
    }

    private fun toStrip(
        viewPort: DoubleRectangle
    ): (DataPointAesthetics) -> DoubleRectangle? {
        val mainRange = viewPort.yRange()
        val secondaryRange = viewPort.xRange()
        fun stripRectByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
            val minValue = p.finiteOrNull(minAes) ?: return null
            val maxValue = p.finiteOrNull(maxAes) ?: return null
            if (minValue > maxValue) return null
            if (minValue !in mainRange && maxValue !in mainRange) return null
            return DoubleRectangle.LTRB(secondaryRange.lowerEnd, maxValue, secondaryRange.upperEnd, minValue).flipIf(!isVertical)
        }
        return ::stripRectByDataPoint
    }

    private fun getStripBorders(
        dataPoints: Iterable<DataPointAesthetics>,
        viewPort: DoubleRectangle,
        helper: GeomHelper.SvgElementHelper
    ): List<SvgNode> {
        return dataPoints.mapNotNull { p ->
            toStrip(viewPort)(p)?.let { strip ->
                if (isVertical) {
                    listOf(strip.top, strip.bottom)
                } else {
                    listOf(strip.left, strip.right)
                }.filterNot { intercept ->
                    intercept in setOf(viewPort.top, viewPort.bottom)
                }.mapNotNull { intercept ->
                    getStripBorder(intercept, viewPort, p, helper)
                }
            }
        }.flatten()
    }

    private fun getStripBorder(
        intercept: Double,
        viewPort: DoubleRectangle,
        p: DataPointAesthetics,
        helper: GeomHelper.SvgElementHelper
    ): SvgNode? {
        val start = DoubleVector(viewPort.left, intercept).flipIf(!isVertical)
        val end = DoubleVector(viewPort.right, intercept).flipIf(!isVertical)
        return helper.createLine(start, end, p)?.first
    }

    private fun resample(range: DoubleSpan): List<Double> {
        return (0 until TOOLTIP_SAMPLE_SIZE).map { i ->
            range.lowerEnd + (i.toDouble() / (TOOLTIP_SAMPLE_SIZE - 1)) * (range.upperEnd - range.lowerEnd)
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

    companion object {
        const val TOOLTIP_SAMPLE_SIZE = 512
        const val HANDLES_GROUPS = false
    }
}