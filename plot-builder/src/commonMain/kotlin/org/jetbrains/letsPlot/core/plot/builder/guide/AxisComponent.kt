/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor.*
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor.*
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.AxisUtil.tickLabelBaseOffset
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
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
        if (!hideAxis && !hideAxisBreaks)
            buildAxis()
    }

    private data class TickStyle(
        val width: Double,
        val color: Color,
        val lineType: LineType,
        val length: Double,
        val showTickMark: Boolean,
        val showLabel: Boolean,
    )

    private fun majorTickStyle() = TickStyle(
        width = axisTheme.tickMarkWidth(),
        color = axisTheme.tickMarkColor(),
        lineType = axisTheme.tickMarkLineType(),
        length = axisTheme.tickMarkLength(),
        showTickMark = axisTheme.showTickMarks(),
        showLabel = axisTheme.showLabels()
    )

    private fun minorTickStyle() = TickStyle(
        width = axisTheme.minorTickMarkWidth(),
        color = axisTheme.minorTickMarkColor(),
        lineType = axisTheme.minorTickMarkLineType(),
        length = axisTheme.minorTickMarkLength(),
        showTickMark = axisTheme.showMinorTickMarks(),
        showLabel = true
    )

    private data class TickData(
        val breaks: List<DoubleVector>,
        val labels: List<String?>,
        val indices: List<Int>,
        val style: TickStyle
    )

    private fun buildAxis() {
        val rootElement = rootGroup
        val start = 0.0
        val end: Double = length

        // Ticks and labels
        val tickLabelBaseOffset = tickLabelBaseOffset(axisTheme, orientation)

        if (axisTheme.showLabels() || axisTheme.showTickMarks()) {
            val majorTicks = TickData(
                breaks = breaksData.majorBreaks,
                labels = breaksData.majorLabels,
                indices = breaksData.majorIndices,
                style = majorTickStyle()
            )
            addTicks(majorTicks, tickLabelBaseOffset)
        }

        if (axisTheme.showMinorTickMarks()) {
            val minorTicks = TickData(
                breaks = breaksData.minorBreaks,
                labels = List(breaksData.minorBreaks.size) { null }, // no labels for minor ticks for now
                indices = List(breaksData.minorBreaks.size) { it },
                style = minorTickStyle()
            )
            addTicks(minorTicks, tickLabelBaseOffset)
        }

        // Axis line
        if (axisTheme.showLine()) {
            val x1: Double = if (orientation.isHorizontal) start else 0.0
            val x2: Double = if (orientation.isHorizontal) end else 0.0
            val y1: Double = if (!orientation.isHorizontal) start else 0.0
            val y2: Double = if (!orientation.isHorizontal) end else 0.0

            val axisLine = SvgLineElement(x1, y1, x2, y2).apply {
                strokeWidth().set(axisTheme.lineWidth())
                strokeColor().set(axisTheme.lineColor())
                StrokeDashArraySupport.apply(this, axisTheme.lineWidth(), axisTheme.lineType())
            }
            rootElement.children().add(axisLine)
        }
    }

    private fun addTicks(ticks: TickData, tickLabelBaseOffset: DoubleVector) {
        for (i in ticks.breaks.indices) {
            val br = ticks.breaks[i]
            val loc = if (orientation.isHorizontal) br.x else br.y
            if (loc !in 0.0..length) continue

            // label may be null
            val label = ticks.labels[i]
            val idx = ticks.indices[i]

            // ToDo: minor ticks should have their own label offset logic
            val labelOffset = if (label != null)
                tickLabelBaseOffset.add(labelAdjustments.additionalOffset(idx))
            else null

            val g = SvgGElement()

            if (ticks.style.showTickMark)
                g.children().add(buildTickMark(ticks.style))

            if (ticks.style.showLabel && label != null && labelOffset != null)
                g.children().add(buildTickLabel(label, labelOffset))

            if (orientation.isHorizontal)
                transformTranslate(g, loc, 0.0)
            else
                transformTranslate(g, 0.0, loc)

            rootGroup.children().add(g)
        }
    }

    private fun buildTickMark(style: TickStyle): SvgLineElement {
        return SvgLineElement().apply {
            strokeWidth().set(style.width)
            strokeColor().set(style.color)
            StrokeDashArraySupport.apply(this, style.width, style.lineType)

            when (orientation) {
                Orientation.LEFT ->   { x2().set(-style.length); y2().set(0.0) }
                Orientation.RIGHT ->  { x2().set( style.length); y2().set(0.0) }
                Orientation.TOP ->    { x2().set(0.0); y2().set(-style.length) }
                Orientation.BOTTOM -> { x2().set(0.0); y2().set( style.length) }
            }
        }
    }

    private fun buildTickLabel(
        label: String,
        labelOffset: DoubleVector
    ): SvgGElement {

        val tickLabel = Label(label)
        tickLabel.addClassName("${Style.AXIS_TEXT}-${axisTheme.axis}")

        tickLabel.moveTo(labelOffset.x, labelOffset.y)
        tickLabel.setHorizontalAnchor(labelAdjustments.horizontalAnchor)
        tickLabel.setVerticalAnchor(labelAdjustments.verticalAnchor)

        val tickHeight = PlotLabelSpecFactory.axisTick(axisTheme).height()
        tickLabel.setFontSize(tickHeight)
        tickLabel.setLineHeight(tickHeight)
        tickLabel.rotate(labelAdjustments.rotationDegree)

        return tickLabel.rootGroup
    }

    class BreaksData(
        val majorBreaks: List<DoubleVector>,
        val majorIndices: List<Int>,
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
        val bounds: List<DoubleRectangle>? = null,
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

