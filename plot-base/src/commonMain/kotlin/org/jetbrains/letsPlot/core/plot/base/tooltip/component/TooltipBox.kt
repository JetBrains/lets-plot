/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.component

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipDefaults
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipMarker
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipModel
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet

class TooltipBox(
    styleSheet: StyleSheet
) : SvgComponent() {
    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    internal enum class PointerDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    enum class TargetIndicatorShape {
        TAIL,    // attached callout tail from the box to the target
        POINTER, // detached triangle at the target (rotated tooltips)
        CIRCLE,  // circular markers at all targets (multi-target tooltips)
        NONE     // no target indicator
    }

    val contentRect
        get() = DoubleRectangle.span(
            DoubleVector.ZERO,
            contentBox.dimension
        )

    // The tail decoration draws the box body itself, so it is always present.
    private val tailDecoration = TailDecoration()
    private val contentBox = ContentBox(styleSheet)

    // Optional decorations are created on demand and kept across reuse (see SvgComponentPool).
    private var circleDecoration: CircleDecoration? = null
    private var pointerDecoration: PointerDecoration? = null

    // Styling captured in update(), applied to optional decorations only when they are shown.
    private var circleIndicators: List<CircleDecoration.CircleIndicator> = emptyList()
    private var pointerStyle: PointerDecoration.Style? = null

    internal val pointerDirection get() = tailDecoration.pointerDirection // for tests

    override fun buildComponent() {
        add(tailDecoration)
        add(contentBox)
    }

    fun update(
        fillColor: Color,
        textColor: Color?,
        borderColor: Color,
        strokeWidth: Double,
        lineType: LineType,
        targets: List<TooltipModel.Target>,
        title: String?,
        textClassName: String,
        tooltipMinWidth: Double? = null,
        borderRadius: Double,
        pointMarkerStrokeColor: Color = borderColor
    ) {
        contentBox.update(
            targets,
            title,
            textColor,
            tooltipMinWidth,
            textClassName
        )
        tailDecoration.updateStyle(
            fillColor = fillColor,
            borderColor = borderColor,
            strokeWidth = strokeWidth,
            lineType = lineType,
            borderRadius = borderRadius
        )
        circleIndicators = targets.map { target ->
            CircleDecoration.CircleIndicator(
                coord = target.coord,
                fillColor = target.marker.pointMarkerFillColor(),
                strokeColor = pointMarkerStrokeColor
            )
        }
        pointerStyle = PointerDecoration.Style(
            fillColor = fillColor,
            borderColor = borderColor,
            strokeColor = pointMarkerStrokeColor
        )
    }

    fun setPosition(
        tooltipCoord: DoubleVector,
        pointerCoord: DoubleVector,
        orientation: Orientation,
        targetIndicatorShape: TargetIndicatorShape = TargetIndicatorShape.TAIL
    ) {
        // The POINTER is the only rotated indicator, so it drives the rotation angle.
        val rotate = targetIndicatorShape == TargetIndicatorShape.POINTER
        val rotationAngle = if (rotate) TooltipDefaults.ROTATION_ANGLE else 0.0
        rotate(rotationAngle)

        val p = pointerCoord
            .subtract(tooltipCoord)
            .rotate(toRadians(-rotationAngle))   // cancel rotation for pointer point coordinates

        tailDecoration.update(
            pointerCoord = p,
            contentRect = contentRect,
            orientation = orientation,
            showTail = targetIndicatorShape == TargetIndicatorShape.TAIL
        )
        updateCircleDecoration(tooltipCoord, visible = targetIndicatorShape == TargetIndicatorShape.CIRCLE)
        updatePointerDecoration(p, visible = targetIndicatorShape == TargetIndicatorShape.POINTER)
        moveTo(tooltipCoord)

        if (DEBUG_DRAWING) {
            contentBox.drawDebugRect()
        }
    }

    private fun updateCircleDecoration(tooltipCoord: DoubleVector, visible: Boolean) {
        // Don't instantiate the decoration unless this tooltip actually shows markers.
        val decoration = circleDecoration ?: if (visible) {
            CircleDecoration().also { circleDecoration = it; add(it) }
        } else {
            return
        }
        if (visible) {
            decoration.updateStyle(circleIndicators)
        }
        decoration.update(tooltipCoord, visible)
    }

    private fun updatePointerDecoration(pointerCoord: DoubleVector, visible: Boolean) {
        // Don't instantiate the decoration unless this tooltip actually shows a pointer.
        val decoration = pointerDecoration ?: if (visible) {
            PointerDecoration().also { pointerDecoration = it; add(it) }
        } else {
            return
        }
        if (visible) {
            pointerStyle?.let(decoration::updateStyle)
        }
        decoration.update(pointerCoord, visible)
    }

    private fun TooltipMarker.pointMarkerFillColor(): Color {
        return majorColor.takeIf { it.isVisible() }
            ?: minorColor.takeIf { it.isVisible() }
            ?: Color.BLACK
    }

    private fun Color?.isVisible(): Boolean {
        return this != null && alpha != 0
    }

    companion object {
        private const val DEBUG_DRAWING = false
    }
}
