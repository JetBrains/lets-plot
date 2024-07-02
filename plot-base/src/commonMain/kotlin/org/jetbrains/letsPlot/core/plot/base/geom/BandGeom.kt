/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

/*
  For this geometry 'isVertical' means that it has vertical bounds: ymin and ymax.
*/
class BandGeom(isVertical: Boolean) : GeomBase() {
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
        val colorMapper = HintColorUtil.createColorMarkerMapper(GeomKind.BAND, ctx)
        val viewPort = overallAesBounds(ctx)

        linesHelper.createStrips(aesthetics.dataPoints(), toStrip(viewPort), coord.isLinear) { p, linePath, polygon ->
            root.appendNodes(listOf(linePath))
            buildHints(p, polygon, geomHelper, colorMapper(p), ctx)
        }
        buildStripBorders(aesthetics.dataPoints(), viewPort, helper) { root.add(it) }
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
                    buildStripBorder(intercept, viewPort, p, helper)?.let { svgNode ->
                        handler(svgNode)
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
    ): SvgNode? {
        val start = afterRotation(DoubleVector(afterRotation(viewPort).left, intercept))
        val end = afterRotation(DoubleVector(afterRotation(viewPort).right, intercept))
        return helper.createLine(start, end, p)?.first ?: return null
    }

    private fun buildHints(
        p: DataPointAesthetics,
        polygon: List<DoubleVector>,
        helper: GeomHelper,
        markerColors: List<Color>,
        ctx: GeomContext
    ) {
        val hintsCollection = HintsCollection(p, helper)
        val tooltipParams = GeomTargetCollector.TooltipParams(
            tipLayoutHints = hintsCollection.hints,
            markerColors = markerColors
        )
        ctx.targetCollector.addPolygon(polygon, p.index(), tooltipParams)
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}