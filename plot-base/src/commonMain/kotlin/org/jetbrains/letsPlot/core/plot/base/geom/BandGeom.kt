/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

class BandGeom : GeomBase() {
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
        val linesHelper = LinesHelper(pos, coord, ctx)
        val viewPort = overallAesBounds(ctx)

        fun toSvg(intercept: Double, startCoord: Double, endCoord: Double, p: DataPointAesthetics, flip: Boolean): SvgNode? {
            val start = DoubleVector(startCoord, intercept).let { vector ->
                if (flip) vector.flip() else vector
            }
            val end = DoubleVector(endCoord, intercept).let { vector ->
                if (flip) vector.flip() else vector
            }
            val (svgTop, _) = helper.createLine(start, end, p) ?: return null
            return svgTop
        }

        val horizontalStrips = linesHelper.createStrips(aesthetics.dataPoints(), toHorizontalStrip(viewPort))
        root.appendNodes(horizontalStrips)
        for (p in aesthetics.dataPoints()) {
            val strip = toHorizontalStrip(viewPort)(p) ?: continue
            toSvg(strip.top, viewPort.left, viewPort.right, p, false)?.also { svgNode -> root.add(svgNode) }
            toSvg(strip.bottom, viewPort.left, viewPort.right, p, false)?.also { svgNode -> root.add(svgNode) }
        }

        val verticalStrips = linesHelper.createStrips(aesthetics.dataPoints(), toVerticalStrip(viewPort))
        root.appendNodes(verticalStrips)
        for (p in aesthetics.dataPoints()) {
            val strip = toVerticalStrip(viewPort)(p) ?: continue
            toSvg(strip.right, viewPort.bottom, viewPort.top, p, true)?.also { svgNode -> root.add(svgNode) }
            toSvg(strip.left, viewPort.bottom, viewPort.top, p, true)?.also { svgNode -> root.add(svgNode) }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun toVerticalStrip(viewPort: DoubleRectangle): (DataPointAesthetics) -> DoubleRectangle? {
            return toStrip(viewPort, Aes.XMIN, Aes.XMAX)
        }

        private fun toHorizontalStrip(viewPort: DoubleRectangle): (DataPointAesthetics) -> DoubleRectangle? {
            return toStrip(viewPort, Aes.YMIN, Aes.YMAX)
        }

        private fun toStrip(
            viewPort: DoubleRectangle,
            minAes: Aes<Double>,
            maxAes: Aes<Double>
        ): (DataPointAesthetics) -> DoubleRectangle? {
            val isVertical = minAes == Aes.XMIN
            val mainRange = if (isVertical) {
                viewPort.xRange()
            } else {
                viewPort.yRange()
            }
            val secondaryRange = if (isVertical) {
                viewPort.yRange()
            } else {
                viewPort.xRange()
            }
            fun stripRectByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
                val minValue = p.finiteOrNull(minAes) ?: return null
                val maxValue = p.finiteOrNull(maxAes) ?: return null
                if (minValue > maxValue) return null
                if (minValue !in mainRange && maxValue !in mainRange) return null
                return if (isVertical) {
                    DoubleRectangle.LTRB(minValue, secondaryRange.upperEnd, maxValue, secondaryRange.lowerEnd)
                } else {
                    DoubleRectangle.LTRB(secondaryRange.lowerEnd, maxValue, secondaryRange.upperEnd, minValue)
                }
            }
            return ::stripRectByDataPoint
        }
    }
}