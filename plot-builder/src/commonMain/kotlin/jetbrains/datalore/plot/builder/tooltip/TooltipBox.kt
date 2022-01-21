/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.COLOR_BAR_WIDTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DARK_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DATA_TOOLTIP_FONT_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.H_CONTENT_PADDING
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LABEL_VALUE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LINE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MAX_VALUE_LINE_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MAX_POINTER_FOOTING_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.POINTER_FOOTING_TO_SIDE_LENGTH_RATIO
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
        myTextBox.dimension.add(DoubleVector(additionalIndentInContentRect, 0.0))
    )

    private val myPointerBox = PointerBox()
    private val myTextBox = TextBox()
    internal val pointerDirection get() = myPointerBox.pointerDirection // for tests
    private val additionalIndentInContentRect get() = if (myPointerBox.addColorBar) {
        H_CONTENT_PADDING + COLOR_BAR_WIDTH
    } else {
        0.0
    }

    override fun buildComponent() {
        add(myPointerBox)
        add(myTextBox)
    }

    fun update(
        fillColor: Color,
        textColor: Color,
        borderColor: Color,
        markerFillColor: Color?, // todo add also the border color for the marker
        strokeWidth: Double,
        lines: List<TooltipSpec.Line>,
        style: String,
        rotate: Boolean,
        tooltipMinWidth: Double? = null,
        borderRadius: Double
    ) {
        addClassName(style)
        myTextBox.update(
            lines,
            labelTextColor = DARK_TEXT_COLOR,
            valueTextColor = textColor,
            tooltipMinWidth,
            rotate
        )
        myPointerBox.updateStyle(fillColor, borderColor, markerFillColor, strokeWidth, borderRadius)
    }

    internal fun setPosition(tooltipCoord: DoubleVector, pointerCoord: DoubleVector, orientation: Orientation) {
        myPointerBox.update(pointerCoord.subtract(tooltipCoord), orientation)
        moveTo(tooltipCoord.x, tooltipCoord.y)
        myTextBox.moveTo(additionalIndentInContentRect, 0.0)
    }

    private inner class PointerBox : SvgComponent() {
        private val myPointerPath = SvgPathElement()
        private val myColorBar = SvgPathElement()
        internal var pointerDirection: PointerDirection? = null
        internal var addColorBar = false
        private var myBorderRadius = 0.0

        override fun buildComponent() {
            add(myPointerPath)
            add(myColorBar)
        }

        internal fun updateStyle(
            fillColor: Color,
            borderColor: Color,
            markerFillColor: Color?,
            strokeWidth: Double,
            borderRadius: Double
        ) {
            addColorBar = markerFillColor != null
            myBorderRadius = borderRadius

            myPointerPath.apply {
                strokeColor().set(borderColor)
                strokeOpacity().set(strokeWidth)
                fillColor().set(fillColor)
            }
            myColorBar.apply {
                if (markerFillColor != null) {
                    strokeOpacity().set(1.0)
                    strokeColor().set(markerFillColor)
                    strokeWidth().set(COLOR_BAR_WIDTH)
                } else {
                    strokeOpacity().set(0.0)
                }
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
            if (addColorBar) {
                myColorBar.d().set(
                    SvgPathDataBuilder().apply {
                        with(contentRect) {
                            val x = left + (H_CONTENT_PADDING + COLOR_BAR_WIDTH / 2)
                            moveTo(x, bottom - V_CONTENT_PADDING)
                            lineTo(x, top + V_CONTENT_PADDING)
                        }
                    }.build()
                )
            }
        }

        private fun calculatePointerFootingIndent(sideLength: Double): Double {
            val footingLength = min(sideLength * POINTER_FOOTING_TO_SIDE_LENGTH_RATIO, MAX_POINTER_FOOTING_LENGTH)
            return (sideLength - footingLength) / 2
        }
    }

    private inner class TextBox : SvgComponent() {
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

        val dimension get() = myContent.run { DoubleVector(width().get()!!, height().get()!!) }

        override fun buildComponent() {
            add(myContent)
            myContent.children().add(myLinesContainer)
        }

        internal fun update(
            lines: List<TooltipSpec.Line>,
            labelTextColor: Color,
            valueTextColor: Color,
            tooltipMinWidth: Double?,
            rotate: Boolean
        ) {
            myLinesContainer.children().clear()

            // bBoxes
            fun getBBox(text: String?, textLabel: TextLabel?): DoubleRectangle? {
                if (textLabel == null || text.isNullOrBlank()) {
                    // also for blank string - Batik throws an exception for a text element with a blank string
                    return null
                }
                return textLabel.rootGroup.bBox
            }

            val components: List<Pair<TextLabel?, TextLabel>> = lines.map { line ->
                Pair(
                    line.label?.let(::TextLabel),
                    line.value.let { value ->
                        val chunked = value.chunkedBy(delimiter = " ", MAX_VALUE_LINE_LENGTH)
                        if (chunked.size == 1) {
                            TextLabel(chunked.single())
                        } else {
                            TextLabel(chunked)
                        }
                    }
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
                valueComponent.setTSpanX(0.0)
                valueComponent.setTSpanDY(0.0)
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
                valueComponent.setTSpanDY(height + LINE_INTERVAL)
            }

            // bBoxes
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

            // in case of multilines: increase the interval between text label and use left alignment
            val hasMultiLines = lines.any { it.value.length > MAX_VALUE_LINE_LENGTH }
            val textInterval = if (hasMultiLines) 4 * LINE_INTERVAL else LINE_INTERVAL

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

                            if (hasMultiLines) {
                                // Use left alignment
                                valueComponent.x().set(maxLabelWidth + LABEL_VALUE_INTERVAL)
                                valueComponent.setHorizontalAnchor(TextLabel.HorizontalAnchor.LEFT)
                                valueComponent.setTSpanX(maxLabelWidth + LABEL_VALUE_INTERVAL)
                            } else {
                                valueComponent.x().set(maxLineWidth)
                                valueComponent.setHorizontalAnchor(TextLabel.HorizontalAnchor.RIGHT)
                            }
                        }
                        valueBBox.dimension.x == maxLineWidth -> {
                            // No label and value's width is equal to the total width => centered
                            // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                            valueComponent.x().set(-valueBBox.left)
                        }
                        else -> {
                            // Move value to the center
                            valueComponent.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
                            valueComponent.x().set(maxLineWidth / 2)
                            valueComponent.setTSpanX(maxLineWidth / 2)
                        }
                    }
                    DoubleVector(
                        x = maxLineWidth,
                        y = valueComponent.y().get()!! + max(
                            valueBBox.height,
                            labelBBox.height
                        ) + textInterval
                    )
                }).subtract(DoubleVector(0.0, textInterval)) // remove LINE_INTERVAL from last line
                .let { textSize ->
                    if (rotate) {
                        components
                            .onEach { (labelComponent, valueComponent) ->
                                labelComponent?.y()?.set(-labelComponent.y().get()!!)
                                labelComponent?.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
                                labelComponent?.rotate(90.0)

                                valueComponent.y().set(-valueComponent.y().get()!!)
                                valueComponent.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
                                valueComponent.rotate(90.0)
                            }
                        textSize.flip()
                    } else {
                        textSize
                    }
                }

            myLinesContainer.apply {
                x().set(if (rotate) 0.0 else H_CONTENT_PADDING)
                y().set(V_CONTENT_PADDING)
                width().set(textSize.x + H_CONTENT_PADDING * 2)
                height().set(textSize.y)
            }

            myContent.apply {
                width().set(textSize.x + H_CONTENT_PADDING * 2)
                height().set(textSize.y + V_CONTENT_PADDING * 2)
            }
        }
    }

    companion object {

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
    }
}