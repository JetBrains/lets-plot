/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.base.values.Colors.darker
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DARK_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.H_CONTENT_PADDING
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LABEL_VALUE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LINE_INTERVAL
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MAX_POINTER_FOOTING_LENGTH
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.POINTER_FOOTING_TO_SIDE_LENGTH_RATIO
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.V_CONTENT_PADDING
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.HORIZONTAL
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.VERTICAL
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.PointerDirection.*
import jetbrains.datalore.vis.svg.SvgGraphicsElement.Visibility.HIDDEN
import jetbrains.datalore.vis.svg.SvgGraphicsElement.Visibility.VISIBLE
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import jetbrains.datalore.vis.svg.SvgPathElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import kotlin.math.max
import kotlin.math.min

class TooltipBox : SvgComponent() {
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

    val contentRect get() = DoubleRectangle.span(DoubleVector.ZERO, myTextBox.dimension)
    var visible: Boolean
        get() = rootGroup.visibility().get() == VISIBLE
        set(isVisible) { rootGroup.visibility().set(VISIBLE.takeIf { isVisible } ?: HIDDEN) }

    private val myPointerBox = PointerBox()
    private val myTextBox = TextBox()

    private var textColor: Color = Color.BLACK
    private var fillColor: Color = Color.WHITE
    internal val pointerDirection get() = myPointerBox.pointerDirection // for tests

    override fun buildComponent() {
        add(myPointerBox)
        add(myTextBox)
    }

    internal fun setContent(color: Color, lines: List<TooltipSpec.Line>, style: String, isOutlier: Boolean) {
        addClassName(style)
        if (isOutlier) {
            fillColor = Colors.mimicTransparency(color, color.alpha / 255.0, Color.WHITE)
            textColor = LIGHT_TEXT_COLOR.takeIf { fillColor.isDark() } ?: DARK_TEXT_COLOR
        } else {
            fillColor = Color.WHITE
            textColor = color.takeIf { color.isDark() } ?: darker(color) ?: DARK_TEXT_COLOR
        }
        myTextBox.update(lines, labelTextColor = DARK_TEXT_COLOR, valueTextColor = textColor)
    }

    internal fun setPosition(tooltipCoord: DoubleVector, pointerCoord: DoubleVector, orientation: Orientation) {
        myPointerBox.update(pointerCoord.subtract(tooltipCoord), orientation)
        moveTo(tooltipCoord.x, tooltipCoord.y)
    }

    private fun Color.isDark() = Colors.luminance(this) < 0.5

    private inner class PointerBox : SvgComponent() {
        private val myPointerPath = SvgPathElement()
        internal var pointerDirection: PointerDirection? = null

        override fun buildComponent() {
            add(myPointerPath)
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

            myPointerPath.apply {
                strokeColor().set(textColor)
                strokeOpacity().set(1.0)
                fillColor().set(fillColor)
            }

            val vertFootingIndent = -calculatePointerFootingIndent(contentRect.height)
            val horFootingIndent = calculatePointerFootingIndent(contentRect.width)

            myPointerPath.d().set(
                SvgPathDataBuilder().apply {
                    with(contentRect) {

                        fun lineToIf(p: DoubleVector, isTrue: Boolean) { if (isTrue) lineTo(p) }

                        // start point
                        moveTo(right, bottom)

                        // right side
                        lineTo(right, bottom + vertFootingIndent)
                        lineToIf(pointerCoord, pointerDirection == RIGHT)
                        lineTo(right, top - vertFootingIndent)
                        lineTo(right, top)

                        // top side
                        lineTo(right - horFootingIndent, top)
                        lineToIf (pointerCoord, pointerDirection == UP)
                        lineTo(left + horFootingIndent, top)
                        lineTo(left, top)

                        // left side
                        lineTo(left, top - vertFootingIndent)
                        lineToIf (pointerCoord, pointerDirection == LEFT)
                        lineTo(left, bottom + vertFootingIndent)
                        lineTo(left, bottom)

                        // bottom
                        lineTo(left + horFootingIndent, bottom)
                        lineToIf (pointerCoord, pointerDirection == DOWN)
                        lineTo(right - horFootingIndent, bottom)
                        lineTo(right, bottom)
                    }
                }.build()
            )
        }

        private fun calculatePointerFootingIndent(sideLength: Double): Double {
            val footingLength = min(sideLength * POINTER_FOOTING_TO_SIDE_LENGTH_RATIO, MAX_POINTER_FOOTING_LENGTH)
            return (sideLength - footingLength) / 2
        }
    }

    private inner class TextBox : SvgComponent() {
        private val myLines = SvgSvgElement().apply {
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
            myContent.children().add(myLines)
            add(myContent)
        }

        internal fun update(lines: List<TooltipSpec.Line>, labelTextColor: Color, valueTextColor: Color) {
            val valueComponents: List<TextLabel> = lines
                .map(TooltipSpec.Line::value)
                .map { TextLabel(it) }

            val labelInfoComponents: List<Pair<String?, TextLabel?>> = lines
                .map(TooltipSpec.Line::label)
                .map { label ->
                    // TextLabel is null for both situation: string is null or empty
                    val textLabel = if (label.isNullOrEmpty()) null else TextLabel(label)
                    label to textLabel
                }

            valueComponents
                .onEach {
                    it.textColor().set(valueTextColor)
                    myLines.children().add(it.rootGroup)
                }

            labelInfoComponents
                .mapNotNull { it.second }
                .onEach {
                    it.textColor().set(labelTextColor)
                    myLines.children().add(it.rootGroup)
                }

            val maxLabelWidth =
                labelInfoComponents.mapNotNull { it.second }.map { it.rootGroup.bBox.width }.max() ?: 0.0

            val lineComponents = valueComponents.zip(labelInfoComponents)

            var maxLineWidth = 0.0
            lineComponents.forEach { line ->
                val valueBBox = line.first.rootGroup.bBox
                val labelTextLabel = line.second.second
                maxLineWidth = max(
                    maxLineWidth,
                    if (labelTextLabel == null) {
                        valueBBox.width
                    } else {
                        maxLabelWidth + valueBBox.width + LABEL_VALUE_INTERVAL
                    }
                )
            }

            val textSize = lineComponents
                .fold(DoubleVector.ZERO, { textDimension, line ->
                    val valueTextLabel = line.first
                    val labelTextLabel = line.second.second
                    val labelString = line.second.first

                    val valueBBox = valueTextLabel.rootGroup.bBox
                    val labelBBox =
                        labelTextLabel?.rootGroup?.bBox ?: DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)

                    // bBox.top is negative baseline of the text.
                    // Can't use bBox.height:
                    //  - in Batik it is close to the abs(bBox.top)
                    //  - in JavaFx it is constant = fontSize
                    val yPosition = textDimension.y - min(valueBBox.top, labelBBox.top)
                    valueTextLabel.y().set(yPosition)
                    labelTextLabel?.y()?.set(yPosition)

                    when {
                        labelTextLabel != null -> {
                            // Move label to the left border, value - to the right

                            // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                            labelTextLabel.x().set(-labelBBox.left)

                            valueTextLabel.x().set(maxLineWidth)
                            valueTextLabel.setHorizontalAnchor(TextLabel.HorizontalAnchor.RIGHT)
                        }
                        valueBBox.width == maxLineWidth -> {
                            // No label and value's width is equal to the total width => centered
                            // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                            valueTextLabel.x().set(-valueBBox.left)
                        }
                        labelString == "" -> {
                            // Move value to the right border
                            valueTextLabel.x().set(maxLineWidth)
                            valueTextLabel.setHorizontalAnchor(TextLabel.HorizontalAnchor.RIGHT)
                        }
                        else -> {
                            // Move value to the center
                            valueTextLabel.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
                            valueTextLabel.x().set(maxLineWidth / 2)
                        }
                    }

                    DoubleVector(
                        x = maxLineWidth,
                        y = valueTextLabel.y().get()!! + max(
                            valueBBox.height + valueBBox.top,
                            labelBBox.height + labelBBox.top
                        ) + LINE_INTERVAL
                    )
                })
                .subtract(DoubleVector(0.0, LINE_INTERVAL)) // remove LINE_INTERVAL from last line

            myLines.apply {
                x().set(H_CONTENT_PADDING)
                y().set(V_CONTENT_PADDING)
                width().set(textSize.x)
                height().set(textSize.y)
            }

            myContent.apply {
                width().set(textSize.x + H_CONTENT_PADDING * 2)
                height().set(textSize.y + V_CONTENT_PADDING * 2)
            }
        }
    }
}