/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.component

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.*
import org.jetbrains.letsPlot.core.plot.base.render.text.LineBoxMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipDefaults
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipMarker
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipModel
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipStyle
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.Orientation.HORIZONTAL
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.Orientation.VERTICAL
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.PointerDirection.*
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class TooltipBox(
    private val styleSheet: StyleSheet
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

    private enum class PointerMode {
        POINTER,      // stem pointer from the box to the target
        POINT_MARKER, // no stem; a point marker drawn at the target (rotated tooltips)
        NONE          // no stem, no marker (merged tooltips - markers are drawn by the renderer)
    }

    val contentRect
        get() = DoubleRectangle.span(
            DoubleVector.ZERO,
            myContentBox.dimension
        )

    private val myPointerBox = PointerBox()
    private val myContentBox = ContentBox()

    internal val pointerDirection get() = myPointerBox.pointerDirection // for tests
    private var myHorizontalContentPadding = TooltipDefaults.H_CONTENT_PADDING
    private var myVerticalContentPadding = TooltipDefaults.V_CONTENT_PADDING
    private val myYPositionsBetweenLines = mutableListOf<Double>()

    // draw tooltip content rectangles in DEBUG_DRAWING mode
    private val myDebugRectangles = SvgComponentPool(
        itemFactory = Companion::RectangleComponent,
        parent = this.rootGroup
    )

    override fun buildComponent() {
        add(myPointerBox)
        add(myContentBox)
    }

    fun update(
        fillColor: Color,
        textColor: Color?,
        borderColor: Color,
        strokeWidth: Double,
        lineType: LineType,
        lines: List<TooltipModel.Line>,
        title: String?,
        textClassName: String,
        tooltipMinWidth: Double? = null,
        borderRadius: Double,
        marker: TooltipMarker,
        pointMarkerStrokeColor: Color = borderColor,
        coord: DoubleVector = DoubleVector.ZERO,
    ) {
        update(
            fillColor = fillColor,
            textColor = textColor,
            borderColor = borderColor,
            strokeWidth = strokeWidth,
            lineType = lineType,
            targets = listOf(TooltipModel.Target(title = null, marker = marker, lines = lines, coord = coord)),
            title = title,
            textClassName = textClassName,
            tooltipMinWidth = tooltipMinWidth,
            borderRadius = borderRadius,
            pointMarkerStrokeColor = pointMarkerStrokeColor
        )
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
        val lines = targets.flatMap { target ->
            listOfNotNull(target.title?.let(TooltipModel.Line::withValue)) + target.lines
        }
        val totalLines = lines.size + if (title != null) 1 else 0
        myHorizontalContentPadding = if (totalLines > 1) {
            TooltipDefaults.CONTENT_EXTENDED_PADDING
        } else {
            TooltipDefaults.H_CONTENT_PADDING
        }
        myVerticalContentPadding = if (totalLines > 1) {
            TooltipDefaults.CONTENT_EXTENDED_PADDING
        } else {
            TooltipDefaults.V_CONTENT_PADDING
        }
        myYPositionsBetweenLines.clear()

        myContentBox.update(
            targets,
            title,
            textColor,
            tooltipMinWidth,
            textClassName
        )
        myPointerBox.updateStyle(fillColor, borderColor, strokeWidth, lineType, borderRadius, pointMarkerStrokeColor)
    }

    fun setPosition(
        tooltipCoord: DoubleVector,
        pointerCoord: DoubleVector,
        orientation: Orientation,
        rotate: Boolean = false,
        showPointer: Boolean = true
    ) {
        // Rotate component
        val rotationAngle = if (rotate) TooltipDefaults.ROTATION_ANGLE else 0.0
        rotate(rotationAngle)

        val p = pointerCoord
            .subtract(tooltipCoord)
            .rotate(toRadians(-rotationAngle))   // cancel rotation for pointer point coordinates

        val pointerMode = when {
            rotate -> PointerMode.POINT_MARKER
            showPointer -> PointerMode.POINTER
            else -> PointerMode.NONE
        }
        myPointerBox.update(p, orientation, pointerMode)
        moveTo(tooltipCoord)

        if (DEBUG_DRAWING) {
            myContentBox.drawDebugRect()
        }
    }

    private fun setSvgSvgStyle(svgSvgElement: SvgSvgElement, prefix: String) {
        val id = SvgUID.get(prefix)
        svgSvgElement.id().set(id)
        svgSvgElement.setStyle(object : SvgCssResource {
            override fun css(): String {
                return buildString {
                    styleSheet.getClasses().forEach { className ->
                        append(styleSheet.toCSS(className, id))
                    }
                }
            }
        })

    }

    private inner class PointerBox : SvgComponent() {
        private val myPointerPath = SvgPathElement()
        var pointerDirection: PointerDirection? = null
        private var myBorderRadius = 0.0
        private val myHighlightPoint = SvgPathElement()

        override fun buildComponent() {
            add(myPointerPath)
            add(myHighlightPoint)
        }

        fun updateStyle(
            fillColor: Color,
            borderColor: Color,
            strokeWidth: Double,
            lineType: LineType,
            borderRadius: Double,
            pointMarkerStrokeColor: Color
        ) {
            myBorderRadius = borderRadius

            myPointerPath.apply {
                strokeColor().set(borderColor)
                strokeWidth().set(strokeWidth)
                fillColor().set(fillColor)
                StrokeDashArraySupport.apply(this, strokeWidth, lineType)
            }

            myHighlightPoint.apply {
                val fill = if (fillColor == pointMarkerStrokeColor) borderColor else fillColor
                fillColor().set(fill)
                strokeWidth().set(1.0)
                strokeColor().set(pointMarkerStrokeColor)
            }
        }

        fun update(pointerCoord: DoubleVector, orientation: Orientation, pointerMode: PointerMode) {
            pointerDirection = if (pointerMode != PointerMode.POINTER) null else when (orientation) {
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

            myPointerPath.d().set(
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
                        moveTo(right - myBorderRadius, bottom)

                        // right-bottom
                        corner(
                            DoubleVector(right - myBorderRadius, bottom),
                            DoubleVector(right, bottom),
                            DoubleVector(right, bottom - myBorderRadius)
                        )

                        // right side
                        lineTo(right, bottom + vertFootingIndent)
                        lineToIf(pointerCoord, pointerDirection == RIGHT)
                        lineTo(right, top - vertFootingIndent)

                        // right-top corner
                        corner(
                            DoubleVector(right, top + myBorderRadius),
                            DoubleVector(right, top),
                            DoubleVector(right - myBorderRadius, top)
                        )

                        // top side
                        lineTo(right - horFootingIndent, top)
                        lineToIf(pointerCoord, pointerDirection == UP)
                        lineTo(left + horFootingIndent, top)

                        // left-top corner
                        corner(
                            DoubleVector(left + myBorderRadius, top),
                            DoubleVector(left, top),
                            DoubleVector(left, top + myBorderRadius)
                        )

                        // left side
                        lineTo(left, top - vertFootingIndent)
                        lineToIf(pointerCoord, pointerDirection == LEFT)
                        lineTo(left, bottom + vertFootingIndent)

                        // left-bottom corner
                        corner(
                            DoubleVector(left, bottom - myBorderRadius),
                            DoubleVector(left, bottom),
                            DoubleVector(left + myBorderRadius, bottom)
                        )

                        // bottom side
                        lineTo(left + horFootingIndent, bottom)
                        lineToIf(pointerCoord, pointerDirection == DOWN)
                        lineTo(right - horFootingIndent, bottom)
                        lineTo(right - myBorderRadius, bottom)
                    }
                }.build()
            )

            if (pointerMode == PointerMode.POINT_MARKER) {
                myHighlightPoint.d().set(trianglePointer(pointerCoord).build())
                SvgUtils.transformRotate(
                    myHighlightPoint,
                    -2 * TooltipDefaults.ROTATION_ANGLE,
                    pointerCoord.x,
                    pointerCoord.y
                )
                myHighlightPoint.visibility().set(SvgGraphicsElement.Visibility.VISIBLE)
            } else {
                myHighlightPoint.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
            }
        }

        private fun calculatePointerFootingIndent(sideLength: Double): Double {
            val footingLength = min(
                sideLength * TooltipDefaults.POINTER_FOOTING_TO_SIDE_LENGTH_RATIO,
                TooltipDefaults.MAX_POINTER_FOOTING_LENGTH
            )
            return (sideLength - footingLength) / 2
        }

        private fun trianglePointer(pointerCoord: DoubleVector) = SvgPathDataBuilder().apply {
            val xy = TRIANGLE_POINTS.map { it.add(pointerCoord) }
            moveTo(xy[0])
            xy.forEach(::lineTo)
            closePath()
        }
    }

    private inner class ContentBox : SvgComponent() {
        private val myTitleContainer = SvgSvgElement().apply {
            x().set(0.0)
            y().set(0.0)
            width().set(0.0)
            height().set(0.0)
        }
        private val myLinesContainer = SvgSvgElement().apply {
            x().set(0.0)
            y().set(0.0)
            width().set(0.0)
            height().set(0.0)
        }
        private val myContent = SvgSvgElement().apply {
            x().set(0.0)
            y().set(0.0)
            width().set(0.0)
            height().set(0.0)
        }

        private val myColorBars = mutableListOf<SvgPathElement>()
        private val myLineYBounds = mutableListOf<Pair<Double, Double>>()
        private var colorBarIndent = 0.0

        val dimension get() = myContent.run { DoubleVector(width().get()!!, height().get()!!) }

        override fun buildComponent() {
            setSvgSvgStyle(myContent, "tt-c-")
            setSvgSvgStyle(myTitleContainer, "tt-t-")
            setSvgSvgStyle(myLinesContainer, "tt-l-")

            add(myContent)
            myContent.children().add(myTitleContainer)
            myContent.children().add(myLinesContainer)
        }

        fun update(
            targets: List<TooltipModel.Target>,
            title: String?,
            valueTextColor: Color?,
            tooltipMinWidth: Double?,
            textClassName: String
        ) {
            myLinesContainer.children().clear()
            myTitleContainer.children().clear()

            val targetLayout = layoutTargets(targets)
            calculateColorBarIndent(targets)

            // title component
            val titleComponent = title?.let(::initTitleComponent)
            val rawTitleBBox = getBBox(titleComponent) ?: DoubleRectangle.ZERO

            // detect min tooltip width
            val minWidthWithTitle = listOfNotNull(tooltipMinWidth, rawTitleBBox.width).maxOrNull()

            // lines (label: value)
            val textSize = layoutLines(
                targetLayout.lines,
                valueTextColor,
                minWidthWithTitle,
                textClassName
            )

            val totalTooltipWidth = textSize.x + colorBarIndent + myHorizontalContentPadding * 2

            // title
            val titleTextSize = layoutTitle(
                titleComponent,
                totalTooltipWidth,
                rawTitleBBox
            )

            // container sizes

            myTitleContainer.apply {
                if (titleComponent != null) {
                    x().set(0.0)
                    y().set(myVerticalContentPadding)
                    width().set(totalTooltipWidth)
                    height().set(titleTextSize.y)
                }
            }

            myLinesContainer.apply {
                x().set(myHorizontalContentPadding + colorBarIndent)
                y().set(titleTextSize.y + myVerticalContentPadding)
                width().set(totalTooltipWidth - myHorizontalContentPadding)
                height().set(textSize.y + titleTextSize.y + myVerticalContentPadding)
            }

            myContent.apply {
                width().set(totalTooltipWidth)
                height().set(textSize.y + titleTextSize.y + myVerticalContentPadding * 2)
            }

            // draw color bars
            layoutColorBars(targets, targetLayout.lineRanges)

            // draw lines
            drawLineSeparators(
                yTitleLinePosition = if (titleComponent != null) titleTextSize.y - myVerticalContentPadding / 2 else null,
                myYPositionsBetweenLines
            )
        }

        private fun colorBars(marker: TooltipMarker): List<Pair<Color, Double>> {
            return when {
                marker.majorColor != null && marker.minorColor != null -> listOf(
                    marker.minorColor to TooltipDefaults.COLOR_BAR_STROKE_WIDTH,
                    marker.majorColor to TooltipDefaults.COLOR_BAR_WIDTH,
                    marker.minorColor to TooltipDefaults.COLOR_BAR_STROKE_WIDTH
                )

                marker.majorColor != null -> listOf(
                    marker.majorColor to TooltipDefaults.COLOR_BAR_WIDTH
                )

                marker.minorColor != null -> listOf(
                    marker.minorColor to TooltipDefaults.COLOR_BAR_STROKE_WIDTH
                )

                else -> emptyList()
            }
        }

        private fun layoutTargets(targets: List<TooltipModel.Target>): TargetsLayout {
            val lines = ArrayList<TooltipModel.Line>()
            val lineRanges = ArrayList<IntRange>()
            targets.forEach { target ->
                val firstLineIndex = lines.size
                target.title?.let { lines += TooltipModel.Line.withValue(it) }
                lines += target.lines
                val lastLineIndex = lines.lastIndex
                lineRanges += firstLineIndex..lastLineIndex
            }
            return TargetsLayout(lines, lineRanges)
        }

        private fun calculateColorBarIndent(targets: List<TooltipModel.Target>) {
            val markerWidth = targets.maxOfOrNull { target ->
                colorBars(target.marker).sumOf { (_, width) -> width }
            }
            colorBarIndent = if (markerWidth != null && markerWidth != 0.0) {
                markerWidth + myHorizontalContentPadding
            } else {
                0.0
            }
        }

        private fun layoutColorBars(targets: List<TooltipModel.Target>, lineRanges: List<IntRange>) {
            val targetColorBars = targets.zip(lineRanges).flatMap { (target, lineRange) ->
                val yBounds = lineRange
                    .filter { it in myLineYBounds.indices }
                    .map(myLineYBounds::get)
                val top = yBounds.minOfOrNull { (top, _) -> top }
                val bottom = yBounds.maxOfOrNull { (_, bottom) -> bottom }
                if (top == null || bottom == null) {
                    emptyList()
                } else {
                    var xOffset = 0.0
                    colorBars(target.marker).map { colorBar ->
                        BlockColorBar(colorBar.first, colorBar.second, top, bottom, xOffset).also {
                            xOffset += colorBar.second
                        }
                    }
                }
            }
            updateColorBarCount(targetColorBars.size)

            myColorBars
                .zip(targetColorBars)
                .forEach { (bar, colorBar) ->
                    val x = contentRect.left + myHorizontalContentPadding + colorBar.xOffset
                    bar.fillOpacity().set(1.0)
                    bar.fillColor().set(colorBar.color)
                    // adjacent vertical bars
                    bar.d().set(
                        SvgPathDataBuilder().apply {
                            val y = myLinesContainer.y().get()!! + colorBar.top
                            val bottom = myLinesContainer.y().get()!! + colorBar.bottom
                            moveTo(x, y)
                            horizontalLineTo(x + colorBar.width)
                            verticalLineTo(bottom)
                            horizontalLineTo(x)
                            verticalLineTo(y)
                        }.build()
                    )
                }
        }

        private fun updateColorBarCount(count: Int) {
            while (myColorBars.size > count) {
                val bar = myColorBars.removeLast()
                rootGroup.children().remove(bar)
            }
            while (myColorBars.size < count) {
                SvgPathElement().also { bar ->
                    rootGroup.children().add(bar)
                    myColorBars.add(bar)
                }
            }
        }

        private inner class TargetsLayout(
            val lines: List<TooltipModel.Line>,
            val lineRanges: List<IntRange>
        )

        private inner class BlockColorBar(
            val color: Color,
            val width: Double,
            val top: Double,
            val bottom: Double,
            val xOffset: Double
        )

        private fun getBBox(textLabel: Label?): DoubleRectangle? {
            if (textLabel == null || textLabel.text.isBlank()) {
                // also for blank string - Batik throws an exception for a text element with a blank string
                return null
            }
            return textLabel.rootGroup.bBox
        }

        private fun initTitleComponent(titleLine: String): Label {
            val fontSize = styleSheet.getTextStyle(TooltipStyle.TOOLTIP_TITLE).size
            val titleComponent = Label(titleLine)
            titleComponent.addClassName(TooltipStyle.TOOLTIP_TITLE)
            titleComponent.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            val defaultMetrics = LineBoxMetrics.fromBoxHeight(fontSize)
            val metricsByLine = estimateLineLayoutMetrics(titleLine, TooltipStyle.TOOLTIP_TITLE).map { it ?: defaultMetrics }
            titleComponent.setTextLayout(TextBlockLayout.fromLineBoxes(metricsByLine))
            titleComponent.setFontSize(fontSize)

            myTitleContainer.children().add(titleComponent.rootGroup)
            return titleComponent
        }

        private fun estimateLineLayoutMetrics(line: String, className: String): List<LineBoxMetrics?> {
            val style = styleSheet.getTextStyle(className)
            return line
                .split("\n")
                .map { Label(it).apply {
                    addClassName(className)
                    setFontSize(style.size)
                } }
                .map { lineTextLabel ->
                    with(myLinesContainer.children()) {
                        add(lineTextLabel.rootGroup)
                        val height = getBBox(lineTextLabel)?.height
                        remove(lineTextLabel.rootGroup)
                        height
                    }
                }
                .let { estimatedHeights ->
                    estimateBaseline(line, style, estimatedHeights)
                }
        }

        // For line-height estimation, we currently treat the SVG bounding box as the single source of truth.
        // But we need a baseline-aware height, so we also have to get the baseline,
        // and that information is only available from RichText line layout estimates.
        // This makes the algorithm more complex.
        // In practice, though, RichText line layout estimates may already be accurate enough,
        // so we should consider relying on it alone and dropping the merge with getBBox().
        private fun estimateBaseline(
            line: String,
            style: TextStyle,
            estimatedHeights: List<Double?>
        ): List<LineBoxMetrics?> {
            val font = Font(
                family = DefaultFontFamilyRegistry().get(style.family),
                size = style.size.roundToInt(),
                isBold = style.face.bold,
                isItalic = style.face.italic
            )
            val estimatedMetrics = RichText.measure(line, font).layout.lineBoxes
            if (estimatedMetrics.size == estimatedHeights.size) {
                // One scale for the whole block, not per line: keeps the baseline split
                // across lines so Label's baseline stacking stays consistent between plain and fraction lines.
                val scale = estimatedMetrics.sumOf(LineBoxMetrics::boxHeight).let { totalMetricsHeight ->
                    if (totalMetricsHeight > 0) {
                        estimatedHeights.filterNotNull().sum() / totalMetricsHeight
                    } else 1.0
                }
                return (estimatedMetrics zip estimatedHeights).map { (metrics, height) ->
                    if (height == null) {
                        null
                    } else {
                        LineBoxMetrics(
                            boxHeight = metrics.boxHeight * scale + TooltipDefaults.INTERVAL_BETWEEN_SUBSTRINGS,
                            topToBaseline = metrics.topToBaseline * scale + TooltipDefaults.INTERVAL_BETWEEN_SUBSTRINGS
                        )
                    }
                }
            }
            // Fallback
            return estimatedMetrics
        }

        private fun layoutTitle(
            titleComponent: Label?,
            totalTooltipWidth: Double,
            titleBBox: DoubleRectangle
        ): DoubleVector {
            if (titleComponent == null) {
                return DoubleVector.ZERO
            }

            titleComponent.setX(totalTooltipWidth / 2)
            titleComponent.setY(-titleBBox.top)

            return DoubleVector(totalTooltipWidth, myVerticalContentPadding + titleBBox.height)
        }

        private fun layoutLines(
            lines: List<TooltipModel.Line>,
            valueTextColor: Color?,
            tooltipMinWidth: Double?,
            textClassName: String
        ): DoubleVector {
            myLineYBounds.clear()
            val labelFontSize = styleSheet.getTextStyle(TooltipStyle.TOOLTIP_LABEL).size
            val valueFontSize = styleSheet.getTextStyle(textClassName).size
            // bBoxes
            val components: List<Pair<Label?, Label>> = lines
                .map { line ->
                    Pair(
                        line.label?.let(::Label),
                        Label(line.value, wrapWidth = TooltipDefaults.VALUE_LINE_MAX_LENGTH)
                    )
                }
            // for labels
            components.onEach { (labelComponent, _) ->
                if (labelComponent != null) {
                    labelComponent.addClassName(TooltipStyle.TOOLTIP_LABEL)
                    labelComponent.setFontSize(labelFontSize)
                    myLinesContainer.children().add(labelComponent.rootGroup)
                }
            }
            // for values
            components.onEach { (_, valueComponent) ->
                valueComponent.addClassName(textClassName)
                valueComponent.setFontSize(valueFontSize)
                valueTextColor?.let(valueComponent.textColor()::set)
                myLinesContainer.children().add(valueComponent.rootGroup)
            }

            // calculate LineLayoutMetrics of original label/value lines
            val defaultLabelMetrics = LineBoxMetrics.fromBoxHeight(labelFontSize)
            val defaultValueMetrics = LineBoxMetrics.fromBoxHeight(valueFontSize)
            val metricsByLine: List<Pair<List<LineBoxMetrics>?, List<LineBoxMetrics>>> = lines.map { line ->
                Pair(
                    line.label?.let {
                        estimateLineLayoutMetrics(it, TooltipStyle.TOOLTIP_LABEL).map { labelMetrics -> labelMetrics ?: defaultLabelMetrics }
                    },
                    estimateLineLayoutMetrics(line.value, textClassName).map { valueMetrics -> valueMetrics ?: defaultValueMetrics }
                )
            }

            // set vertical shifts for tspan elements
            metricsByLine.zip(components).onEach { (metrics, component) ->
                val (labelMetrics, valueMetrics) = metrics
                val (labelComponent, valueComponent) = component
                labelMetrics?.let { labelComponent?.setTextLayout(TextBlockLayout.fromLineBoxes(it)) }
                valueComponent.setTextLayout(TextBlockLayout.fromLineBoxes(valueMetrics))
            }

            val rawBBoxes = components.map { (label, value) -> getBBox(label) to getBBox(value) }

            // max label width - all labels will be aligned to this value
            val maxLabelWidth = rawBBoxes.maxOf { (labelBbox) -> labelBbox?.width ?: 0.0 }

            // max line height - will be used as default height for empty string
            val defaultLineHeight = metricsByLine
                .flatMap { (labelMetrics, valueMetrics) ->
                    listOfNotNull(
                        labelMetrics?.maxOfOrNull(LineBoxMetrics::boxHeight),
                        valueMetrics.maxOfOrNull(LineBoxMetrics::boxHeight)
                    )
                }
                .maxOrNull()
                ?: valueFontSize

            val labelWidths = lines.zip(components).map { (line, component) ->
                val label = line.label
                when {
                    label == null -> {
                        // label null - the value component will be centered
                        0.0
                    }

                    label.isEmpty() && component.second.linesCount() == 1 -> {
                        // label is not null, but empty - add space for the label, the value will be moved to the right;
                        // also value should not be multiline for right alignment
                        maxLabelWidth
                    }

                    else -> {
                        // align the label width to the maximum and add interval between label and value
                        maxLabelWidth + TooltipDefaults.LABEL_VALUE_INTERVAL
                    }
                }
            }
            val valueWidths = rawBBoxes.map { (_, valueBBox) -> valueBBox?.dimension?.x ?: 0.0 }
            val lineWidths = labelWidths.zip(valueWidths)

            // max line width
            val maxLineWidth = lineWidths.maxOf { (labelWidth, valueWidth) ->
                max(tooltipMinWidth ?: 0.0, labelWidth + valueWidth)
            }

            // prepare bbox
            val lineBBoxes = rawBBoxes.zip(lineWidths).map { (bBoxes, width) ->
                val (labelBBox, valueBBox) = bBoxes
                val (labelWidth, valueWidth) = width

                val labelDimension = DoubleVector(
                    labelWidth,
                    labelBBox?.run { height + top } ?: 0.0
                )
                val valueDimension = DoubleVector(
                    valueWidth,
                    valueBBox?.run { height + top } ?: if (labelBBox == null) {
                        // it's the empty line - use default height
                        defaultLineHeight
                    } else {
                        0.0
                    }
                )
                Pair(
                    DoubleRectangle(labelBBox?.origin ?: DoubleVector.ZERO, labelDimension),
                    DoubleRectangle(valueBBox?.origin ?: DoubleVector.ZERO, valueDimension)
                )
            }

            val textSize = components
                .zip(lineBBoxes)
                .fold(DoubleVector.ZERO) { textDimension, (lineInfo, bBoxes) ->
                    val (labelComponent, valueComponent) = lineInfo
                    val (labelBBox, valueBBox) = bBoxes

                    // bBox.top is negative baseline of the text.
                    // Can't use bBox.height:
                    //  - in Batik it is close to the abs(bBox.top)
                    //  - in JavaFx it is constant = fontSize
                    val yPosition = textDimension.y - min(valueBBox.top, labelBBox.top)
                    valueComponent.setY(yPosition)
                    labelComponent?.setY(yPosition)

                    when {
                        labelComponent != null && labelBBox.dimension.x > 0 -> {
                            // Move label to the left border, value - to the right

                            // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                            labelComponent.setX(-labelBBox.left)

                            if (valueComponent.linesCount() > 1) {
                                // Use left alignment
                                valueComponent.setX(maxLabelWidth + TooltipDefaults.LABEL_VALUE_INTERVAL)
                                valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
                            } else {
                                valueComponent.setX(maxLineWidth)
                                valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.RIGHT)
                            }
                        }

                        valueBBox.dimension.x == maxLineWidth && valueComponent.linesCount() == 1 -> {
                            // No label and value's width is equal to the total width => centered
                            // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                            valueComponent.setX(-valueBBox.left)
                        }

                        else -> {
                            // Move value to the center
                            valueComponent.setX(maxLineWidth / 2)
                            valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
                        }
                    }

                    val y = yPosition + max(valueBBox.height, labelBBox.height)
                    myLineYBounds += textDimension.y to y
                    myYPositionsBetweenLines.add(y + TooltipDefaults.LINE_INTERVAL / 2)

                    DoubleVector(
                        x = maxLineWidth,
                        y = y + TooltipDefaults.LINE_INTERVAL
                    )
                }.subtract(DoubleVector(0.0, TooltipDefaults.LINE_INTERVAL))
                .also { myYPositionsBetweenLines.removeLastOrNull() }

            return textSize
        }

        private fun drawLineSeparators(yTitleLinePosition: Double?, yPositionsBetweenLines: List<Double>) {
            fun drawLineSeparator(path: SvgPathElement, toSvgElem: SvgSvgElement) {
                path.strokeWidth().set(TooltipDefaults.LINE_SEPARATOR_WIDTH)
                path.strokeOpacity().set(1.0)
                path.strokeColor().set(Color.gray(80))

                toSvgElem.children().add(path)
            }

            if (yTitleLinePosition != null) {
                val pathData = SvgPathDataBuilder().apply {
                    moveTo(myHorizontalContentPadding, yTitleLinePosition)
                    horizontalLineTo(myTitleContainer.width().get()!! - myHorizontalContentPadding)
                }.build()
                drawLineSeparator(SvgPathElement(pathData), myTitleContainer)
            }

            yPositionsBetweenLines.map { y ->
                SvgPathDataBuilder().apply {
                    with(myContent) {
                        val padding = 2.0
                        moveTo(x().get()!! + padding, y)
                        horizontalLineTo(width().get()!! - myHorizontalContentPadding * 2 - colorBarIndent - padding)
                    }
                }.build()
            }.forEach { pathData -> drawLineSeparator(SvgPathElement(pathData), myLinesContainer) }
        }


        fun drawDebugRect() {
            fun drawRect(rectComponent: RectangleComponent, svgElem: SvgSvgElement, color: Color) {
                rectComponent.update(
                    svgElem.x().get()!!,
                    svgElem.y().get()!!,
                    svgElem.width().get()!!,
                    svgElem.height().get()!!,
                    color
                )
            }

            val rectangles = myDebugRectangles.provide(3)
            drawRect(rectangles[0], myContent, Color.RED)
            drawRect(rectangles[1], myTitleContainer, Color.DARK_GREEN)
            drawRect(rectangles[2], myLinesContainer, Color.ORANGE)
        }
    }

    companion object {
        private const val DEBUG_DRAWING = false

        class RectangleComponent : SvgComponent() {
            private val myRect = SvgPathElement()

            init {
                myRect.strokeWidth().set(1.0)
                myRect.fillOpacity().set(0.0)
            }

            override fun buildComponent() {
                add(myRect)
            }

            fun update(x: Double, y: Double, w: Double, h: Double, color: Color) {
                val pathData = SvgPathDataBuilder().apply {
                    moveTo(x, y)
                    horizontalLineTo(w)
                    verticalLineTo(h)
                    horizontalLineTo(x)
                    verticalLineTo(y)
                }.build()
                myRect.d().set(pathData)
                myRect.strokeColor().set(color)
            }
        }

        private val TRIANGLE_POINTS: List<DoubleVector> = run {
            val size = 8.0
            val height = size + 1.0
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(size / 2, height),
                DoubleVector(-size / 2, height)
            )
        }
    }
}
