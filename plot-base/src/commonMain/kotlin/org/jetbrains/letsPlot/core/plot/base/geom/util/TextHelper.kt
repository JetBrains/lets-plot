/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.GeometryUtils
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom.Companion.BASELINE_TEXT_WIDTH
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

class TextHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val labelFactory: (DataPointAesthetics, DoubleVector, String, Double, GeomContext, DoubleVector?) -> SvgElement
) : GeomHelper(pos, coord, ctx) {
    fun createTexts(
        formatter: ((Any) -> String)?,
        naValue: String,
        sizeUnit: String?,
        checkOverlap: Boolean,
        handler: (DataPointAesthetics, SvgNode, List<DoubleVector>) -> Unit
    ) {
        val restrictions = mutableListOf<List<DoubleVector>>()
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center
        myAesthetics.dataPoints().forEach { p ->
            val text = toString(p.label(), naValue, formatter)
            if (text.isEmpty()) return@forEach
            val point = p.finiteVectorOrNull(Aes.X, Aes.Y) ?: return@forEach
            val loc = toClient(point, p) ?: return@forEach

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, BASELINE_TEXT_WIDTH)

            val rectangle = getRect(p, loc, text, sizeUnitRatio, ctx, aesBoundsCenter)
            if (checkOverlap) {
                if (restrictions.any { GeometryUtils.arePolygonsIntersected(rectangle, it) }) {
                    return@forEach
                }
                restrictions.add(rectangle)
            }

            labelFactory(p, loc, text, sizeUnitRatio, ctx, aesBoundsCenter).let { svgElement ->
                handler(p, svgElement, rectangle)
            }
        }
    }

    private fun objectRectangle(
        location: DoubleVector,
        textSize: DoubleVector,
        fontSize: Double,
        hAnchor: Text.HorizontalAnchor,
        vAnchor: Text.VerticalAnchor,
    ) = TextUtil.rectangleForText(location, textSize, padding = 0.0, hAnchor, vAnchor)

    private fun getRect(
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
        val angle = toRadians(TextUtil.angle(p))

        return objectRectangle(location, textSize, fontSize, hAnchor, vAnchor)
            .rotate(angle, location)
    }

    private fun toString(label: Any?, naValue: String, formatter: ((Any) -> String)?): String {
        if (label == null) return naValue

        val formatter = formatter ?: ctx.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }
}