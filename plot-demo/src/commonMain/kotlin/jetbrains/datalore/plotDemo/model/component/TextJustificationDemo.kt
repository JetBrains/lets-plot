/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.component.TextJustificationDemo.Companion.TextJustification.Companion.applyJustification
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgUtils

class TextJustificationDemo : SimpleDemoBase(DEMO_BOX_SIZE) {

    override val cssStyle: String
        get() = ".$LABEL_CLASS_NAME { font-size: ${FONT_SIZE}px; }"

    fun createModel(): GroupComponent {
        val specs = List(11) { it.toDouble() / 10 }.map {
            TextJustification(it, it)
        }
        val rect = DoubleRectangle(
            DoubleVector(10.0, 10.0),
            DoubleVector(500.0, 80.0)
        )

        val groupComponent = GroupComponent()

        fun place(angle: Double, startPos: DoubleVector) {
            var y = startPos.x
            var x = startPos.y
            specs.forEach { spec ->
                val labelExample = createLabelExample(rect, spec, angle)
                SvgUtils.transformTranslate(labelExample, x, y)
                groupComponent.add(labelExample)
                if (angle != 0.0) {
                    x += 80.0
                }
                else {
                    y += rect.height + 20.0
                }
            }
        }

        place(angle = 0.0, startPos = DoubleVector(10.0, 10.0))
        place(angle = 90.0, startPos = DoubleVector(10.0, 600.0))
        place(angle = -90.0, startPos = DoubleVector(590.0, 600.0))

        return groupComponent
    }

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(1600.0, 1200.0)
        private const val FONT_SIZE = 20.0
        private const val LABEL_CLASS_NAME = "label"

        class TextJustification(val x: Double, val y: Double) {
            companion object {
                fun applyJustification(
                    boundRect: DoubleRectangle,
                    textSize: DoubleVector,
                    lineHeight: Double, // todo can be specified in element_text
                    justification: TextJustification,
                    angle: Double = 0.0
                ): Pair<DoubleVector, Text.HorizontalAnchor> {
                    require(angle in listOf(0.0, 90.0, -90.0))

                    val rect = if (angle != 0.0) boundRect.flip() else boundRect

                    val (x, hAnchor) = xPosition(rect, textSize, justification.x)
                    val y = yPosition(rect, textSize, lineHeight, justification.y)

                    val position = when {
                        angle == 0.0 -> DoubleVector(x, y)
                        angle < 0.0 -> DoubleVector(y, rect.left + rect.right - x)
                        else -> DoubleVector(rect.top + rect.bottom -  y, x)
                    }
                    return position to hAnchor
                }

                private fun xPosition(
                    boundRect: DoubleRectangle,
                    textSize: DoubleVector,
                    hjust: Double,
                ): Pair<Double, Text.HorizontalAnchor> {
                    val textWidth = 0.0  // todo val textWidth = textSize.x
                    val x = boundRect.left + (boundRect.width - textWidth) * hjust
                    // todo: val anchor = Text.HorizontalAnchor.LEFT
                    val anchor = when {
                        hjust < 0.5 -> Text.HorizontalAnchor.LEFT
                        hjust == 0.5 -> Text.HorizontalAnchor.MIDDLE
                        else -> Text.HorizontalAnchor.RIGHT
                    }
                    return x to anchor
                }

                private fun yPosition(
                    boundRect: DoubleRectangle,
                    textSize: DoubleVector,
                    lineHeight: Double,
                    vjust: Double,
                ): Double {
                    val y = boundRect.bottom - (boundRect.height - textSize.y) * vjust
                    return y - textSize.y + lineHeight * 0.7 // like vertical_anchor = 'top' (dy="0.7em")
                }
            }
        }

        private fun createLabelExample(
            rect: DoubleRectangle,
            justification: TextJustification,
            angle: Double
        ): SvgGElement {
            val r = if (angle != 0.0) rect.flip() else rect
            val textLabel = createTextLabel(r, justification, angle)
            val g = SvgGElement()
            g.children().add(createRect(r))
            g.children().add(textLabel.rootGroup)
            return g
        }

        private fun createRect(r: DoubleRectangle): SvgElement {
            val rect = SvgRectElement(r)
            rect.strokeColor().set(Color.DARK_BLUE)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.0)
            val g = SvgGElement()
            g.children().add(rect)
            return g
        }

        private fun createTextLabel(boundRect: DoubleRectangle, justification: TextJustification, angle: Double): MultilineLabel {
            val text = "Horizontal justification:" + justification.x + "\n" +
                    "Vertical justification:" + justification.y

            val label = MultilineLabel(text)
            label.addClassName(LABEL_CLASS_NAME)
            label.textColor().set(Color.DARK_BLUE)

            // todo textSize
            val lineHeight = FONT_SIZE
            val textSize = DoubleVector(
                PlotLabelSpec(FONT_SIZE).width(text.length),
                lineHeight * label.linesCount()
            )
            val (position, hAnchor) = applyJustification(
                boundRect,
                textSize,
                lineHeight,
                justification,
                angle
            )
            label.setLineHeight(lineHeight)
            label.setHorizontalAnchor(hAnchor)
            label.rotate(angle)
            label.moveTo(position)
            return label
        }
    }
}