/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.base.render.svg.TextLabel.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.TextLabel.VerticalAnchor.*
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.PanelGridTheme
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgUtils.transformTranslate

class AxisComponent(
    private val length: Double,
    private val orientation: Orientation,
    private val breaksData: BreaksData,
    private val labelAdjustments: TickLabelAdjustments = TickLabelAdjustments(orientation),
    private val gridLineLength: Double,
    private val axisTheme: AxisTheme,
    private val gridTheme: PanelGridTheme,
    private val useSmallFont: Boolean = false,
    private val hideAxis: Boolean = false,
    private val hideAxisBreaks: Boolean = false,
    private val hideGridlines: Boolean = false,
) : SvgComponent() {

    private val tickMarkPadding = Defaults.Plot.Axis.TICK_MARK_PADDING

    override fun buildComponent() {
        buildAxis()
    }

    private fun buildAxis() {
        val rootElement = rootGroup
        if (!hideAxis) {
            rootElement.addClass(Style.AXIS)
            if (useSmallFont) {
                rootElement.addClass(Style.SMALL_TICK_FONT)
            }
        }

        val l = length
        val x1: Double
        val y1: Double
        val x2: Double
        val y2: Double
        val start: Double
        val end: Double
        when (orientation) {
            Orientation.LEFT, Orientation.RIGHT -> {
                x2 = 0.0
                x1 = x2
                start = 0.0
                y1 = start
                end = l
                y2 = end
            }
            Orientation.TOP, Orientation.BOTTOM -> {
                start = 0.0
                x1 = start
                end = l
                x2 = end
                y2 = 0.0
                y1 = y2
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
                val labelsCleaner = TickLabelsCleaner(orientation.isHorizontal)

                for ((i, br) in breaksData.majorBreaks.withIndex()) {
                    val label = breaksData.majorLabels[i % breaksData.majorLabels.size]
                    val labelOffset = tickLabelBaseOffset().add(labelAdjustments.additionalOffset(i))
                    val group = buildTick(
                        label,
                        labelOffset,
                        skipLabel = !labelsCleaner.beforeAddLabel(br, labelAdjustments.rotationDegree),
                        axisTheme
                    )

                    when (orientation) {
                        Orientation.LEFT, Orientation.RIGHT -> transformTranslate(group, 0.0, br)
                        Orientation.TOP, Orientation.BOTTOM -> transformTranslate(group, br, 0.0)
                    }

                    rootElement.children().add(group)
                }
            }

            // Axis line
//        val axisLine: SvgLineElement? =
//            if (!hideAxisBreaks && axisTheme.showLine()) {
//                SvgLineElement(x1, y1, x2, y2).apply {
//                    strokeWidth().set(axisTheme.lineWidth())
//                    strokeColor().set(axisTheme.lineColor())
//                }
//            } else {
//                null
//            }
//
//        if (axisLine != null) {
//            rootElement.children().add(axisLine)
//        }

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
        val elem = when (orientation) {
            Orientation.LEFT -> SvgLineElement(0.0, 0.0, gridLineLength, 0.0)
            Orientation.RIGHT -> SvgLineElement(0.0, 0.0, -gridLineLength, 0.0)
            Orientation.TOP -> SvgLineElement(0.0, 0.0, 0.0, gridLineLength)
            Orientation.BOTTOM -> SvgLineElement(0.0, 0.0, 0.0, -gridLineLength)
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
            tickLabel.textColor().set(axisTheme.labelColor())
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

        g.addClass(Style.TICK)
        return g
    }


//    private fun tickLabelDistance(): Double {
//        return tickMarkLength() + tickMarkPadding.get()
//    }

    private fun tickLabelBaseOffset(): DoubleVector {
        val distance = axisTheme.tickLabelDistance()
        return when (orientation) {
            Orientation.LEFT -> DoubleVector(-distance, 0.0)
            Orientation.RIGHT -> DoubleVector(distance, 0.0)
            Orientation.TOP -> DoubleVector(0.0, -distance)
            Orientation.BOTTOM -> DoubleVector(0.0, distance)
        }
    }


    companion object {
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
        horizontalAnchor: TextLabel.HorizontalAnchor? = null,
        verticalAnchor: TextLabel.VerticalAnchor? = null,
        val rotationDegree: Double = 0.0,
        private val additionalOffsets: List<DoubleVector>? = null
    ) {
        val horizontalAnchor: TextLabel.HorizontalAnchor = horizontalAnchor ?: when (orientation) {
            Orientation.LEFT -> RIGHT
            Orientation.RIGHT -> LEFT
            Orientation.TOP, Orientation.BOTTOM -> MIDDLE
        }
        val verticalAnchor: TextLabel.VerticalAnchor = verticalAnchor ?: when (orientation) {
            Orientation.LEFT, Orientation.RIGHT -> CENTER
            Orientation.TOP -> BOTTOM
            Orientation.BOTTOM -> TOP
        }

        fun additionalOffset(tickIndex: Int): DoubleVector {
            return additionalOffsets?.get(tickIndex) ?: DoubleVector.ZERO
        }
    }

    private class TickLabelsCleaner(val horizontalAxis: Boolean) {
        private val filledRanges = ArrayList<ClosedRange<Double>>()

        fun beforeAddLabel(loc: Double, rotationDegree: Double): Boolean {
            if (!isRelevant(rotationDegree)) return true

            val len = PlotLabelSpec.AXIS_TICK.height()

            // find overlap
            if (filledRanges.any { it.contains(loc) || it.contains(loc + len) }) {
                // overlap - don't add this label
                return false
            }

            filledRanges.add(ClosedRange(loc, loc + len))
            return true
        }

        private fun isRelevant(rotationDegree: Double): Boolean {
            return when {
                horizontalAxis -> isVertical(rotationDegree)
                else -> isHorizontal(rotationDegree)
            }
        }

        private fun isHorizontal(rotationDegree: Double): Boolean {
            return rotationDegree % 180 == 0.0
        }

        private fun isVertical(rotationDegree: Double): Boolean {
            return (rotationDegree / 90) % 2 == 1.0
        }
    }
}

