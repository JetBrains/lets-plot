/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.component

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipDefaults
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.Orientation.HORIZONTAL
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.Orientation.VERTICAL
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.PointerDirection
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.PointerDirection.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.math.min

internal class TailDecoration : SvgComponent() {
    var pointerDirection: PointerDirection? = null
        private set
    private val pointerPath = SvgPathElement()
    private var borderRadius = 0.0

    override fun buildComponent() {
        add(pointerPath)
    }

    fun updateStyle(
        fillColor: Color,
        borderColor: Color,
        strokeWidth: Double,
        lineType: LineType,
        borderRadius: Double
    ) {
        this@TailDecoration.borderRadius = borderRadius

        pointerPath.apply {
            strokeColor().set(borderColor)
            strokeWidth().set(strokeWidth)
            fillColor().set(fillColor)
            StrokeDashArraySupport.apply(this, strokeWidth, lineType)
        }
    }

    fun update(
        pointerCoord: DoubleVector,
        contentRect: DoubleRectangle,
        orientation: TooltipBox.Orientation,
        showTail: Boolean
    ) {
        pointerDirection = if (!showTail) null else when (orientation) {
            HORIZONTAL -> when {
                pointerCoord.x < contentRect.left -> LEFT
                pointerCoord.x > contentRect.right -> RIGHT
                else -> null
            }

            VERTICAL -> when {
                pointerCoord.y > contentRect.bottom -> DOWN
                pointerCoord.y < contentRect.top -> UP
                else -> null
            }
        }

        val vertFootingIndent = -calculatePointerFootingIndent(contentRect.height)
        val horFootingIndent = calculatePointerFootingIndent(contentRect.width)

        pointerPath.d().set(
            SvgPathDataBuilder().apply {
                with(contentRect) {

                    fun lineToIf(p: DoubleVector, isTrue: Boolean) {
                        if (isTrue) lineTo(p)
                    }

                    fun corner(controlStart: DoubleVector, controlEnd: DoubleVector, to: DoubleVector) {
                        // todo parameters: (x, y, radiusX, radiusY)
                        lineTo(controlStart)
                        if (controlStart != to) curveTo(controlStart, controlEnd, to)
                    }

                    // start point
                    moveTo(right - borderRadius, bottom)

                    // right-bottom
                    corner(
                        DoubleVector(right - borderRadius, bottom),
                        DoubleVector(right, bottom),
                        DoubleVector(right, bottom - borderRadius)
                    )

                    // right side
                    lineTo(right, bottom + vertFootingIndent)
                    lineToIf(pointerCoord, pointerDirection == RIGHT)
                    lineTo(right, top - vertFootingIndent)

                    // right-top corner
                    corner(
                        DoubleVector(right, top + borderRadius),
                        DoubleVector(right, top),
                        DoubleVector(right - borderRadius, top)
                    )

                    // top side
                    lineTo(right - horFootingIndent, top)
                    lineToIf(pointerCoord, pointerDirection == UP)
                    lineTo(left + horFootingIndent, top)

                    // left-top corner
                    corner(
                        DoubleVector(left + borderRadius, top),
                        DoubleVector(left, top),
                        DoubleVector(left, top + borderRadius)
                    )

                    // left side
                    lineTo(left, top - vertFootingIndent)
                    lineToIf(pointerCoord, pointerDirection == LEFT)
                    lineTo(left, bottom + vertFootingIndent)

                    // left-bottom corner
                    corner(
                        DoubleVector(left, bottom - borderRadius),
                        DoubleVector(left, bottom),
                        DoubleVector(left + borderRadius, bottom)
                    )

                    // bottom side
                    lineTo(left + horFootingIndent, bottom)
                    lineToIf(pointerCoord, pointerDirection == DOWN)
                    lineTo(right - horFootingIndent, bottom)
                    lineTo(right - borderRadius, bottom)
                }
                }.build()
            )
    }

    private fun calculatePointerFootingIndent(sideLength: Double): Double {
        val footingLength = min(
            sideLength * TooltipDefaults.POINTER_FOOTING_TO_SIDE_LENGTH_RATIO,
            TooltipDefaults.MAX_POINTER_FOOTING_LENGTH
        )
        return (sideLength - footingLength) / 2
    }
}
