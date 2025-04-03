/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectangleTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

class ErrorBarGeom : GeomBase(), WithWidth {
    var widthUnit: DimensionUnit = DEF_WIDTH_UNIT

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = ErrorBarLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.ERROR_BAR, ctx)
        val tooltipHelper = RectangleTooltipHelper(
            pos = pos,
            coord = coord,
            ctx = ctx,
            hintAesList = listOf(Aes.YMIN, Aes.YMAX),
            colorMarkerMapper = colorsByDataPoint
        )

        for (p in aesthetics.dataPoints()) {
            val x = p.finiteOrNull(Aes.X) ?: continue
            val ymin = p.finiteOrNull(Aes.YMIN) ?: continue
            val ymax = p.finiteOrNull(Aes.YMAX) ?: continue

            val width = widthOrNull(p, geomHelper) ?: continue
            val height = ymax - ymin

            val rect = DoubleRectangle(x - width / 2, ymin, width, height)
            val segments = errorBarShapeSegments(rect)
            val g = errorBarShape(segments, p, geomHelper)
            root.add(g)
        }
        // tooltip
        val hintHelper = RectanglesHelper(aesthetics, pos, coord, ctx, rectByDataPoint(geomHelper))
        hintHelper.createRectangles { aes, _, rect -> tooltipHelper.addTarget(aes, rect) }
    }

    private fun rectByDataPoint(geomHelper: GeomHelper): (DataPointAesthetics) -> DoubleRectangle? {
        fun factory(p: DataPointAesthetics): DoubleRectangle? {
            val x = p.finiteOrNull(Aes.X) ?: return null
            val ymin = p.finiteOrNull(Aes.YMIN) ?: return null
            val ymax = p.finiteOrNull(Aes.YMAX) ?: return null
            val width = widthOrNull(p, geomHelper) ?: return null

            val height = ymax - ymin
            return DoubleRectangle(x - width / 2.0, ymax - height / 2.0, width, 0.0)
        }

        return ::factory
    }

    private fun widthOrNull(
        p: DataPointAesthetics,
        helper: GeomHelper
    ): Double? {
        val width = p.finiteOrNull(Aes.WIDTH) ?: return null
        return width * helper.getUnitResolution(widthUnit, Aes.X)
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return DimensionsUtil.dimensionSpan(p, coordAes, Aes.WIDTH, resolution, widthUnit)
    }

    internal class ErrorBarLegendKeyElementFactory : LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val strokeWidth = AesScaling.strokeWidth(p)

            val width = p.width()!! * (size.x - strokeWidth)
            val height = size.y - strokeWidth
            val x = (size.x - width) / 2
            val y = strokeWidth / 2
            return errorBarLegendShape(
                errorBarShapeSegments(DoubleRectangle(x, y, width, height)), p
            )
        }
    }

    companion object {
        private val DEF_WIDTH_UNIT: DimensionUnit = DimensionUnit.RESOLUTION

        private fun errorBarLegendShape(segments: List<DoubleSegment>, p: DataPointAesthetics): SvgGElement {
            val g = SvgGElement()
            segments.forEach { segment ->
                val shapeLine = SvgLineElement(segment.start.x, segment.start.y, segment.end.x, segment.end.y)
                GeomHelper.decorate(shapeLine, p)
                g.children().add(shapeLine)
            }
            return g
        }

        private fun errorBarShapeSegments(r: DoubleRectangle): List<DoubleSegment> {
            val center = r.left + r.width / 2
            return with(r) {
                listOf(
                    DoubleSegment(DoubleVector(left, top), DoubleVector(right, top)),
                    DoubleSegment(DoubleVector(left, bottom), DoubleVector(right, bottom)),
                    DoubleSegment(DoubleVector(center, top), DoubleVector(center, bottom))
                )
            }
        }

        private fun errorBarShape(
            segments: List<DoubleSegment>,
            p: DataPointAesthetics,
            geomHelper: GeomHelper
        ): SvgGElement {
            val g = SvgGElement()
            val elementHelper = geomHelper.createSvgElementHelper()
            elementHelper.setStrokeAlphaEnabled(true)
            segments.forEach { segment ->
                g.children().add(
                    elementHelper.createLine(segment.start, segment.end, p)!!.first
                )
            }
            return g
        }

        const val HANDLES_GROUPS = false
    }
}
