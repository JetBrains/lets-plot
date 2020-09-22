/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
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

    internal fun setContent(background: Color, lines: List<TooltipSpec.LabelValue>, style: String) {
        addClassName(style)
        fillColor = Colors.mimicTransparency(background, background.alpha / 255.0, Color.WHITE)
        textColor = LIGHT_TEXT_COLOR.takeIf { fillColor.isDark() } ?: DARK_TEXT_COLOR

        myTextBox.update(lines, labelTextColor = textColor, textColor = textColor)
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

            myPointerPath.strokeColor().set(textColor)
            myPointerPath.fillColor().set(fillColor)

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

        internal fun update(lines: List<TooltipSpec.LabelValue>, labelTextColor: Color, textColor: Color) {
            val valueComponents = lines
                .map { TextLabel(it.value).apply { textColor().set(textColor) } }
                .onEach { myLines.children().add(it.rootGroup) }

            val labelComponents: List<TextLabel?> = lines
                .map(TooltipSpec.LabelValue::label)
                .map { labelString ->
                    if (labelString == null) {
                        null
                    } else {
                        TextLabel(labelString).apply { textColor().set(labelTextColor) }
                    }
                }
                .onEach { textLabel ->
                    if (textLabel != null) {
                        myLines.children().add(textLabel.rootGroup)
                    }
                }

            val maxLabelWidth = labelComponents.filterNotNull().map {
                it.rootGroup.bBox.width
            }.max() ?: 0.0

            var maxLineWidth = 0.0
            valueComponents.forEachIndexed { index, valueTextLabel ->
                val valueBbox = valueTextLabel.rootGroup.bBox
                maxLineWidth = max(
                    maxLineWidth,
                    if (labelComponents[index] == null) {
                        valueBbox.width
                    } else {
                        maxLabelWidth + valueBbox.width + LABEL_VALUE_INTERVAL
                    }
                )
            }

            val textSize = valueComponents
                .foldIndexed(DoubleVector.ZERO, { index, textDimension, valueTextLabel ->
                    val labelTextLabel = labelComponents[index]

                    val valueBbox = valueTextLabel.rootGroup.bBox
                    val labelBBox =
                        labelTextLabel?.rootGroup?.bBox ?: DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)

                    // bBox.top is negative baseline of the text.
                    // Can't use bBox.height:
                    //  - in Batik it is close to the abs(bBox.top)
                    //  - in JavaFx it is constant = fontSize
                    val yPosition = textDimension.y - min(valueBbox.top, labelBBox.top)
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
                        valueBbox.width == maxLineWidth -> {
                            // Again works differently in Batik(some positive padding) and JavaFX (always zero)
                            valueTextLabel.x().set(-valueBbox.left)
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
                            valueBbox.height + valueBbox.top,
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