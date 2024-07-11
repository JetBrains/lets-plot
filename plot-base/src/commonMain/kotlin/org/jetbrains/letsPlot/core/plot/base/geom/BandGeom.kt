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

    private fun afterRotation(aes: Aes<Double>): Aes<Double> {
        return flipHelper.getEffectiveAes(aes)
    }

    private fun afterRotation(vector: DoubleVector): DoubleVector {
        return flipHelper.flip(vector)
    }

    private fun afterRotation(rectangle: DoubleRectangle): DoubleRectangle {
        return flipHelper.flip(rectangle)
    }

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.XMIN, Aes.XMAX).map(::afterRotation)
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
            .setStrokeAlphaEnabled(true)
            .setResamplingEnabled(!coord.isLinear)
        val linesHelper = LinesHelper(pos, coord, ctx)
        val viewPort = overallAesBounds(ctx)

        linesHelper.createStrips(aesthetics.dataPoints(), toStrip(viewPort), coord.isLinear) { linePath ->
            root.appendNodes(listOf(linePath))
        }
        buildStripBorders(aesthetics.dataPoints(), viewPort, helper) { svg ->
            root.add(svg)
        }

        buildHints(aesthetics, pos, coord, ctx)
    }

    private fun toStrip(
        viewPort: DoubleRectangle
    ): (DataPointAesthetics) -> DoubleRectangle? {
        val minAes = afterRotation(Aes.YMIN)
        val maxAes = afterRotation(Aes.YMAX)
        val mainRange = afterRotation(viewPort).yRange()
        val secondaryRange = afterRotation(viewPort).xRange()
        fun stripRectByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
            val minValue = p.finiteOrNull(minAes) ?: return null
            val maxValue = p.finiteOrNull(maxAes) ?: return null
            if (minValue > maxValue) return null
            if (minValue !in mainRange && maxValue !in mainRange) return null
            return afterRotation(DoubleRectangle.LTRB(secondaryRange.lowerEnd, maxValue, secondaryRange.upperEnd, minValue))
        }
        return ::stripRectByDataPoint
    }

    private fun buildStripBorders(
        dataPoints: Iterable<DataPointAesthetics>,
        viewPort: DoubleRectangle,
        helper: GeomHelper.SvgElementHelper,
        handler: (SvgNode) -> Unit
    ) {
        for (p in dataPoints) {
            toStrip(viewPort)(p)?.let { strip ->
                listOf(
                    afterRotation(strip).top,
                    afterRotation(strip).bottom
                ).forEach { intercept ->
                    buildStripBorder(intercept, viewPort, p, helper)?.let { (svg, _) ->
                        handler(svg)
                    }
                }
            }
        }
    }

    private fun buildStripBorder(
        intercept: Double,
        viewPort: DoubleRectangle,
        p: DataPointAesthetics,
        helper: GeomHelper.SvgElementHelper
    ): Pair<SvgNode, List<DoubleVector>>? {
        val start = afterRotation(DoubleVector(afterRotation(viewPort).left, intercept))
        val end = afterRotation(DoubleVector(afterRotation(viewPort).right, intercept))
        return helper.createLine(start, end, p)
    }

    private fun resample(range: DoubleSpan, n: Int = 512): List<Double> {
        return (0 until n).map { i ->
            range.lowerEnd + (i.toDouble() / (n - 1)) * (range.upperEnd - range.lowerEnd)
        }
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorMapper = HintColorUtil.createColorMarkerMapper(GeomKind.BAND, ctx)
        val isVerticallyOriented = when (isVertical) {
            true -> !ctx.flipped
            false -> ctx.flipped
        }
        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(0.0)
            .defaultKind(VERTICAL_TOOLTIP.takeIf { isVerticallyOriented } ?: HORIZONTAL_TOOLTIP)

        val minAes = afterRotation(Aes.YMIN)
        val maxAes = afterRotation(Aes.YMAX)
        val viewPort = afterRotation(overallAesBounds(ctx))
        val xRange = resample(DoubleSpan(viewPort.left, viewPort.right))

        for (p in aesthetics.dataPoints()) {
            for (x in xRange) {
                val top = p[maxAes] ?: continue
                val bottom = p[minAes] ?: continue
                val defaultColor = p.fill() ?: continue

                hint.defaultCoord(x)
                    .defaultColor(defaultColor, alpha = null)

                val hintsCollectionTop = HintsCollection(p, helper, maxAes)
                    .addHint(hint.create(maxAes))
                val tooltipParamsTop = GeomTargetCollector.TooltipParams(
                    tipLayoutHints = hintsCollectionTop.hints,
                    markerColors = colorMapper(p)
                )
                helper.toClient(afterRotation(DoubleVector(x, top)), p)?.let { topPoint ->
                    ctx.targetCollector.addPoint(p.index(), topPoint, 0.0, tooltipParamsTop)
                }
                val hintsCollectionBottom = HintsCollection(p, helper, minAes)
                    .addHint(hint.create(minAes))
                val tooltipParamsBottom = GeomTargetCollector.TooltipParams(
                    tipLayoutHints = hintsCollectionBottom.hints,
                    markerColors = colorMapper(p)
                )
                helper.toClient(afterRotation(DoubleVector(x, bottom)), p)?.let { bottomPoint ->
                    ctx.targetCollector.addPoint(p.index(), bottomPoint, 0.0, tooltipParamsBottom)
                }
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}