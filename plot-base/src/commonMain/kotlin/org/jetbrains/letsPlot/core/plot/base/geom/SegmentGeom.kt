/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.intern.math.distance2
import org.jetbrains.letsPlot.commons.intern.math.pointOnLine
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.math.sign
import kotlin.math.sin


class SegmentGeom : GeomBase() {

    var arrowSpec: ArrowSpec? = null
    var animation: Any? = null
    var flat: Boolean = false
    var geodesic: Boolean = false
    var spacer: Double = 0.0    // additional space to shorten segment by moving the start/end

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val tooltipHelper = TargetCollectorHelper(GeomKind.SEGMENT, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()

        if (!coord.isLinear && !flat) {
            helper.setResamplingEnabled(true)
        }

        helper.setStrokeAlphaEnabled(true)
        helper.setGeometryHandler { aes, lineString -> tooltipHelper.addLine(lineString, aes) }

        for (p in aesthetics.dataPoints()) {
            val x = finiteOrNull(p.x()) ?: continue
            val y = finiteOrNull(p.y()) ?: continue
            val xend = finiteOrNull(p.xend()) ?: continue
            val yend = finiteOrNull(p.yend()) ?: continue

            val start = DoubleVector(x, y)
            val end = DoubleVector(xend, yend)

            val strokeWidth = AesScaling.strokeWidth(p)

            // Target sizes to adjust the start/end of the segment
            val targetSizeStart = targetSize(p, atStart = true)
            val targetSizeEnd = targetSize(p, atStart = false)

            val miterLength = arrowSpec?.angle?.let { ArrowSpec.miterLength(it * 2, strokeWidth) } ?: 0.0
            val miterSign = arrowSpec?.angle?.let { sign(sin(it * 2)) } ?: 0.0
            val miterOffset = miterLength * miterSign / 2

            // Total offsets
            val startPadding = targetSizeStart + spacer + (miterOffset.takeIf { arrowSpec?.isOnFirstEnd == true } ?: 0.0)
            val endPadding = targetSizeEnd + spacer + (miterOffset.takeIf { arrowSpec?.isOnLastEnd == true } ?: 0.0)

            if (coord.isLinear) {
                val clientStart = geomHelper.toClient(start, p) ?: continue
                val clientEnd = geomHelper.toClient(end, p) ?: continue

                val startPoint = pointOnLine(clientStart, clientEnd, startPadding)
                val endPoint = pointOnLine(clientEnd, clientStart, endPadding)
                tooltipHelper.addLine(listOf(startPoint, endPoint), p)

                // draw segment
                val line = SvgLineElement(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
                GeomHelper.decorate(line, p, applyAlphaToAll = true, filled = false)
                root.add(line)

                // add arrows
                arrowSpec?.let { arrowSpec ->
                    ArrowSpec.createArrows(p, listOf(startPoint, endPoint), arrowSpec)
                        .forEach(root::add)
                }
            } else {
                if (arrowSpec == null) {
                    helper.createLine(start, end, p)?.let(root::add)
                } else {
                    // New helper to not trigger geometry callback
                    val lineGeometry = geomHelper
                        .createSvgElementHelper()
                        .setResamplingEnabled(true)
                        .createLineGeometry(start, end, p)
                        ?: continue

                    val adjustedGeometry = padPath(lineGeometry, startPadding, endPadding)

                    tooltipHelper.addLine(adjustedGeometry, p)

                    ArrowSpec.createArrows(p, adjustedGeometry, arrowSpec!!).forEach(root::add)

                    val svgPathElement = SvgPathElement(SvgPathDataBuilder().lineString(adjustedGeometry).build())
                    GeomHelper.decorate(svgPathElement, p, applyAlphaToAll = true, filled = false)
                    root.add(svgPathElement)
                }
            }
        }
    }

    private fun padPath(lineString: List<DoubleVector>, startPadding: Double, endPadding: Double): List<DoubleVector> {
        if (lineString.size < 3) {
            return lineString
        }

        val startPadding2 = startPadding * startPadding
        val startPoint = lineString.first()
        val indexOutsideStartPadding = lineString.indexOfFirst { distance2(startPoint, it) >= startPadding2 }
        if (indexOutsideStartPadding < 1) { // not found or first points already satisfy the padding
            return lineString
        }

        val adjustedStartPoint = run {
            val insidePadding = lineString[indexOutsideStartPadding - 1]
            val outsidePadding = lineString[indexOutsideStartPadding]
            val overPadding = distance(startPoint, outsidePadding) - startPadding

            pointOnLine(outsidePadding, insidePadding, overPadding)
        }

        val endPadding2 = endPadding * endPadding
        val endPoint = lineString.last()
        val indexOutsideEndPadding = lineString.indexOfLast { distance2(endPoint, it) >= endPadding2 }
        if (indexOutsideEndPadding < 1) { // not found or first points already satisfy the padding
            return lineString
        }

        val adjustedEndPoint = run {
            val insidePadding = lineString[indexOutsideEndPadding + 1]
            val outsidePadding = lineString[indexOutsideEndPadding]
            val overPadding = distance(endPoint, outsidePadding) - endPadding

            pointOnLine(outsidePadding, insidePadding, overPadding)
        }

        return listOf(adjustedStartPoint) + lineString.subList(indexOutsideStartPadding, indexOutsideEndPadding) + adjustedEndPoint
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun targetSize(p: DataPointAesthetics, atStart: Boolean): Double {
            val sizeAes = if (atStart) DataPointAesthetics::sizeStart else DataPointAesthetics::sizeEnd
            val strokeAes = if (atStart) DataPointAesthetics::strokeStart else DataPointAesthetics::strokeEnd
            return AesScaling.circleDiameter(p, sizeAes) / 2 + AesScaling.pointStrokeWidth(p, strokeAes)
        }
    }
}
