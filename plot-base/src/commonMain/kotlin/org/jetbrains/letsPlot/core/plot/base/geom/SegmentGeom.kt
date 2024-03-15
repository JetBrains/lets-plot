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
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.toLocation
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
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
        val svgHelper = geomHelper
            .createSvgElementHelper()
            .setResamplingEnabled(!coord.isLinear && !flat)

        for (p in aesthetics.dataPoints()) {
            val start = p.toLocation(Aes.X, Aes.Y) ?: continue
            val end = p.toLocation(Aes.XEND, Aes.YEND) ?: continue
            val segmentGeometry = svgHelper.createLineGeometry(start, end, p) ?: continue

            // Apply padding to segment geometry based on the target size, spacer and arrow spec
            val startPadding = padding(p, arrowSpec, spacer, atStart = true)
            val endPadding = padding(p, arrowSpec, spacer, atStart = false)

            val adjustedSegmentGeometry = padLineString(segmentGeometry, startPadding, endPadding)

            val svgSegmentElement = SvgPathElement(SvgPathDataBuilder().lineString(adjustedSegmentGeometry).build())
            GeomHelper.decorate(svgSegmentElement, p, applyAlphaToAll = true, filled = false)
            root.add(svgSegmentElement)

            arrowSpec
                ?.let { ArrowSpec.createArrows(p, adjustedSegmentGeometry, it) }
                ?.forEach(root::add)

            tooltipHelper.addLine(adjustedSegmentGeometry, p)
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun pad(lineString: List<DoubleVector>, padding: Double): Pair<Int, DoubleVector>? {
            if (lineString.size < 2) {
                return null
            }

            val padding2 = padding * padding
            val indexOutsidePadding = lineString.indexOfFirst { distance2(lineString.first(), it) >= padding2 }
            if (indexOutsidePadding < 1) { // not found or first points already satisfy the padding
                return null
            }

            val adjustedStartPoint = run {
                val insidePadding = lineString[indexOutsidePadding - 1]
                val outsidePadding = lineString[indexOutsidePadding]
                val overPadding = distance(lineString.first(), outsidePadding) - padding

                pointOnLine(outsidePadding, insidePadding, overPadding)
            }

            return indexOutsidePadding to adjustedStartPoint
        }

        private fun padStart(lineString: List<DoubleVector>, padding: Double): List<DoubleVector> {
            val (index, adjustedStartPoint) = pad(lineString, padding) ?: return lineString
            return listOf(adjustedStartPoint) + lineString.subList(index, lineString.size)
        }

        private fun padEnd(lineString: List<DoubleVector>, padding: Double): List<DoubleVector> {
            val (index, adjustedEndPoint) = pad(lineString.asReversed(), padding) ?: return lineString
            return lineString.subList(0, lineString.size - index) + adjustedEndPoint
        }

        fun padLineString(
            lineString: List<DoubleVector>,
            startPadding: Double,
            endPadding: Double
        ): List<DoubleVector> {
            val startPadded = padStart(lineString, startPadding)
            return padEnd(startPadded, endPadding)
        }

        fun padding(
            p: DataPointAesthetics,
            arrowSpec: ArrowSpec?,
            spacer: Double,
            atStart: Boolean
        ): Double {
            val targetSize = AesScaling.targetSize(p, atStart)

            val miterOffset = arrowSpec?.let {
                val hasArrow = if (atStart) arrowSpec.isOnFirstEnd else arrowSpec.isOnLastEnd
                if (hasArrow) {
                    val strokeWidth = AesScaling.strokeWidth(p)
                    val miterLength = ArrowSpec.miterLength(arrowSpec.angle * 2, strokeWidth)
                    val miterSign = sign(sin(arrowSpec.angle * 2))
                    miterLength * miterSign / 2
                } else {
                    0.0
                }
            } ?: 0.0

            return targetSize + spacer + miterOffset
        }
    }
}
