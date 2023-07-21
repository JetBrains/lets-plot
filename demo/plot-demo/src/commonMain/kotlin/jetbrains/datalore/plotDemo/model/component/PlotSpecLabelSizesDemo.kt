/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.svg.TextLabel
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec
import org.jetbrains.letsPlot.core.plot.builder.presentation.PlotLabelSpec
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

class PlotSpecLabelSizesDemo : SimpleDemoBase(DEMO_BOX_SIZE) {

    // todo experimental rule for text width calculating
    enum class LetterCategory(val value: Double) {
        NARROW(0.3),
        NORMAL(0.67),
        WIDE(0.8),
        EXTRA_WIDE(0.98);

        companion object {
            private fun getLetterCategory(letter: Char): LetterCategory {
                return when (letter) {
                    'i', 'j', 'l',
                    '!', '.', ',', '\'',
                    'I' -> NARROW
                    'm', 'M', 'W' -> EXTRA_WIDE
                    'w', in 'A'..'Z', in '0'..'9' -> WIDE
                    else -> NORMAL
                }
            }

            fun getLetterRatio(letter: Char): Double {
                return getLetterCategory(letter).value
            }
        }
    }

    private fun PlotLabelSpec.width(text: String): Double {
        return text.map(LetterCategory.Companion::getLetterRatio).sum() * font.size
    }

    ////////

    fun createModels(): List<GroupComponent> {
        fun titles(charRange: CharRange): List<String> =
            charRange.map { letter -> List(15) { letter }.joinToString("") }

        val theme = DefaultTheme.minimal2()

        return listOf(
            Style.PLOT_TITLE to PlotLabelSpecFactory.plotTitle(theme.plot()),
            "${Style.AXIS_TITLE}-x" to PlotLabelSpecFactory.axisTitle(theme.horizontalAxis(false))
        ).flatMap {
            listOf(
                createModel(it, titles('A'..'Z')),
                createModel(it, titles('a'..'z')),
                createModel(it, titles('!'..'9'))
            )
        }
    }

    private fun createModel(plotLabel: Pair<String, LabelSpec>, titles: List<String>): GroupComponent {
        val groupComponent = GroupComponent()

        val x = 120.0
        var y = 20.0

        val nameSpecElement = TextLabel(plotLabel.first).rootGroup
        SvgUtils.transformTranslate(nameSpecElement, 10.0, y)
        groupComponent.add(nameSpecElement)

        titles
            .forEach { title ->
                val spec = LabelTextAndSpec(title, plotLabel.second)
                val textLabel = createTextLabel(spec, plotLabel.first)

                val element = textLabel.rootGroup
                SvgUtils.transformTranslate(element, x, y)
                groupComponent.add(element)

                val titleSizeNew = titleDimensions(spec, useLetterTypes = true)
                val rectNew = DoubleRectangle(x, y - titleSizeNew.y / 2, titleSizeNew.x, titleSizeNew.y)
                val titleSizeOld = titleDimensions(spec, useLetterTypes = false)
                val rectOld = DoubleRectangle(x, y - titleSizeOld.y / 2, titleSizeOld.x, titleSizeOld.y)

                groupComponent.add(svgRect(rectOld, Color.DARK_GREEN, strokeWidth = 2.0))
                groupComponent.add(svgRect(rectNew, Color.MAGENTA, strokeWidth = 1.5))

                y += titleSizeOld.y + 10.0
            }
        return groupComponent
    }

    private fun svgRect(r: DoubleRectangle, color: Color, strokeWidth: Double): SvgRectElement {
        val rect = SvgRectElement(r)
        rect.strokeColor().set(color)
        rect.strokeWidth().set(strokeWidth)
        rect.fillOpacity().set(0.0)
        return rect
    }

    private fun titleDimensions(spec: LabelTextAndSpec, useLetterTypes: Boolean): DoubleVector {
        if (spec.text.isEmpty()) {
            return DoubleVector.ZERO
        }
        val width = if (useLetterTypes) {
            spec.plotLabelSpec.width(spec.text)
        } else {
            spec.plotLabelSpec.width(spec.text)
        }
        return DoubleVector(width, spec.plotLabelSpec.height())
    }

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(450.0, 800.0)

        private class LabelTextAndSpec(
            val text: String,
            val plotLabelSpec: LabelSpec
        )

        private fun createTextLabel(spec: LabelTextAndSpec, className: String): TextLabel {
            val label = TextLabel(spec.text)
            label.addClassName(className)
            label.textColor().set(Color.DARK_BLUE)
            label.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
            label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
            return label
        }
    }
}