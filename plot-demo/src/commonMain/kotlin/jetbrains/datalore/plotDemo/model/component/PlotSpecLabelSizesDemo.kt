/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgUtils

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
                    'I'-> NARROW
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

    // todo later should be moved to LabelMetrics.kt
    fun PlotLabelSpec.width(text: String): Double {
        return text.map(LetterCategory.Companion::getLetterRatio).sum() * fontSize
    }

    ////////

    fun createModels(): List<GroupComponent> {
        fun titles(charRange: CharRange): List<String> =
            charRange.map { letter -> List(15) { letter }.joinToString("") }

        return listOf(
            createModel(PlotLabelSpec.PLOT_TITLE, titles('A'..'Z')),
            createModel(PlotLabelSpec.PLOT_TITLE, titles('a'..'z')),
            createModel(PlotLabelSpec.PLOT_TITLE, titles('!'..'9')),

            createModel(PlotLabelSpec.AXIS_TITLE, titles('A'..'Z')),
            createModel(PlotLabelSpec.AXIS_TITLE, titles('a'..'z')),
            createModel(PlotLabelSpec.AXIS_TITLE, titles('!'..'9')),
        )
    }

    private fun createModel(plotLabelSpec: PlotLabelSpec, titles: List<String>): GroupComponent {
        val groupComponent = GroupComponent()

        val x = 120.0
        var y = 20.0

        val nameSpecElement = TextLabel(labelSpecToString(plotLabelSpec)).rootGroup
        SvgUtils.transformTranslate(nameSpecElement, 10.0, y)
        groupComponent.add(nameSpecElement)

        titles
            .map { title -> LabelSpec(title, plotLabelSpec) }
            .forEach { spec ->
                val textLabel = createTextLabel(spec)

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

    private fun titleDimensions(spec: LabelSpec, useLetterTypes: Boolean): DoubleVector {
        if (spec.text.isEmpty()) {
            return DoubleVector.ZERO
        }
        val width = if (useLetterTypes) {
            spec.plotLabelSpec.width(spec.text)
        } else {
            spec.plotLabelSpec.width(spec.text.length)
        }
        return DoubleVector(width, spec.plotLabelSpec.height())
    }

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(450.0, 800.0)

        private class LabelSpec(
            val text: String,
            val plotLabelSpec: PlotLabelSpec
        )

        private fun createTextLabel(spec: LabelSpec): TextLabel {
            val label = TextLabel(spec.text)

            label.setFontSize(spec.plotLabelSpec.fontSize)
            if (spec.plotLabelSpec.isBold) {
                label.setFontWeight("bold")
            }
            label.textColor().set(Color.DARK_BLUE)

            label.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
            label.setVerticalAnchor(Text.VerticalAnchor.CENTER)

            return label
        }

        private fun labelSpecToString(plotLabelSpec: PlotLabelSpec): String {
            return plotLabelSpec.name
        }
    }
}