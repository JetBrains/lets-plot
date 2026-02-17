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
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom.Companion.BASELINE_TEXT_WIDTH
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

class TextHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext
) : GeomHelper(pos, coord, ctx) {

    private var myLabelOptions: LabelOptions? = null
    private var mySizeUnit: String? = null
    private var myCoordOrNull: (DataPointAesthetics) -> DoubleVector? = DEF_COORD_OR_NULL

    fun setLabelOptions(labelOptions: LabelOptions) {
        this.myLabelOptions = labelOptions
    }

    fun setSizeUnit(sizeUnit: String) {
        this.mySizeUnit = sizeUnit
    }

    fun setCoordOrNull(f: (DataPointAesthetics) -> DoubleVector?) {
        this.myCoordOrNull = f
    }

    internal fun createSvgComponents(
        formatter: ((Any) -> String)? = null,
        naValue: String = TextGeom.DEF_NA_VALUE,
        checkOverlap: Boolean = false,
        flipAngle: Boolean = false,
        labelNudge: (DoubleVector, DoubleVector) -> DoubleVector = TextUtil.DEF_NUDGE
    ): List<SvgGElement> {
        val restrictions = mutableListOf<List<DoubleVector>>()
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center
        return myAesthetics.dataPoints().mapNotNull { p ->
            val text = toString(p.label(), formatter, naValue)
            if (text.isEmpty()) return@mapNotNull null
            val point = myCoordOrNull(p) ?: return@mapNotNull null
            val location = toClient(point, p) ?: return@mapNotNull null

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, mySizeUnit, BASELINE_TEXT_WIDTH)

            val rectangle = getRect(p, location, text, ctx, flipAngle, sizeUnitRatio, aesBoundsCenter)
            if (checkOverlap) {
                if (restrictions.any { GeometryUtils.arePolygonsIntersected(rectangle, it) }) {
                    return@mapNotNull null
                }
                restrictions.add(rectangle)
            }

            if (myLabelOptions == null) {
                TextUtil.textComponentFactory(p, location, text, ctx, flipAngle, sizeUnitRatio, aesBoundsCenter, labelNudge)
            } else {
                TextUtil.labelComponentFactory(p, location, text, ctx, myLabelOptions!!, flipAngle, sizeUnitRatio, aesBoundsCenter, labelNudge)
            }
        }
    }

    internal fun buildHints(targetCollector: GeomTargetCollector) {
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TEXT, this.ctx)

        myAesthetics.dataPoints().forEach { p ->
            val point = myCoordOrNull(p) ?: return
            val location = toClient(point, p) ?: return
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, mySizeUnit, BASELINE_TEXT_WIDTH)
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

    internal fun toString(
        label: Any?,
        formatter: ((Any) -> String)? = null,
        naValue: String = TextGeom.DEF_NA_VALUE
    ): String {
        if (label == null) return naValue

        val formatter = formatter ?: ctx.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    internal fun getRect(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        ctx: GeomContext,
        flipAngle: Boolean = false,
        sizeUnitRatio: Double = 1.0,
        boundsCenter: DoubleVector? = null
    ): List<DoubleVector> {
        val textSize = TextUtil.measure(text, p, ctx, sizeUnitRatio)
        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        val vAnchor = TextUtil.vAnchor(p, location, boundsCenter)
        val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
        val angle = toRadians(TextUtil.orientedAngle(p, flipAngle, ctx))

        val rectangle = if (myLabelOptions == null) {
            TextUtil.rectangleForText(location, textSize, padding = 0.0, hAnchor, vAnchor)
        } else {
            TextUtil.rectangleForText(location, textSize, padding = fontSize * myLabelOptions!!.paddingFactor, hAnchor, vAnchor)
        }
        return rectangle.rotate(angle, location)
    }

    companion object {
        val DEF_COORD_OR_NULL: (DataPointAesthetics) -> DoubleVector? = { it.finiteVectorOrNull(Aes.X, Aes.Y) }
    }
}