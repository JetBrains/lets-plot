/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.COLOR_BARS_MARGIN
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.COLOR_BAR_WIDTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.CONTENT_EXTENDED_PADDING
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DARK_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DATA_TOOLTIP_FONT_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.H_CONTENT_PADDING
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.INTERVAL_BETWEEN_SUBSTRINGS
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LABEL_VALUE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LINE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LINE_SEPARATOR_WIDTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MAX_POINTER_FOOTING_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.POINTER_FOOTING_TO_SIDE_LENGTH_RATIO
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.VALUE_LINE_MAX_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.V_CONTENT_PADDING
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.HORIZONTAL
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.VERTICAL
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.PointerDirection.*
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import jetbrains.datalore.vis.svg.SvgPathElement
import jetbrains.datalore.vis.svg.SvgSvgElement
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

    override fun buildComponent() {
        add(myPointerBox)
        add(myContentBox)
    }

    fun update(
        fillColor: Color,
        textColor: Color,
        borderColor: Color,
        strokeWidth: Double,
        lines: List<TooltipSpec.Line>,
        style: String,
        rotate: Boolean,
        tooltipMinWidth: Double? = null,
        borderRadius: Double,
        markerColors: List<Color>
    ) {
        addClassName(style)
        myHorizontalContentPadding = if (lines.size > 1) CONTENT_EXTENDED_PADDING else H_CONTENT_PADDING
        myVerticalContentPadding = if (lines.size > 1) CONTENT_EXTENDED_PADDING else V_CONTENT_PADDING

        myContentBox.update(
            lines,
            labelTextColor = DARK_TEXT_COLOR,
            valueTextColor = textColor,
            tooltipMinWidth,
            rotate,
            markerColors
        )
        myPointerBox.updateStyle(fillColor, borderColor, strokeWidth, borderRadius)
    }

    internal fun setPosition(tooltipCoord: DoubleVector, pointerCoord: DoubleVector, orientation: Orientation) {
        myPointerBox.update(pointerCoord.subtract(tooltipCoord), orientation)
        moveTo(tooltipCoord.x, tooltipCoord.y)
    }

    private inner class PointerBox : SvgComponent() {
        private val myPointerPath = SvgPathElement()
        internal var pointerDirection: PointerDirection? = null
        private var myBorderRadius = 0.0

        override fun buildComponent() {
            add(myPointerPath)
        }

        internal fun updateStyle(
            fillColor: Color,
            borderColor: Color,
            strokeWidth: Double,
            borderRadius: Double
        ) {
            myBorderRadius = borderRadius

            myPointerPath.apply {
                strokeColor().set(borderColor)
                strokeOpacity().set(strokeWidth)
                fillColor().set(fillColor)
            }
        }

        internal fun update(pointerCoord: DoubleVector, orientation: Orientation) {
            pointerDirection = when (orientation) {
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
                            curveTo(controlStart, controlEnd, to)
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
        }

        private fun calculatePointerFootingIndent(sideLength: Double): Double {
            val footingLength = min(sideLength * POINTER_FOOTING_TO_SIDE_LENGTH_RATIO, MAX_POINTER_FOOTING_LENGTH)
            return (sideLength - footingLength) / 2
        }
    }

    private inner class ContentBox : SvgComponent() {
        private val myTitlesContainer = SvgSvgElement().apply {
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

        private val myColorBars = List(2) { SvgPathElement() }  // max two bars
        private var colorBarIndent = 0.0

        val dimension get() = myContent.run { DoubleVector(width().get()!!, height().get()!!) }

        override fun buildComponent() {
            add(myContent)
            myContent.children().add(myTitlesContainer)
            myContent.children().add(myLinesContainer)
            myColorBars.forEach { add(it) }
        }

        internal fun update(
            lines: List<TooltipSpec.Line>,
            labelTextColor: Color,
            valueTextColor: Color,
            tooltipMinWidth: Double?,
            rotate: Boolean,
            markerColors: List<Color>
        ) {
            myLinesContainer.children().clear()
            myTitlesContainer.children().clear()

            calculateColorBarIndent(markerColors)

            val titleLines = lines.filter(TooltipSpec.Line::isTitle).map(TooltipSpec.Line::value)
            val labelValueLines = lines.filterNot(TooltipSpec.Line::isTitle)

            // title components
            val titleComponents = initTitleComponents(titleLines, valueTextColor)
            val rawTitleBBoxes = titleLines.zip(titleComponents).map { (titleLine, component) ->
                getBBox(titleLine, component)
            }
             val titleHeights = rawTitleBBoxes.map { bBox ->
                bBox?.height ?: DATA_TOOLTIP_FONT_SIZE.toDouble()
            }

            // detect min tooltip width
            val maxTitleWidth = rawTitleBBoxes.map { bBox -> bBox?.width ?: 0.0 }.maxOrNull()
            val minWidthWithTitle = listOfNotNull(tooltipMinWidth, maxTitleWidth).maxOrNull()

            // lines (label: value)
            val textSize = placeLines(
                labelValueLines,
                labelTextColor,
                valueTextColor,
                minWidthWithTitle,
                rotate
            )

            val totalTooltipWidth = textSize.x + colorBarIndent + myHorizontalContentPadding * 2

            // titles
            val titleTextSize = placeTitles(
                titleComponents,
                totalTooltipWidth,
                titleHeights
            )

            // container sizes

            myTitlesContainer.apply {
                if (titleComponents.isNotEmpty()) {
                    x().set(0.0)
                    y().set(myVerticalContentPadding)
                    width().set(totalTooltipWidth)
                    height().set(titleTextSize.y)
                }
            }

            myLinesContainer.apply {
                x().set(if (rotate) 0.0 else myHorizontalContentPadding + colorBarIndent)
                y().set(titleTextSize.y + myVerticalContentPadding)
                width().set(totalTooltipWidth - myHorizontalContentPadding)
                height().set(textSize.y + titleTextSize.y + myVerticalContentPadding )
            }

            myContent.apply {
                width().set(totalTooltipWidth)
                height().set(textSize.y + titleTextSize.y + myVerticalContentPadding * 2)
            }

            // draw color bars
            placeColorBars(markerColors)
        }

        private fun colorBarWidth(barsNum: Int): Double {
            // make color bars wider if there are more than one
            return COLOR_BAR_WIDTH * if (barsNum > 1) 1.4 else barsNum.toDouble()
        }

        private fun calculateColorBarIndent(markerColors: List<Color>) {
            colorBarIndent = min(myColorBars.size, markerColors.size).let { colorBarNums ->
                if (colorBarNums > 0) {
                    myHorizontalContentPadding +
                            colorBarNums * colorBarWidth(colorBarNums) +
                            (colorBarNums - 1) * COLOR_BARS_MARGIN
                } else {
                    0.0
                }
            }
        }

        private fun placeColorBars(markerColors: List<Color>) {
            myColorBars.forEachIndexed { index, bar ->
                if (markerColors.size > index) {
                    bar.fillOpacity().set(1.0)
                    bar.fillColor().set(markerColors[index])
                } else {
                    bar.fillOpacity().set(0.0)
                }
            }

            val colorBars = myColorBars.filter { it.fillOpacity().get()!! > 0 }
            val barWidth = colorBarWidth(colorBars.size)
            colorBars
                .forEachIndexed { index, bar ->
                    // adjacent vertical bars
                    bar.d().set(
                        SvgPathDataBuilder().apply {
                            val x = contentRect.left + myHorizontalContentPadding +
                                    index * (barWidth + COLOR_BARS_MARGIN)
                            val y = myLinesContainer.y().get()!!
                            val bottom = myLinesContainer.height().get()!!

                            moveTo(x, y)
                            horizontalLineTo(x + barWidth)
                            verticalLineTo(bottom)
                            horizontalLineTo(x)
                            verticalLineTo(y)
                        }.build()
                    )
                }
        }

        private fun getBBox(text: String?, textLabel: SvgComponent?): DoubleRectangle? {
            if (textLabel == null || text.isNullOrBlank()) {
                // also for blank string - Batik throws an exception for a text element with a blank string
                return null
            }
            return textLabel.rootGroup.bBox
        }

        private fun initTitleComponents(
            titleLines: List<String>,
            titleColor: Color
        ) : List<MultilineLabel> {
            val titleComponents = titleLines.map { titleLine ->
                MultilineLabel(titleLine, VALUE_LINE_MAX_LENGTH)
            }

            titleComponents.onEach { component ->
                component.textColor().set(titleColor)
                component.setX(0.0)
                component.setFontWeight("bold")
                component.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
                myTitlesContainer.children().add(component.rootGroup)
            }

            // set interval between substrings
            val titleLineHeights = titleLines.map { titleLine ->
                val lineTextLabel = TextLabel(titleLine)
                with (myTitlesContainer.children()) {
                    add(lineTextLabel.rootGroup)
                    val height = getBBox(titleLine, lineTextLabel)?.height ?: DATA_TOOLTIP_FONT_SIZE.toDouble()
                    remove(lineTextLabel.rootGroup)
                    return@map height
                }
            }
            titleLineHeights.zip(titleComponents).onEach { (height, component) ->
                component.setLineVerticalMargin(height + INTERVAL_BETWEEN_SUBSTRINGS)
            }

            return titleComponents
        }

        private fun placeTitles(
            titleComponents: List<MultilineLabel>,
            totalTooltipWidth: Double,
            lineHeights: List<Double>
        ): DoubleVector {
            if (titleComponents.isEmpty()) {
                return DoubleVector.ZERO
            }

            val titleSize =  titleComponents
                .zip(lineHeights)
                .fold(DoubleVector(0.0, myVerticalContentPadding), { textDimension, (component, height) ->
                    val yPosition = textDimension.y
                    component.y().set(yPosition)
                    component.setX(totalTooltipWidth / 2)

                    DoubleVector(
                        x = totalTooltipWidth,
                        y = yPosition + height + LINE_INTERVAL
                    )
                }).subtract(DoubleVector(0.0, LINE_INTERVAL)) // remove LINE_INTERVAL from last line

            // add line separator
            val pathData = SvgPathDataBuilder().apply {
                val y = titleSize.y - myVerticalContentPadding / 2
                moveTo(myHorizontalContentPadding, y)
                lineTo(totalTooltipWidth - myHorizontalContentPadding, y)
            }.build()
            drawLineSeparator(SvgPathElement(pathData), myTitlesContainer)

            return titleSize
        }

        private fun placeLines(
            lines: List<TooltipSpec.Line>,
            labelTextColor: Color,
            valueTextColor: Color,
            tooltipMinWidth: Double?,
            rotate: Boolean
        ): DoubleVector {
            // bBoxes
            val components: List<Pair<TextLabel?, MultilineLabel>> = lines
                .map { line ->
                    Pair(
                        line.label?.let(::TextLabel),
                        MultilineLabel(line.value, VALUE_LINE_MAX_LENGTH)
                    )
                }
            // for labels
            components.onEach { (labelComponent, _) ->
                if (labelComponent != null) {
                    labelComponent.textColor().set(labelTextColor)
                    labelComponent.setFontWeight("bold")
                    myLinesContainer.children().add(labelComponent.rootGroup)
                }
            }
            // for values
            components.onEach { (_, valueComponent) ->
                valueComponent.textColor().set(valueTextColor)
                valueComponent.setX(0.0)
                myLinesContainer.children().add(valueComponent.rootGroup)
            }

            // calculate heights of original value lines
            val valueLineHeights = lines.map { line ->
                val lineTextLabel = TextLabel(line.value)
                with (myLinesContainer.children()) {
                    add(lineTextLabel.rootGroup)
                    val height = getBBox(line.value, lineTextLabel)?.height ?: DATA_TOOLTIP_FONT_SIZE.toDouble()
                    remove(lineTextLabel.rootGroup)
                    return@map height
                }
            }
            // sef vertical shifts for tspan elements
            valueLineHeights.zip(components).onEach { (height, component) ->
                val (_, valueComponent) = component
                valueComponent.setLineVerticalMargin(height + INTERVAL_BETWEEN_SUBSTRINGS)
            }

            val rawBBoxes = lines.zip(components).map { (line, component) ->
                val (labelComponent, valueComponent) = component
                Pair(
                    getBBox(line.label, labelComponent),
                    getBBox(line.value, valueComponent)
                )
            }

            // max label width - all labels will be aligned to this value
            val maxLabelWidth = rawBBoxes.maxOf { (labelBbox) -> labelBbox?.width ?: 0.0 }

            // max line height - will be used as default height for empty string
            val defaultLineHeight = (valueLineHeights + rawBBoxes.mapNotNull { it.first?.height }).maxOrNull()
                ?: DATA_TOOLTIP_FONT_SIZE.toDouble()

            val labelWidths = lines.map { line ->
                when {
                    line.label == null -> {
                        // label null - the value component will be centered
                        0.0
                    }
                    line.label!!.isEmpty() -> {
                        // label is not null, but empty - add space for the label, the value will be moved to the right
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

            val yPositionsBetweenLines = mutableListOf<Double>()

            val textSize = components
                .zip(lineBBoxes)
                .fold(DoubleVector.ZERO, { textDimension, (lineInfo, bBoxes) ->
                    val (labelComponent, valueComponent) = lineInfo
                    val (labelBBox, valueBBox) = bBoxes

                    // bBox.top is negative baseline of the text.
                    // Can't use bBox.height:
                    //  - in Batik it is close to the abs(bBox.top)
                    //  - in JavaFx it is constant = fontSize
                    val yPosition = textDimension.y - min(valueBBox.top, labelBBox.top)
                    valueComponent.y().set(yPosition)
                    labelComponent?.y()?.set(yPosition)

                    when {
                        labelComponent != null && labelBBox.dimension.x > 0 -> {
                            // Move label to the left border, value - to the right

                            // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                            labelComponent.x().set(-labelBBox.left)

                            if (valueComponent.containsSubtext()) {
                                // Use left alignment
                                valueComponent.setX(maxLabelWidth + LABEL_VALUE_INTERVAL)
                                valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
                            } else {
                                valueComponent.setX(maxLineWidth)
                                valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.RIGHT)
                            }
                        }
                        valueBBox.dimension.x == maxLineWidth -> {
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

                    val y = valueComponent.y().get()!! + max(valueBBox.height, labelBBox.height)
                    yPositionsBetweenLines.add(y + LINE_INTERVAL / 2)

                    DoubleVector(
                        x = maxLineWidth,
                        y = y + LINE_INTERVAL
                    )
                }).subtract(DoubleVector(0.0, LINE_INTERVAL)) // remove LINE_INTERVAL from last line
                .let { textSize ->
                    if (rotate) {
                        components
                            .onEach { (labelComponent, valueComponent) ->
                                labelComponent?.y()?.set(-labelComponent.y().get()!!)
                                labelComponent?.setVerticalAnchor(Text.VerticalAnchor.CENTER)
                                labelComponent?.rotate(90.0)

                                valueComponent.y().set(-valueComponent.y().get()!!)
                                valueComponent.setVerticalAnchor(Text.VerticalAnchor.CENTER)
                                valueComponent.rotate(90.0)
                            }
                        textSize.flip()
                    } else {
                        textSize
                    }
                }

            yPositionsBetweenLines.dropLast(1).map { y ->
                SvgPathDataBuilder().apply {
                    with(myContent) {
                        val padding = 2.0
                        moveTo(x().get()!! + padding, y)
                        lineTo(width().get()!! - myHorizontalContentPadding * 2 - colorBarIndent - padding, y)
                    }
                }.build()
            }.forEach { pathData -> drawLineSeparator(SvgPathElement(pathData), myLinesContainer) }

            return textSize
        }

        private fun drawLineSeparator(path: SvgPathElement, toSvgElem: SvgSvgElement) {
            path.strokeWidth().set(LINE_SEPARATOR_WIDTH);
            path.strokeOpacity().set(1.0)
            path.strokeColor().set(Color.VERY_LIGHT_GRAY)

            toSvgElem.children().add(path)
        }

        fun drawDebugRect() {
            fun drawRect(svgElem: SvgSvgElement, color: Color) {
                val r = SvgPathDataBuilder().apply {
                    with(svgElem) {
                        moveTo(x().get()!!, y().get()!!)
                        horizontalLineTo(width().get()!!)
                        verticalLineTo(height().get()!!)
                        horizontalLineTo(x().get()!!)
                        verticalLineTo(y().get()!!)
                    }
                }.build()
                val path = SvgPathElement(r)
                path.strokeWidth().set(1.0);
                path.fillOpacity().set(0.0)
                path.strokeColor().set(color)
                add(path)
            }

            drawRect(myContent, Color.RED)
            drawRect(myTitlesContainer, Color.DARK_GREEN)
            drawRect(myLinesContainer, Color.ORANGE)
        }
    }
}