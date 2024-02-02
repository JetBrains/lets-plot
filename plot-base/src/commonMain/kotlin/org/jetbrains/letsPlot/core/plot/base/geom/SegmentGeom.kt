/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.pointOnLine
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import kotlin.math.*


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
            val strokeWidth = AesScaling.strokeWidth(p)

            val clientStart = geomHelper.toClient(DoubleVector(x, y), p) ?: continue
            val clientEnd = geomHelper.toClient(DoubleVector(xend, yend), p) ?: continue

            // Target sizes to move the start/end of the segment
            val targetSizeStart = targetSize(p, atStart = true)
            val targetSizeEnd = targetSize(p, atStart = false)

            // Additional offset to avoid intersection with arrow
            val segmentArrowOffset = arrowSpec?.let {
                val angle = abs(it.angle)
                (strokeWidth / 2) / tan(angle) * sign(sin(angle))
            } ?: 0.0

            // Total offsets
            val startOffset = targetSizeStart + spacer +
                    (segmentArrowOffset.takeIf { arrowSpec?.isOnFirstEnd == true } ?: 0.0)
            val endOffset = targetSizeEnd + spacer +
                    (segmentArrowOffset.takeIf { arrowSpec?.isOnLastEnd == true } ?: 0.0)

            val startPoint = pointOnLine(clientStart, clientEnd, startOffset)
            val endPoint = pointOnLine(clientEnd, clientStart, endOffset)

            // draw segment
            val line = helper.createLine(startPoint, endPoint, p) { point: DoubleVector -> point } ?: continue
            root.add(line)

            // add arrows
            arrowSpec?.let { arrowSpec ->
                // Add offset for arrow by geometry width
                val angle = abs(arrowSpec.angle)
                val arrowOffset = (strokeWidth / 2) / sin(angle) * sign(tan(angle))
                val start = pointOnLine(clientStart, clientEnd, targetSizeStart + arrowOffset)
                val end = pointOnLine(clientEnd, clientStart, targetSizeEnd + arrowOffset)

                ArrowSpec.createArrows(p, listOf(start, end), arrowSpec)
                    .forEach(root::add)
            }
        }
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
