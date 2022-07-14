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
        get() = "text {" +
                "font-size: ${FONT_SIZE}px;" +
                "}"

    fun createModel(changeHJust: Boolean, changeVJust: Boolean): GroupComponent {
        val specs = List(11) { it.toDouble() / 10 }.map {
            val hjust = if (changeHJust) it else 0.0
            val vjust = if (changeVJust) it else 1.0
            TextJustification(hjust, vjust)
        }
        val rect = DoubleRectangle(
            DoubleVector(10.0, 10.0),
            DoubleVector(500.0, 80.0)
        )

        val groupComponent = GroupComponent()
        var y = 10.0
        specs.forEach { spec ->
            val labelExample = createLabelExample(rect, spec)
            SvgUtils.transformTranslate(labelExample, 50.0, y)
            groupComponent.add(labelExample)
            y += rect.height + 20.0
        }
        return groupComponent
    }

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(600.0, 1200.0)
        private const val FONT_SIZE = 20.0

        class TextJustification(val x: Double, val y: Double) {
            companion object {
                fun MultilineLabel.applyJustification(
                    boundRect: DoubleRectangle,
                    textSize: DoubleVector,
                    lineHeight: Double, // todo can be specified in element_text
                    justification: TextJustification,
                ) {
                    val (x, hAnchor) = xPosition(boundRect, textSize, justification.x)
                    val y = yPosition(boundRect, textSize, lineHeight, justification.y)

                    val position = DoubleVector(x, y)
                    setLineHeight(lineHeight)
                    setHorizontalAnchor(hAnchor)
                    moveTo(position)
                }

                private fun xPosition(
                    boundRect: DoubleRectangle,
                    textSize: DoubleVector,
                    hjust: Double,
                ): Pair<Double, Text.HorizontalAnchor> {
                    val textWidth = 0.0  // todo textWidth = textSize.x
                    val x = boundRect.left + (boundRect.width - textWidth) * hjust
                    // todo: anchor = LEFT
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
                    return y - textSize.y + lineHeight
                }
            }
        }

        private fun createLabelExample(
            rect: DoubleRectangle,
            justification: TextJustification,
        ): SvgGElement {
            val textLabel = createTextLabel(rect, justification)
            val g = SvgGElement()
            g.children().add(createRect(rect))
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

        private fun createTextLabel(rect: DoubleRectangle, justification: TextJustification): MultilineLabel {
            val text = "Horizontal justification:" + justification.x + "\n" +
                    "Vertical justification:" + justification.y

            val label = MultilineLabel(text)
            label.setX(0.0)
            label.textColor().set(Color.DARK_BLUE)

            // todo textSize
            val textSize = DoubleVector(
                PlotLabelSpec(FONT_SIZE).width(text.length),
                FONT_SIZE * label.linesCount()
            )
            label.applyJustification(
                boundRect = rect,
                textSize = textSize,
                lineHeight = FONT_SIZE,
                justification = justification
            )
            return label
        }
    }
}