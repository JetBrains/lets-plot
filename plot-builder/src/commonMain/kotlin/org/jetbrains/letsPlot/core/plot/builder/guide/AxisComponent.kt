/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor.*
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor.*
import org.jetbrains.letsPlot.core.plot.base.render.svg.TextLabel
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.AxisUtil.tickLabelBaseOffset
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils.transformTranslate

class AxisComponent(
    private val length: Double,
    private val orientation: Orientation,
    private val breaksData: BreaksData,
    private val labelAdjustments: TickLabelAdjustments = TickLabelAdjustments(orientation),
    private val axisTheme: AxisTheme,
    private val hideAxis: Boolean = false,
    private val hideAxisBreaks: Boolean = false,
) : SvgComponent() {

    override fun buildComponent() {
        buildAxis()
    }

    private fun buildAxis() {
        val rootElement = rootGroup
        val start = 0.0
        val end: Double = length

        val x1: Double = if (orientation.isHorizontal) start else 0.0
        val x2: Double = if (orientation.isHorizontal) end else 0.0
        val y1: Double = if (!orientation.isHorizontal) start else 0.0
        val y2: Double = if (!orientation.isHorizontal) end else 0.0

        // Axis
        if (!hideAxis) {
            // Ticks and labels
            if (!hideAxisBreaks && (axisTheme.showLabels() || axisTheme.showTickMarks())) {
                val tickLabelBaseOffset = tickLabelBaseOffset(axisTheme, orientation)

                for ((i, br) in breaksData.majorBreaks.withIndex()) {
                    val loc = when (orientation.isHorizontal) {
                        true -> br.x
                        false -> br.y
                    }
                    if (loc in start..end) {
                        val label = breaksData.majorLabels[i % breaksData.majorLabels.size]
                        val labelOffset = tickLabelBaseOffset.add(labelAdjustments.additionalOffset(i))
                        val group = buildTick(label, labelOffset, axisTheme)

                        when (orientation.isHorizontal) {
                            false -> transformTranslate(group, 0.0, loc)
                            true -> transformTranslate(group, loc, 0.0)
                        }

                        rootElement.children().add(group)
                    }
                }
            }

            // Axis line
            if (!hideAxisBreaks && axisTheme.showLine()) {
                val axisLine = SvgLineElement(x1, y1, x2, y2).apply {
                    strokeWidth().set(axisTheme.lineWidth())
                    strokeColor().set(axisTheme.lineColor())
                    StrokeDashArraySupport.apply(this, axisTheme.lineWidth(), axisTheme.lineType())
                }
                rootElement.children().add(axisLine)
            }
        }
    }

    private fun buildTick(
        label: String,
        labelOffset: DoubleVector,
        axisTheme: AxisTheme
    ): SvgGElement {

        var tickMark: SvgLineElement? = null
        if (axisTheme.showTickMarks()) {
            tickMark = SvgLineElement()
            tickMark.strokeWidth().set(axisTheme.tickMarkWidth())
            tickMark.strokeColor().set(axisTheme.tickMarkColor())
            StrokeDashArraySupport.apply(tickMark, axisTheme.tickMarkWidth(), axisTheme.tickMarkLineType())
        }

        var tickLabel: TextLabel? = null
        if (axisTheme.showLabels()) {
            tickLabel = TextLabel(label)
            tickLabel.addClassName("${Style.AXIS_TEXT}-${axisTheme.axis}")
        }

        val markLength = axisTheme.tickMarkLength()
        when (orientation) {
            Orientation.LEFT -> {
                if (tickMark != null) {
                    tickMark.x2().set(-markLength)
                    tickMark.y2().set(0.0)
                }
            }

            Orientation.RIGHT -> {
                if (tickMark != null) {
                    tickMark.x2().set(markLength)
                    tickMark.y2().set(0.0)
                }
            }

            Orientation.TOP -> {
                if (tickMark != null) {
                    tickMark.x2().set(0.0)
                    tickMark.y2().set(-markLength)
                }
            }

            Orientation.BOTTOM -> {
                if (tickMark != null) {
                    tickMark.x2().set(0.0)
                    tickMark.y2().set(markLength)
                }
            }
        }

        val g = SvgGElement()
        if (tickMark != null) {
            g.children().add(tickMark)
        }

        if (tickLabel != null) {
            tickLabel.moveTo(labelOffset.x, labelOffset.y)
            tickLabel.setHorizontalAnchor(labelAdjustments.horizontalAnchor)
            tickLabel.setVerticalAnchor(labelAdjustments.verticalAnchor)
            tickLabel.rotate(labelAdjustments.rotationDegree)
            g.children().add(tickLabel.rootGroup)
        }
        return g
    }

    class BreaksData(
        val majorBreaks: List<DoubleVector>,
        val majorLabels: List<String>,
        val minorBreaks: List<DoubleVector>,
        val majorGrid: List<List<DoubleVector>>,
        val minorGrid: List<List<DoubleVector>>,
    )

    class TickLabelAdjustments(
        orientation: Orientation,
        horizontalAnchor: Text.HorizontalAnchor? = null,
        verticalAnchor: Text.VerticalAnchor? = null,
        val rotationDegree: Double = 0.0,
        private val additionalOffsets: List<DoubleVector>? = null
    ) {
        val horizontalAnchor: Text.HorizontalAnchor = horizontalAnchor ?: when (orientation) {
            Orientation.LEFT -> RIGHT
            Orientation.RIGHT -> LEFT
            Orientation.TOP, Orientation.BOTTOM -> MIDDLE
        }
        val verticalAnchor: Text.VerticalAnchor = verticalAnchor ?: when (orientation) {
            Orientation.LEFT, Orientation.RIGHT -> CENTER
            Orientation.TOP -> BOTTOM
            Orientation.BOTTOM -> TOP
        }

        fun additionalOffset(tickIndex: Int): DoubleVector {
            return additionalOffsets?.get(tickIndex) ?: DoubleVector.ZERO
        }
    }

}

