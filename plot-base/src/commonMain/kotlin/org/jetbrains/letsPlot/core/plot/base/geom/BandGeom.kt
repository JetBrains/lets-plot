/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
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

        val stripBuilder = Strip.builder(minAes, maxAes, viewPort = viewPort)
        val toVertices = { p: DataPointAesthetics -> stripBuilder(p)?.vertices?.map { v -> v.flipIf(!isVertical) } }
        val toBorders = { p: DataPointAesthetics ->
            stripBuilder(p)?.borders?.map { (start, end, aes) -> Strip.Border(start.flipIf(!isVertical), end.flipIf(!isVertical), aes) }
        }

        linesHelper.createStrips(aesthetics.dataPoints(), toVertices, coord.isLinear).forEach { linePath ->
            root.appendNodes(listOf(linePath))
        }
        getStripBorders(aesthetics.dataPoints(), toBorders, helper).forEach { svg ->
            root.add(svg)
        }
        buildHints(aesthetics, pos, coord, ctx, toBorders)
    }

    private fun getStripBorders(
        dataPoints: Iterable<DataPointAesthetics>,
        toBorders: (DataPointAesthetics) -> List<Strip.Border>?,
        helper: GeomHelper.SvgElementHelper
    ): List<SvgNode> {
        return dataPoints.mapNotNull { p ->
            toBorders(p)?.mapNotNull { border ->
                helper.createLine(border.start, border.end, p)?.first
            }
        }.flatten()
    }

    private fun buildHints(
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        toBorders: (DataPointAesthetics) -> List<Strip.Border>?
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
        val takeDefaultCoord = { point: DoubleVector -> if (isVertical) point.x else point.y }

        aesthetics.dataPoints().forEach { p ->
            toBorders(p)?.forEach { border ->
                for (borderInnerPoint in border.resample(TOOLTIP_SAMPLE_SIZE)) {
                    val defaultColor = p.fill() ?: return

                    hint.defaultCoord(takeDefaultCoord(borderInnerPoint))
                        .defaultColor(defaultColor, alpha = null)

                    val hintsCollection = HintsCollection(p, helper)
                        .addHint(hint.create(border.aes))
                    val tooltipParams = GeomTargetCollector.TooltipParams(
                        tipLayoutHints = hintsCollection.hints,
                        markerColors = colorMapper(p)
                    )
                    helper.toClient(borderInnerPoint, p)?.let { point ->
                        ctx.targetCollector.addPoint(p.index(), point, 0.0, tooltipParams)
                    }
                }
            }
        }
    }

    data class Strip(val band: DoubleRectangle, val borders: List<Border>) {
        val vertices = listOf(
            DoubleVector(band.left, band.top),
            DoubleVector(band.right, band.top),
            DoubleVector(band.right, band.bottom),
            DoubleVector(band.left, band.bottom),
            DoubleVector(band.left, band.top),
        )

        data class Border(val start: DoubleVector, val end: DoubleVector, val aes: Aes<Double>) {
            fun resample(n: Int): List<DoubleVector> {
                return (0 until n).map { i ->
                    start.add(end.subtract(start).mul(i.toDouble() / (n - 1)))
                }
            }
        }

        companion object {
            fun builder(
                minAes: Aes<Double>,
                maxAes: Aes<Double>,
                viewPort: DoubleRectangle
            ): (DataPointAesthetics) -> Strip? {
                val mainRange = viewPort.yRange()
                val secondaryRange = viewPort.xRange()
                fun stripBuilder(p: DataPointAesthetics): Strip? {
                    val minValue = p.finiteOrNull(minAes) ?: return null
                    val maxValue = p.finiteOrNull(maxAes) ?: return null
                    if (minValue > maxValue) return null
                    if (minValue !in mainRange && maxValue !in mainRange) return null
                    val band = DoubleRectangle.LTRB(secondaryRange.lowerEnd, maxValue, secondaryRange.upperEnd, minValue)
                    val minBorder = Border(
                        DoubleVector(secondaryRange.lowerEnd, minValue),
                        DoubleVector(secondaryRange.upperEnd, minValue),
                        minAes
                    )
                    val maxBorder = Border(
                        DoubleVector(secondaryRange.lowerEnd, maxValue),
                        DoubleVector(secondaryRange.upperEnd, maxValue),
                        maxAes
                    )
                    return Strip(band = band, borders = listOf(minBorder, maxBorder))
                }
                return ::stripBuilder
            }
        }
    }

    companion object {
        const val TOOLTIP_SAMPLE_SIZE = 512
        const val HANDLES_GROUPS = false
    }
}