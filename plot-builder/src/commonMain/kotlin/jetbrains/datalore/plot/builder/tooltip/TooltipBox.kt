/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.COLOR_BAR_STROKE_WIDTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.COLOR_BAR_WIDTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.CONTENT_EXTENDED_PADDING
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.H_CONTENT_PADDING
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.INTERVAL_BETWEEN_SUBSTRINGS
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LABEL_VALUE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LINE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LINE_SEPARATOR_WIDTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MAX_POINTER_FOOTING_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.POINTER_FOOTING_TO_SIDE_LENGTH_RATIO
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.ROTATION_ANGLE
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.VALUE_LINE_MAX_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.V_CONTENT_PADDING
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_LABEL
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_TITLE
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.HORIZONTAL
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.VERTICAL
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.PointerDirection.*
import jetbrains.datalore.vis.svg.*
import kotlin.math.max
import kotlin.math.min

class TooltipBox: SvgComponent() {
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
    val contentRect get() = DoubleRectangle.span(
        DoubleVector.ZERO,
        myContentBox.dimension
    )

    private val myPointerBox = PointerBox()
    private val myContentBox = ContentBox()

    internal val pointerDirection get() = myPointerBox.pointerDirection // for tests
    private var myHorizontalContentPadding = H_CONTENT_PADDING
    private var myVerticalContentPadding = V_CONTENT_PADDING
    private val myYPositionsBetweenLines = mutableListOf<Double>()

    // draw tooltip content rectangles in DEBUG_DRAWING mode
    private val myDebugRectangles = RetainableComponents(
        itemFactory = ::RectangleComponent,
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
        lines: List<TooltipSpec.Line>,
        title: String?,
        textClassName: String,
        rotate: Boolean,
        tooltipMinWidth: Double? = null,
        borderRadius: Double,
        markerColors: List<Color>,
        pointMarkerStrokeColor: Color = borderColor
    ) {
        val totalLines = lines.size + if (title != null) 1 else 0
        myHorizontalContentPadding = if (totalLines > 1) CONTENT_EXTENDED_PADDING else H_CONTENT_PADDING
        myVerticalContentPadding = if (totalLines > 1) CONTENT_EXTENDED_PADDING else V_CONTENT_PADDING
        myYPositionsBetweenLines.clear()

        myContentBox.update(
            lines,
            title,
            textColor,
            tooltipMinWidth,
            rotate,
            markerColors,
            textClassName
        )
        myPointerBox.updateStyle(fillColor, borderColor, strokeWidth, borderRadius, pointMarkerStrokeColor)
    }

    fun setPosition(
        tooltipCoord: DoubleVector,
        pointerCoord: DoubleVector,
        orientation: Orientation,
        rotate: Boolean = false
    ) {
        // Rotate component
        val rotationAngle = if (rotate) ROTATION_ANGLE else 0.0
        rotate(rotationAngle)

       val p = pointerCoord
           .subtract(tooltipCoord)
           .rotate(toRadians(-rotationAngle))   // cancel rotation for pointer point coordinates

        myPointerBox.update(p, orientation, usePointMarker = rotate)
        moveTo(tooltipCoord)

        if (DEBUG_DRAWING) {
            myContentBox.drawDebugRect()
        }
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
            borderRadius: Double,
            pointMarkerStrokeColor: Color
        ) {
            myBorderRadius = borderRadius

            myPointerPath.apply {
                strokeColor().set(borderColor)
                strokeWidth().set(strokeWidth)
                fillColor().set(fillColor)
            }

            myHighlightPoint.apply {
                val fill = if (fillColor == pointMarkerStrokeColor) borderColor else fillColor
                fillColor().set(fill)
                strokeWidth().set(1.0)
                strokeColor().set(pointMarkerStrokeColor)
            }
        }

        fun update(pointerCoord: DoubleVector, orientation: Orientation, usePointMarker: Boolean) {
            pointerDirection = if (usePointMarker) null else when (orientation) {
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

                        fun lineToIf(p: DoubleVector, isTrue: Boolean) { if (isTrue) lineTo(p) }

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

            if (usePointMarker) {
                myHighlightPoint.d().set(trianglePointer(pointerCoord).build())
                SvgUtils.transformRotate(myHighlightPoint, -2*ROTATION_ANGLE, pointerCoord.x, pointerCoord.y)
                myHighlightPoint.visibility().set(SvgGraphicsElement.Visibility.VISIBLE)
            } else {
                myHighlightPoint.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
            }
        }

        private fun calculatePointerFootingIndent(sideLength: Double): Double {
            val footingLength = min(sideLength * POINTER_FOOTING_TO_SIDE_LENGTH_RATIO, MAX_POINTER_FOOTING_LENGTH)
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

        private val myColorBars = List(3) { SvgPathElement() }  // max 3 bars
        private var colorBarIndent = 0.0

        val dimension get() = myContent.run { DoubleVector(width().get()!!, height().get()!!) }

        override fun buildComponent() {
            add(myContent)
            myContent.children().add(myTitleContainer)
            myContent.children().add(myLinesContainer)
            myColorBars.forEach { add(it) }
        }

        fun update(
            lines: List<TooltipSpec.Line>,
            title: String?,
            valueTextColor: Color?,
            tooltipMinWidth: Double?,
            rotate: Boolean,
            markerColors: List<Color>,
            textClassName: String
        ) {
            myLinesContainer.children().clear()
            myTitleContainer.children().clear()

            calculateColorBarIndent(markerColors)

            // title component
            val titleComponent = title?.let(::initTitleComponent)
            val rawTitleBBox = getBBox(titleComponent) ?: DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)

            // detect min tooltip width
            val minWidthWithTitle = listOfNotNull(tooltipMinWidth, rawTitleBBox.width).maxOrNull()

            // lines (label: value)
            val textSize = layoutLines(
                lines,
                valueTextColor,
                minWidthWithTitle,
                rotate,
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
            layoutColorBars(markerColors)

            // draw lines
            drawLineSeparators(
                yTitleLinePosition = if (titleComponent != null) titleTextSize.y - myVerticalContentPadding / 2 else null,
                myYPositionsBetweenLines
            )
        }

        private fun colorBarsWidth(barsNum: Int): List<Double> {
            // make color bar wider if there are more than one
            val middleBarWidth = COLOR_BAR_WIDTH.takeIf { barsNum > 0 } ?: 0.0
            val strokeBarWidth = COLOR_BAR_STROKE_WIDTH.takeIf { barsNum > 1 } ?: 0.0
            return listOf(
                strokeBarWidth,
                middleBarWidth,
                strokeBarWidth
            )
        }

        private fun calculateColorBarIndent(markerColors: List<Color>) {
            colorBarIndent = min(myColorBars.size, markerColors.size).let { colorBarNums ->
                colorBarsWidth(colorBarNums).sum().let { width ->
                    if (width != 0.0) width + myHorizontalContentPadding else 0.0
                }
            }
        }

        private fun layoutColorBars(markerColors: List<Color>) {
            // stroke | fill | stroke
            val fillColor = markerColors.firstOrNull()
            val strokeColor = if (markerColors.size > 1) markerColors[1] else null
            myColorBars
                .zip(listOf(strokeColor, fillColor, strokeColor))
                .forEach { (bar, color) ->
                    if (color == null) {
                        bar.fillOpacity().set(0.0)
                    } else {
                        bar.fillOpacity().set(1.0)
                        bar.fillColor().set(color)
                    }
            }

            var x = contentRect.left + myHorizontalContentPadding
            myColorBars
                .zip(colorBarsWidth(markerColors.size))
                .filter { (bar, _) -> bar.fillOpacity().get()!! > 0 }
                .forEach { (bar, width) ->
                    // adjacent vertical bars
                    bar.d().set(
                        SvgPathDataBuilder().apply {
                            val y = myLinesContainer.y().get()!!
                            val bottom = myLinesContainer.height().get()!!
                            moveTo(x, y)
                            horizontalLineTo(x + width)
                            verticalLineTo(bottom)
                            horizontalLineTo(x)
                            verticalLineTo(y)
                        }.build()
                    )
                    x += width
                }
        }

        private fun getBBox(textLabel: MultilineLabel?): DoubleRectangle? {
            if (textLabel == null || textLabel.text.isBlank()) {
                // also for blank string - Batik throws an exception for a text element with a blank string
                return null
            }
            return textLabel.rootGroup.bBox
        }

        private fun initTitleComponent(titleLine: String): MultilineLabel {
            val titleComponent = MultilineLabel(wrap(titleLine, maxLength = null))
            titleComponent.addClassName(TOOLTIP_TITLE)
            titleComponent.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            val lineHeight = estimateLineHeight(titleLine, TOOLTIP_TITLE) ?: 0.0
            titleComponent.setLineHeight(lineHeight + INTERVAL_BETWEEN_SUBSTRINGS)

            myTitleContainer.children().add(titleComponent.rootGroup)
            return titleComponent
        }

        private fun estimateLineHeight(line: String?, className: String): Double? {
            return line
                ?.split("\n")
                ?.map { MultilineLabel(it).apply { addClassName(className) } }
                ?.mapNotNull { lineTextLabel ->
                    with(myLinesContainer.children()) {
                        add(lineTextLabel.rootGroup)
                        val height = getBBox(lineTextLabel)?.height
                        remove(lineTextLabel.rootGroup)
                        height
                    }
                }
                ?.maxOrNull()
        }

        private fun layoutTitle(
            titleComponent: MultilineLabel?,
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
            lines: List<TooltipSpec.Line>,
            valueTextColor: Color?,
            tooltipMinWidth: Double?,
            rotate: Boolean,
            textClassName: String
        ): DoubleVector {
            // bBoxes
            val components: List<Pair<MultilineLabel?, MultilineLabel>> = lines
                .map { line ->
                    Pair(
                        line.label?.let { MultilineLabel(wrap(it, maxLength = null)) },
                        MultilineLabel(wrap(line.value, maxLength = VALUE_LINE_MAX_LENGTH))
                    )
                }
            // for labels
            components.onEach { (labelComponent, _) ->
                if (labelComponent != null) {
                    labelComponent.addClassName(TOOLTIP_LABEL)
                    myLinesContainer.children().add(labelComponent.rootGroup)
                }
            }
            // for values
            components.onEach { (_, valueComponent) ->
                valueComponent.addClassName(textClassName)
                valueTextColor?.let(valueComponent.textColor()::set)
                myLinesContainer.children().add(valueComponent.rootGroup)
            }

            // calculate heights of original value lines
            val lineHeights = lines.map { line ->
                listOfNotNull(
                    estimateLineHeight(line.label, TOOLTIP_LABEL),
                    estimateLineHeight(line.value, textClassName)
                ).maxOrNull() ?: 0.0
            }

            // sef vertical shifts for tspan elements
            lineHeights.zip(components).onEach { (height, component) ->
                val (labelComponent, valueComponent) = component
                labelComponent?.setLineHeight(height + INTERVAL_BETWEEN_SUBSTRINGS)
                valueComponent.setLineHeight(height + INTERVAL_BETWEEN_SUBSTRINGS)
            }

            val rawBBoxes = components.map { (label, value) -> getBBox(label) to getBBox(value) }

            // max label width - all labels will be aligned to this value
            val maxLabelWidth = rawBBoxes.maxOf { (labelBbox) -> labelBbox?.width ?: 0.0 }

            // max line height - will be used as default height for empty string
            val defaultLineHeight = lineHeights.maxOrNull() ?: 0.0

            val labelWidths = lines.zip(components).map { (line, component) ->
                when {
                    line.label == null -> {
                        // label null - the value component will be centered
                        0.0
                    }
                    line.label!!.isEmpty() && component.second.linesCount() == 1 -> {
                        // label is not null, but empty - add space for the label, the value will be moved to the right;
                        // also value should not be multiline for right alignment
                        maxLabelWidth
                    }
                    else -> {
                        // align the label width to the maximum and add interval between label and value
                        maxLabelWidth + LABEL_VALUE_INTERVAL
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
                                valueComponent.setX(maxLabelWidth + LABEL_VALUE_INTERVAL)
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
                    myYPositionsBetweenLines.add(y + LINE_INTERVAL / 2)

                    DoubleVector(
                        x = maxLineWidth,
                        y = y + LINE_INTERVAL
                    )
                }.subtract(DoubleVector(0.0, LINE_INTERVAL)) // remove LINE_INTERVAL from last line
                .also { myYPositionsBetweenLines.removeLastOrNull() }

            return textSize
        }

        private fun drawLineSeparators(yTitleLinePosition: Double?, yPositionsBetweenLines: List<Double>) {
            fun drawLineSeparator(path: SvgPathElement, toSvgElem: SvgSvgElement) {
                path.strokeWidth().set(LINE_SEPARATOR_WIDTH)
                path.strokeOpacity().set(1.0)
                path.strokeColor().set(Color.VERY_LIGHT_GRAY)

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

        private fun wrap(value: String, maxLength: Int?) =
            MultilineLabel.splitLines(value).flatMap { line ->
                if (maxLength != null) {
                    line.chunkedBy(delimiter = " ", maxLength)
                } else {
                    listOf(line)
                }
            }.joinToString("\n")

        private fun String.chunkedBy(delimiter: String, maxLength: Int): List<String> {
            return split(delimiter)
                .chunkedBy(maxLength + delimiter.length) { length + delimiter.length }
                .map { it.joinToString(delimiter) }
        }

        private fun List<String>.chunkedBy(maxSize: Int, size: String.() -> Int): List<List<String>> {
            val result = mutableListOf<List<String>>()
            var subList = mutableListOf<String>()
            var subListSize = 0
            forEach { item ->
                val itemSize = item.size()
                if (subListSize + itemSize > maxSize && subList.isNotEmpty()) {
                    result.add(subList)
                    subList = mutableListOf()
                    subListSize = 0
                }
                subList.add(item)
                subListSize += itemSize
            }
            if (subList.isNotEmpty()) {
                result += subList
            }
            return result
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