/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.commons.intern.math.pointOnLine
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import kotlin.math.sin
import kotlin.math.tan


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
        helper.setStrokeAlphaEnabled(true)
        helper.setGeometryHandler { aes, lineString -> tooltipHelper.addLine(lineString, aes) }

        for (p in aesthetics.dataPoints()) {
            val x = finiteOrNull(p.x()) ?: continue
            val y = finiteOrNull(p.y()) ?: continue
            val xend = finiteOrNull(p.xend()) ?: continue
            val yend = finiteOrNull(p.yend()) ?: continue

            val clientStart = geomHelper.toClient(DoubleVector(x, y), p) ?: continue
            val clientEnd = geomHelper.toClient(DoubleVector(xend, yend), p) ?: continue

            // Target sizes to move the start/end of the segment
            val targetSizeStart = AesScaling.circleDiameter(p, DataPointAesthetics::sizeStart) / 2 +
                        AesScaling.pointStrokeWidth(p, DataPointAesthetics::strokeStart)
            val targetSizeEnd = AesScaling.circleDiameter(p, DataPointAesthetics::sizeEnd) / 2 +
                        AesScaling.pointStrokeWidth(p, DataPointAesthetics::strokeEnd)

            // Use additional offset to avoid intersection with arrow
            val strokeWidth = AesScaling.strokeWidth(p)
            val segmentArrowOffset = arrowSpec?.angle?.let { angle -> (strokeWidth / 2) / tan(angle) } ?: 0.0

            // Total offsets
            val startOffset = (if (arrowSpec?.isOnFirstEnd == true) segmentArrowOffset else 0.0) +
                    targetSizeStart + spacer
            val endOffset = (if (arrowSpec?.isOnLastEnd == true) segmentArrowOffset else 0.0) +
                    targetSizeEnd + spacer

            val startPoint = pointOnLine(clientStart, clientEnd, startOffset)
            val endPoint = pointOnLine(clientEnd, clientStart, endOffset)

            // draw segment
            val line = helper.createLine(startPoint, endPoint, p) /*{ point: DoubleVector -> point } */?: continue
            root.add(line)
/*
            // tooltip
            targetCollector.addPath(
                listOf(
                    // without additional offsets
                    pointOnLine(clientStart, clientEnd, targetSizeStart),
                    pointOnLine(clientEnd, clientStart, targetSizeEnd)
                ),
                { p.index() },
                GeomTargetCollector.TooltipParams(
                    markerColors = colorsByDataPoint(p)
                )
            )
*/
            // add arrows
            arrowSpec?.let { arrowSpec ->
                // Add offset by geometry width
                val arrowOffset = (strokeWidth / 2) / sin(arrowSpec.angle)
                val start = pointOnLine(clientStart, clientEnd, targetSizeStart + arrowOffset)
                val end = pointOnLine(clientEnd, clientStart, targetSizeEnd + arrowOffset)

                ArrowSpec.createArrows(
                    p,
                    listOf(start, end),
                    arrowSpec
                ).forEach(root::add)
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
