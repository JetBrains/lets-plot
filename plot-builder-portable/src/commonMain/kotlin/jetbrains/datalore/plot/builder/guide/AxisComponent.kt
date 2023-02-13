/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.Text.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.Text.VerticalAnchor.*
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.layout.PlotLabelSpecFactory
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.PanelGridTheme
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgUtils.transformTranslate
import kotlin.math.abs

class AxisComponent(
    private val length: Double,
    private val orientation: Orientation,
    private val breaksData: BreaksData,
    private val labelAdjustments: TickLabelAdjustments = TickLabelAdjustments(orientation),
    private val gridLineLength: Double,
    private val gridLineDistance: Double,
    private val axisTheme: AxisTheme,
    private val gridTheme: PanelGridTheme,
    private val hideAxis: Boolean = false,
    private val hideAxisBreaks: Boolean = false,
    private val hideGridlines: Boolean = false
) : SvgComponent() {

    override fun buildComponent() {
        buildAxis()
    }

    private fun buildAxis() {
        val rootElement = rootGroup

        val x1: Double
        val y1: Double
        val x2: Double
        val y2: Double
        val start = 0.0
        val end: Double = length
        when (orientation) {
            Orientation.LEFT, Orientation.RIGHT -> {
                x1 = 0.0
                x2 = 0.0
                y1 = start
                y2 = end
            }
            Orientation.TOP, Orientation.BOTTOM -> {
                x1 = start
                x2 = end
                y1 = 0.0
                y2 = 0.0
            }
        }

        // Grid lines.
        if (!hideGridlines) {
            // Minor grid.
            // do not draw grid lines then it's too close to axis ends.
            val gridLineMinPos = start + 6
            val gridLineMaxPos = end - 6

            if (gridTheme.showMinor()) {
                for (br in breaksData.minorBreaks) {
                    if (br >= gridLineMinPos && br <= gridLineMaxPos) {
                        val elem = buildGridLine(br, gridTheme.minorLineWidth(), gridTheme.minorLineColor())
                        rootElement.children().add(elem)
                    }
                }
            }

            // Major grid.
            if (gridTheme.showMajor()) {
                for (br in breaksData.majorBreaks) {
                    if (br >= gridLineMinPos && br <= gridLineMaxPos) {
                        val elem = buildGridLine(br, gridTheme.majorLineWidth(), gridTheme.majorLineColor())
                        rootElement.children().add(elem)
                    }
                }
            }
        }

        // Axis
        if (!hideAxis) {
            // Ticks and labels
            if (!hideAxisBreaks && (axisTheme.showLabels() || axisTheme.showTickMarks())) {
                val labelsCleaner = TickLabelsCleaner(
                    orientation.isHorizontal,
                    PlotLabelSpecFactory.axisTick(axisTheme)
                )

                for ((i, br) in breaksData.majorBreaks.withIndex()) {
                    if (br >= start && br <= end) {
                        val label = breaksData.majorLabels[i % breaksData.majorLabels.size]
                        val labelOffset = tickLabelBaseOffset().add(labelAdjustments.additionalOffset(i))
                        val group = buildTick(
                            label,
                            labelOffset,
                            skipLabel = !labelsCleaner.beforeAddLabel(
                                br,
                                label,
                                labelAdjustments.rotationDegree,
                                labelOffset
                            ),
                            axisTheme
                        )

                        when (orientation) {
                            Orientation.LEFT, Orientation.RIGHT -> transformTranslate(group, 0.0, br)
                            Orientation.TOP, Orientation.BOTTOM -> transformTranslate(group, br, 0.0)
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
                }
                rootElement.children().add(axisLine)
            }
        }
    }

    private fun buildGridLine(br: Double, width: Double, color: Color): SvgLineElement {
        val start = gridLineDistance
        val end = start + gridLineLength
        val elem = when (orientation) {
            Orientation.LEFT -> SvgLineElement(start, 0.0, end, 0.0)
            Orientation.RIGHT -> SvgLineElement(-start, 0.0, -end, 0.0)
            Orientation.TOP -> SvgLineElement(0.0, start, 0.0, end)
            Orientation.BOTTOM -> SvgLineElement(0.0, -start, 0.0, -end)
        }
        elem.strokeColor().set(color)
        elem.strokeWidth().set(width)

        when (orientation) {
            Orientation.LEFT, Orientation.RIGHT -> {
                elem.y1().set(br)
                elem.y2().set(br)
            }
            Orientation.TOP, Orientation.BOTTOM -> {
                elem.x1().set(br)
                elem.x2().set(br)
            }
        }
        return elem
    }

    private fun buildTick(
        label: String,
        labelOffset: DoubleVector,
        skipLabel: Boolean,
        axisTheme: AxisTheme
    ): SvgGElement {

        var tickMark: SvgLineElement? = null
        if (axisTheme.showTickMarks()) {
            tickMark = SvgLineElement()
            tickMark.strokeWidth().set(axisTheme.tickMarkWidth())
            tickMark.strokeColor().set(axisTheme.tickMarkColor())
        }

        var tickLabel: TextLabel? = null
        if (!skipLabel && axisTheme.showLabels()) {
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

    private fun tickLabelBaseOffset(): DoubleVector {
        val distance = axisTheme.tickLabelDistance(orientation.isHorizontal)
        return when (orientation) {
            Orientation.LEFT -> DoubleVector(axisTheme.tickLabelMargins().left - distance, 0.0)
            Orientation.RIGHT -> DoubleVector(distance - axisTheme.tickLabelMargins().right, 0.0)
            Orientation.TOP -> DoubleVector(0.0, axisTheme.tickLabelMargins().top - distance)
            Orientation.BOTTOM -> DoubleVector(0.0, distance - axisTheme.tickLabelMargins().bottom)
        }
    }

    class BreaksData constructor(
        val majorBreaks: List<Double>,
        val majorLabels: List<String>,
        minorBreaks: List<Double>? = null,
    ) {
        val minorBreaks: List<Double> = minorBreaks ?: let {
            if (majorBreaks.size <= 1) {
                emptyList()
            } else {
                // Default minor grid: a minor line in the middle between each pair of major lines.
                @Suppress("NAME_SHADOWING")
                val minorBreaks: MutableList<Double> = majorBreaks.subList(0, majorBreaks.size - 1)
                    .zip(majorBreaks.subList(1, majorBreaks.size))
                    .fold(ArrayList()) { l, pair ->
                        l.add((pair.second - pair.first) / 2 + pair.first)
                        l
                    }

                // Add one in the front
                majorBreaks.take(2).reduce { first, second -> second - first }.run {
                    minorBreaks.add(0, minorBreaks.first() - this)
                }

                // Add one in the back.
                majorBreaks.takeLast(2).reduce { first, second -> second - first }.run {
                    minorBreaks.add(0, minorBreaks.last() + this)
                }

                minorBreaks
            }
        }
    }

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

    private class TickLabelsCleaner(private val horizontalAxis: Boolean, private val labelSpec: LabelSpec) {
        private val filledAreas = ArrayList<DoubleRectangle>()

        fun beforeAddLabel(loc: Double, label: String, rotationDegree: Double, labelOffset: DoubleVector): Boolean {
            if (!isRelevant(rotationDegree)) return true

            val rect = labelRect(loc, label, rotationDegree, labelOffset)
            // find overlap
            if (filledAreas.any { it.intersects(rect)}) {
                // overlap - don't add this label
                return false
            }
            filledAreas.add(rect)
            return true
        }

        private fun isRelevant(rotationDegree: Double): Boolean {
            return isVertical(rotationDegree) || isHorizontal(rotationDegree)
        }

        private fun isHorizontal(rotationDegree: Double): Boolean {
            return rotationDegree % 180 == 0.0
        }

        private fun isVertical(rotationDegree: Double): Boolean {
            return abs(rotationDegree / 90) % 2 == 1.0
        }

        private fun labelRect(loc: Double, label: String, rotationDegree: Double, labelOffset: DoubleVector): DoubleRectangle {
            val labelNormalSize = labelSpec.dimensions(label)
            val wh = if (isVertical(rotationDegree)) {
                labelNormalSize.flip()
            } else {
                labelNormalSize
            }
            val origin = if (horizontalAxis) DoubleVector(loc, 0.0) else DoubleVector(0.0, loc)
            return DoubleRectangle(origin, wh)
                .subtract(wh.mul(0.5)) // labels use central adjustments
                .add(labelOffset)
        }
    }
}

