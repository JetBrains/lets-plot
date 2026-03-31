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
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil.DEF_LABEL_NUDGE
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

class TextHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext
) : GeomHelper(pos, coord, ctx) {

    private var labelOptions: LabelOptions? = null
    private var formatter: ((Any) -> String)? = null
    private var naValue: String = DEF_NA_VALUE
    private var sizeUnit: String? = null
    private var checkOverlap: Boolean = false
    private var toLocation: (DataPointAesthetics) -> DoubleVector? = DEF_COORD_OR_NULL

    fun setLabelOptions(labelOptions: LabelOptions): TextHelper {
        this.labelOptions = labelOptions
        return this
    }

    fun setFormatter(formatter: ((Any) -> String)?): TextHelper {
        this.formatter = formatter
        return this
    }

    fun setNaValue(naValue: String): TextHelper {
        this.naValue = naValue
        return this
    }

    fun setCheckOverlap(checkOverlap: Boolean): TextHelper {
        this.checkOverlap = checkOverlap
        return this
    }

    fun setSizeUnit(sizeUnit: String?): TextHelper {
        this.sizeUnit = sizeUnit
        return this
    }

    fun toLocation(toLocation: (DataPointAesthetics) -> DoubleVector?): TextHelper {
        this.toLocation = toLocation
        return this
    }

    internal fun createSvgComponents(
        flipAngle: Boolean = false,
        labelNudge: (DoubleVector, DoubleVector) -> DoubleVector = TextUtil.DEF_LABEL_NUDGE
    ): List<SvgGElement> {
        val restrictions = mutableListOf<List<DoubleVector>>()
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center
        return myAesthetics.dataPoints().mapNotNull { p ->
            val text = toString(p.label())
            if (text.isEmpty()) return@mapNotNull null
            val point = toLocation(p) ?: return@mapNotNull null
            val location = toClient(point, p) ?: return@mapNotNull null

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, BASELINE_TEXT_WIDTH)

            val rectangle = getRect(p, location, text, ctx, flipAngle, sizeUnitRatio, aesBoundsCenter)
            if (checkOverlap) {
                if (restrictions.any { GeometryUtils.arePolygonsIntersected(rectangle, it) }) {
                    return@mapNotNull null
                }
                restrictions.add(rectangle)
            }

            componentFactory(p, location, text, flipAngle, sizeUnitRatio, aesBoundsCenter, labelNudge)
        }
    }

    internal fun componentFactory(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        flipAngle: Boolean = false,
        sizeUnitRatio: Double = 1.0,
        boundsCenter: DoubleVector? = null,
        labelNudge: (location: DoubleVector, size: DoubleVector) -> DoubleVector = DEF_LABEL_NUDGE
    ): SvgGElement {
        return if (labelOptions == null) {
            TextUtil.textComponentFactory(p, location, text, ctx, flipAngle, sizeUnitRatio, boundsCenter, labelNudge)
        } else {
            TextUtil.labelComponentFactory(p, location, text, ctx, labelOptions!!, flipAngle, sizeUnitRatio, boundsCenter, labelNudge)
        }
    }

    internal fun buildHints(targetCollector: GeomTargetCollector) {
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(this.ctx)

        myAesthetics.dataPoints().forEach { p ->
            val point = toLocation(p) ?: return
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

    internal fun toString(
        label: Any?
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

        val rectangle = if (labelOptions == null) {
            TextUtil.rectangleForText(location, textSize, padding = 0.0, hAnchor, vAnchor)
        } else {
            TextUtil.rectangleForText(location, textSize, padding = fontSize * labelOptions!!.paddingFactor, hAnchor, vAnchor)
        }
        return rectangle.rotate(angle, location)
    }

    companion object {
        const val DEF_NA_VALUE = "n/a"
        val DEF_COORD_OR_NULL: (DataPointAesthetics) -> DoubleVector? = { it.finiteVectorOrNull(Aes.X, Aes.Y) }
    }
}