/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.GeometryUtils
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom.Companion.BASELINE_TEXT_WIDTH
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

class TextHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val formatter: ((Any) -> String)?,
    private val naValue: String,
    private val sizeUnit: String?,
    private val checkOverlap: Boolean,
    private val flipAngle: Boolean,
    private val coordOrNull: (DataPointAesthetics) -> DoubleVector?,
    private val objectRectangle: (DoubleVector, DoubleVector, Double, Text.HorizontalAnchor, Double) -> DoubleRectangle,
    private val componentFactory: (DataPointAesthetics, DoubleVector, String, Boolean, Double, GeomContext, DoubleVector?) -> SvgGElement
) : GeomHelper(pos, coord, ctx) {

    internal fun createSvgComponents(): List<SvgGElement> {
        val restrictions = mutableListOf<List<DoubleVector>>()
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center
        return myAesthetics.dataPoints().mapNotNull { p ->
            val text = toString(p.label())
            if (text.isEmpty()) return@mapNotNull null
            val point = coordOrNull(p) ?: return@mapNotNull null
            val location = toClient(point, p) ?: return@mapNotNull null

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, BASELINE_TEXT_WIDTH)

            val rectangle = getRect(p, location, text, sizeUnitRatio, ctx, aesBoundsCenter)
            if (checkOverlap) {
                if (restrictions.any { GeometryUtils.arePolygonsIntersected(rectangle, it) }) {
                    return@mapNotNull null
                }
                restrictions.add(rectangle)
            }

            componentFactory(p, location, text, flipAngle, sizeUnitRatio, ctx, aesBoundsCenter)
        }
    }

    internal fun buildHints(targetCollector: GeomTargetCollector) {
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TEXT, this.ctx)

        myAesthetics.dataPoints().forEach { p ->
            val point = coordOrNull(p) ?: return
            val location = toClient(point, p) ?: return
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, BASELINE_TEXT_WIDTH)
            targetCollector.addPoint(
                p.index(),
                location,
                sizeUnitRatio * AesScaling.textSize(p) / 2,
                GeomTargetCollector.TooltipParams(
                    markerColors = colorsByDataPoint(p)
                ),
                TipLayoutHint.Kind.CURSOR_TOOLTIP
            )
        }
    }

    internal fun toString(label: Any?): String {
        if (label == null) return naValue

        val formatter = formatter ?: ctx.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    internal fun getRect(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): List<DoubleVector> {
        val textSize = TextUtil.measure(text, p, ctx, sizeUnitRatio)
        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        val vAnchor = TextUtil.vAnchor(p, location, boundsCenter)
        val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
        val angle = toRadians(TextUtil.orientedAngle(p, flipAngle, ctx))

        return objectRectangle(location, textSize, fontSize, hAnchor, vAnchor)
            .rotate(angle, location)
    }
}