/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.observable.event.EventSources
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.PropertyBinding.bindOneWay
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.base.render.svg.TextLabel.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.TextLabel.VerticalAnchor.*
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgUtils.transformTranslate

class AxisComponent(length: Double, orientation: Orientation) : SvgComponent() {

    val breaks: Property<List<Double>?> = ValueProperty(null)
    val labels: Property<List<String>?> = ValueProperty(null)

    // layout
    val tickLabelRotationDegree: Property<Double> = ValueProperty(0.0)
    val tickLabelHorizontalAnchor: Property<TextLabel.HorizontalAnchor>

    // todo: minorBreaks
    val tickLabelVerticalAnchor: Property<TextLabel.VerticalAnchor>
    val tickLabelSmallFont: Property<Boolean> = ValueProperty(false)
    val tickLabelOffsets: Property<List<DoubleVector>?> = ValueProperty(null)  // optional
    val lineWidth: Property<Double> = ValueProperty(1.0)
    val gridLineColor: Property<Color> = ValueProperty(Color.LIGHT_GRAY)
    val gridLineWidth: Property<Double> = ValueProperty(1.0)
    val gridLineLength: Property<Double> = ValueProperty(0.0)
    val tickMarkWidth: Property<Double> = ValueProperty(1.0)
    val tickMarkLength: Property<Double> = ValueProperty(6.0)
    val tickMarkPadding: Property<Double> = ValueProperty(3.0)
    private val length = ValueProperty<Double?>(null)
    private val orientation = ValueProperty<Orientation?>(null)

    // theme
    private val myTickMarksEnabled = ValueProperty(true)
    private val myTickLabelsEnabled = ValueProperty(true)
    private val myAxisLineEnabled = ValueProperty(true)
    private val lineColor = ValueProperty(Color.BLACK)
    private val tickColor = ValueProperty(Color.BLACK)

    private fun defTickLabelHorizontalAnchor(orientation: Orientation): TextLabel.HorizontalAnchor {
        return when (orientation) {
            Orientation.LEFT -> RIGHT
            Orientation.RIGHT -> LEFT
            Orientation.TOP, Orientation.BOTTOM -> MIDDLE
        }
    }

    private fun defTickLabelVerticalAnchor(orientation: Orientation): TextLabel.VerticalAnchor {
        return when (orientation) {
            Orientation.LEFT, Orientation.RIGHT -> CENTER
            Orientation.TOP -> BOTTOM
            Orientation.BOTTOM -> TOP
            else -> throw RuntimeException("Unexpected orientation:$orientation")
        }
    }

    init {
        this.length.set(length)
        this.orientation.set(orientation)

        tickLabelHorizontalAnchor = ValueProperty(defTickLabelHorizontalAnchor(orientation))
        tickLabelVerticalAnchor = ValueProperty(defTickLabelVerticalAnchor(orientation))

        @Suppress("UNCHECKED_CAST")
        fun <T> EventSource<in PropertyChangeEvent<T>>.asPropertyChangedEventSource() =
            this as EventSource<PropertyChangeEvent<*>>

        EventSources.composite(
            this.length.asPropertyChangedEventSource(),
            this.orientation.asPropertyChangedEventSource(),
            breaks.asPropertyChangedEventSource(),
            labels.asPropertyChangedEventSource(),
            gridLineLength.asPropertyChangedEventSource(),
            tickLabelOffsets.asPropertyChangedEventSource(),
            tickLabelHorizontalAnchor.asPropertyChangedEventSource(),
            tickLabelVerticalAnchor.asPropertyChangedEventSource(),
            tickLabelRotationDegree.asPropertyChangedEventSource(),
            tickLabelSmallFont.asPropertyChangedEventSource()
        ).addHandler(rebuildHandler())
    }

    override fun buildComponent() {
        buildAxis()
    }

    private fun buildAxis() {
        val rootElement = rootGroup
        rootElement.addClass(Style.AXIS)
        if (tickLabelSmallFont.get()) {
            rootElement.addClass(Style.SMALL_TICK_FONT)
        }

        val l = length.get()!!
        val x1: Double
        val y1: Double
        val x2: Double
        val y2: Double
        val start: Double
        val end: Double
        when (orientation.get()) {
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
            else -> throw RuntimeException("Unexpected orientation:" + orientation.get())
        }

        var axisLine: SvgLineElement? = null
        if (axisLineEnabled().get()) {
            axisLine = SvgLineElement(x1, y1, x2, y2)
            reg(bindOneWay(lineWidth, axisLine.strokeWidth()))
            reg(bindOneWay(lineColor, axisLine.strokeColor()))
        }

        // do not draw grid lines then it's too close to axis ends.
        val gridLineMinPos = start + 3
        val gridLineMaxPos = end - 3

        if (breaksEnabled() || gridLineLength.get() > 0) {
            // add ticks before axis line
            val breaks = this.breaks.get()
            if (!breaks.isNullOrEmpty()) {
                var labels: List<String>? = this.labels.get()
                if (labels.isNullOrEmpty()) {
                    labels = List(breaks.size) { "" }
                }

                val labelsCleaner = TickLabelsCleaner(orientation.get()!!.isHorizontal)

                for ((i, br) in breaks.withIndex()) {
                    val addGridLine = br >= gridLineMinPos && br <= gridLineMaxPos
                    val label = labels[i % labels.size]
                    val labelOffset = tickLabelOffset(i)
                    val group = buildTick(
                        label,
                        labelOffset,
                        if (addGridLine) gridLineLength.get() else 0.0,
                        skipLabel = !labelsCleaner.beforeAddLabel(br, tickLabelRotationDegree.get())
                    )

                    when (orientation.get()) {
                        Orientation.LEFT, Orientation.RIGHT -> transformTranslate(group, 0.0, br)
                        Orientation.TOP, Orientation.BOTTOM -> transformTranslate(group, br, 0.0)
                        else -> throw RuntimeException("Unexpected orientation:" + orientation.get())
                    }

                    rootElement.children().add(group)
                }
            }
        }

        // axis line
        if (axisLine != null) {
            rootElement.children().add(axisLine)
        }
    }

    private fun buildTick(
        label: String,
        labelOffset: DoubleVector,
        gridLineLength: Double,
        skipLabel: Boolean
    ): SvgGElement {

        var tickMark: SvgLineElement? = null
        if (tickMarksEnabled().get()) {
            tickMark = SvgLineElement()
            reg(bindOneWay(tickMarkWidth, tickMark.strokeWidth()))
            reg(bindOneWay(tickColor, tickMark.strokeColor()))
        }

        var tickLabel: TextLabel? = null
        if (tickLabelsEnabled().get() && !skipLabel) {
            tickLabel = TextLabel(label)
            reg(bindOneWay(tickColor, tickLabel.textColor()))
        }

        var gridLine: SvgLineElement? = null // optional;
        if (gridLineLength > 0) {
            gridLine = SvgLineElement()
            reg(bindOneWay(gridLineColor, gridLine.strokeColor()))
            reg(bindOneWay(gridLineWidth, gridLine.strokeWidth()))
        }

        val markLength = tickMarkLength.get()
        when (orientation.get()) {
            Orientation.LEFT -> {
                if (tickMark != null) {
                    tickMark.x2().set(-markLength)
                    tickMark.y2().set(0.0)
                }
                if (gridLine != null) {
                    gridLine.x2().set(gridLineLength)
                    gridLine.y2().set(0.0)
                }
            }
            Orientation.RIGHT -> {
                if (tickMark != null) {
                    tickMark.x2().set(markLength)
                    tickMark.y2().set(0.0)
                }
                if (gridLine != null) {
                    gridLine.x2().set(-gridLineLength)
                    gridLine.y2().set(0.0)
                }
            }
            Orientation.TOP -> {
                if (tickMark != null) {
                    tickMark.x2().set(0.0)
                    tickMark.y2().set(-markLength)
                }
                if (gridLine != null) {
                    gridLine.x2().set(0.0)
                    gridLine.y2().set(gridLineLength)
                }
            }
            Orientation.BOTTOM -> {
                if (tickMark != null) {
                    tickMark.x2().set(0.0)
                    tickMark.y2().set(markLength)
                }
                if (gridLine != null) {
                    gridLine.x2().set(0.0)
                    gridLine.y2().set(-gridLineLength)
                }
            }
            else -> throw RuntimeException("Unexpected orientation:" + orientation.get())
        }

        val g = SvgGElement()
        if (gridLine != null) {
            g.children().add(gridLine)
        }

        if (tickMark != null) {
            g.children().add(tickMark)
        }

        if (tickLabel != null) {
            tickLabel.moveTo(labelOffset.x, labelOffset.y)
            tickLabel.setHorizontalAnchor(tickLabelHorizontalAnchor.get())
            tickLabel.setVerticalAnchor(tickLabelVerticalAnchor.get())
            tickLabel.rotate(tickLabelRotationDegree.get())
            g.children().add(tickLabel.rootGroup)
        }

        g.addClass(Style.TICK)
        return g
    }

    private fun tickMarkLength(): Double {
        return if (myTickMarksEnabled.get()) {
            tickMarkLength.get()
        } else {
            0.0
        }
    }

    private fun tickLabelDistance(): Double {
        return tickMarkLength() + tickMarkPadding.get()
    }

    private fun tickLabelBaseOffset(): DoubleVector {
        val distance = tickLabelDistance()
        return when (orientation.get()) {
            Orientation.LEFT -> DoubleVector(-distance, 0.0)
            Orientation.RIGHT -> DoubleVector(distance, 0.0)
            Orientation.TOP -> DoubleVector(0.0, -distance)
            Orientation.BOTTOM -> DoubleVector(0.0, distance)
            else -> throw RuntimeException("Unexpected orientation:" + orientation.get())
        }
    }

    private fun tickLabelOffset(tickIndex: Int): DoubleVector {
        val additionalOffsets = tickLabelOffsets.get()
        val additionalOffset = if (additionalOffsets != null) additionalOffsets[tickIndex] else DoubleVector.ZERO
        return tickLabelBaseOffset().add(additionalOffset)
    }

    private fun breaksEnabled(): Boolean {
        return myTickMarksEnabled.get() || myTickLabelsEnabled.get()
    }

    fun tickMarksEnabled(): Property<Boolean> {
        return myTickMarksEnabled
    }

    fun tickLabelsEnabled(): Property<Boolean> {
        return myTickLabelsEnabled
    }

    fun axisLineEnabled(): Property<Boolean> {
        return myAxisLineEnabled
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

