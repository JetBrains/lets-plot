/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

open class TextGeom : GeomBase() {
    var formatter: ((Any) -> String)? = null
    var naValue = DEF_NA_VALUE
    var sizeUnit: String? = null
    var checkOverlap: Boolean = false

    private val myRestrictions = mutableListOf<List<DoubleVector>>()

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TEXT, ctx)
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center

        for (dp in aesthetics.dataPoints()) {
            val text = toString(dp.label(), ctx)
            if (text.isEmpty()) continue
            val point = dp.finiteOrNull(Aes.X, Aes.Y)?.let { DoubleVector(it) } ?: continue
            val loc = helper.toClient(point, dp) ?: continue

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            val sizeUnitRatio = AesScaling.sizeUnitRatio(dp, coord, sizeUnit, BASELINE_TEXT_WIDTH)

            if (checkOverlap && hasOverlaps(dp, loc, text, sizeUnitRatio, ctx, aesBoundsCenter)) {
                continue
            }

            val tc = buildTextComponent(dp, loc, text, sizeUnitRatio, ctx, aesBoundsCenter)
            root.add(tc)

            // The geom_text tooltip is similar to the geom_tile:
            // it looks better when the text is on a tile in corr_plot (but the color will be different from the geom_tile tooltip)
            targetCollector.addPoint(
                dp.index(),
                loc,
                sizeUnitRatio * AesScaling.textSize(dp) / 2,
                GeomTargetCollector.TooltipParams(
                    markerColors = colorsByDataPoint(dp)
                ),
                TipLayoutHint.Kind.CURSOR_TOOLTIP
            )
        }
    }

    private fun hasOverlaps(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): Boolean {
        val textSize = TextUtil.measure(text, p, ctx, sizeUnitRatio)
        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        val vAnchor = TextUtil.vAnchor(p, location, boundsCenter)

        val rectangle = objectRectangle(location, textSize, TextUtil.fontSize(p, sizeUnitRatio), hAnchor, vAnchor)
            .rotate(toRadians(TextUtil.angle(p)), location)

        if (myRestrictions.any { arePolygonsIntersected(rectangle, it) }) {
            return true
        }
        myRestrictions.add(rectangle)

        return false
    }

    open fun buildTextComponent(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): SvgGElement {
        val label = MultilineLabel(text)
        TextUtil.decorate(label, p, sizeUnitRatio, applyAlpha = true)
        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        label.setHorizontalAnchor(hAnchor)

        val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
        val textHeight = TextUtil.measure(text, p, ctx, sizeUnitRatio).y
        //val textHeight = TextHelper.lineheight(p, sizeUnitRatio) * (label.linesCount() - 1) + fontSize

        val yPosition = when (TextUtil.vAnchor(p, location, boundsCenter)) {
            Text.VerticalAnchor.TOP -> location.y + fontSize * 0.7
            Text.VerticalAnchor.BOTTOM -> location.y - textHeight + fontSize
            Text.VerticalAnchor.CENTER -> location.y - textHeight / 2 + fontSize * 0.8
        }

        val textLocation = DoubleVector(location.x, yPosition)
        label.moveTo(textLocation)

        val g = SvgGElement()
        g.children().add(label.rootGroup)
        SvgUtils.transformRotate(g, TextUtil.angle(p), location.x, location.y)
        return g
    }

    open fun objectRectangle(
        location: DoubleVector,
        textSize: DoubleVector,
        fontSize: Double,
        hAnchor: Text.HorizontalAnchor,
        vAnchor: Text.VerticalAnchor,
    ) = TextUtil.rectangleForText(location, textSize, padding = 0.0, hAnchor, vAnchor)

    private fun toString(label: Any?, geomContext: GeomContext): String {
        if (label == null) return naValue

        val formatter = formatter ?: geomContext.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    companion object {
        const val DEF_NA_VALUE = "n/a"
        const val HANDLES_GROUPS = false

        // Current implementation works for label_format ='.2f'
        // and values between -1.0 and 1.0.
        private const val BASELINE_TEXT_WIDTH = 6.0

        private fun DoubleRectangle.rotate(angle: Double, around: DoubleVector): List<DoubleVector> {
            val lt = origin.rotateAround(around, angle)
            val lb = DoubleVector(left, bottom).rotateAround(around, angle)
            val rt = DoubleVector(right, top).rotateAround(around, angle)
            val rb = DoubleVector(right, bottom).rotateAround(around, angle)
            return listOf(lt, lb, rb, rt)
        }

        private fun arePolygonsIntersected(pg1: List<DoubleVector>, pg2: List<DoubleVector>): Boolean {
            fun projectPolygon(axis: DoubleVector, polygon: List<DoubleVector>): Pair<Double, Double> {
                val dots = polygon.map { it.dotProduct(axis) }
                return dots.min() to dots.max()
            }

            val edges = listOf(pg1, pg2).flatMap { polygon ->
                polygon.indices.map { i ->
                    polygon[i].subtract(polygon[(i + 1) % polygon.size])
                        .orthogonal().normalize()
                }
            }

            return edges.none { axis ->
                val (min1, max1) = projectPolygon(axis, pg1)
                val (min2, max2) = projectPolygon(axis, pg2)
                max1 < min2 || max2 < min1
            }
        }
    }
}

// How 'just' and 'angle' works together
// https://stackoverflow.com/questions/7263849/what-do-hjust-and-vjust-do-when-making-a-plot-using-ggplot