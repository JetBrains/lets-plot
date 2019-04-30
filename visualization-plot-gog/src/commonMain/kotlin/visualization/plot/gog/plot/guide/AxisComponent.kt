package jetbrains.datalore.visualization.plot.gog.plot.guide

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.observable.event.EventSources
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.PropertyBinding.bindOneWay
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgLineElement
import jetbrains.datalore.visualization.base.svg.SvgUtils.transformTranslate
import jetbrains.datalore.visualization.plot.gog.core.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel.HorizontalAnchor.*
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel.VerticalAnchor.*
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Style

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
    val gridLineColor: Property<Color> = ValueProperty(Color.LIGHT_GRAY)
    val lineWidth: Property<Double> = ValueProperty(1.0)
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
        when (orientation) {
            Orientation.LEFT, Orientation.RIGHT -> return CENTER
            Orientation.TOP -> return BOTTOM
            Orientation.BOTTOM -> return TOP
            else -> throw RuntimeException("Unexpected orientation:$orientation")
        }
    }

    init {
        this.length.set(length)
        this.orientation.set(orientation)

        tickLabelHorizontalAnchor = ValueProperty(defTickLabelHorizontalAnchor(orientation))
        tickLabelVerticalAnchor = ValueProperty(defTickLabelVerticalAnchor(orientation))

        EventSources.composite(
                this.length as EventSource<PropertyChangeEvent<*>>,
                this.orientation as EventSource<PropertyChangeEvent<*>>,
                breaks as EventSource<PropertyChangeEvent<*>>,
                labels as EventSource<PropertyChangeEvent<*>>,
                gridLineLength as EventSource<PropertyChangeEvent<*>>,

                tickLabelOffsets as EventSource<PropertyChangeEvent<*>>,
                tickLabelHorizontalAnchor as EventSource<PropertyChangeEvent<*>>,
                tickLabelVerticalAnchor as EventSource<PropertyChangeEvent<*>>,
                tickLabelRotationDegree as EventSource<PropertyChangeEvent<*>>,
                tickLabelSmallFont as EventSource<PropertyChangeEvent<*>>
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

        if (breaksEnabled()) {
            // add ticks before axis line
            val breaks = this.breaks.get()
            if (!(breaks == null || breaks.isEmpty())) {

                var labels: List<String>? = this.labels.get()
                if (labels == null || labels.isEmpty()) {
                    labels = ArrayList()
                    for (i in breaks.indices) {
                        labels.add("")
                    }
                }

                var i = 0
                for (br in breaks) {
                    val addGridLine = br >= gridLineMinPos && br <= gridLineMaxPos
                    val label = labels[i % labels.size]
                    val labelOffset = tickLabelOffset(i)
                    i++
                    val group = buildTick(
                            label,
                            labelOffset,
                            if (addGridLine) gridLineLength.get() else 0.0)

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

    private fun buildTick(label: String, labelOffset: DoubleVector, gridLineLength: Double): SvgGElement {

        var tickMark: SvgLineElement? = null
        if (tickMarksEnabled().get()) {
            tickMark = SvgLineElement()
            reg(bindOneWay(tickMarkWidth, tickMark.strokeWidth()))
            reg(bindOneWay(tickColor, tickMark.strokeColor()))
        }

        var tickLabel: TextLabel? = null
        if (tickLabelsEnabled().get()) {
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

    fun tickLabelDistance(): Double {
        return tickMarkLength.get() + tickMarkPadding.get()
    }

    fun tickLabelBaseOffset(): DoubleVector {
        val distance = tickLabelDistance()
        when (orientation.get()) {
            Orientation.LEFT -> return DoubleVector(-distance, 0.0)
            Orientation.RIGHT -> return DoubleVector(distance, 0.0)
            Orientation.TOP -> return DoubleVector(0.0, -distance)
            Orientation.BOTTOM -> return DoubleVector(0.0, distance)
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
}

