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
    enum class CharCategory(val value: Double) {
        EXTRA_NARROW(0.5),
        NARROW(0.6),
        NORMAL(1.0),
        WIDE(1.3),
        EXTRA_WIDE(1.5);

        companion object {
            private fun getCharCategory(ch: Char): CharCategory {
                return when (ch) {
                    'i', 'j', 'l',
                    '.', ',', '\'',
                    'I' -> EXTRA_NARROW
                    'f', 't', '(', ')', '[', ']', '{', '}', '!' -> NARROW
                    'm', 'M', 'W', '%' -> EXTRA_WIDE
                    'E','F','J','L','X','Z' -> NORMAL
                    'w', in 'A'..'Z', '+', '&' -> WIDE
                    else -> NORMAL
                }
            }

            fun getCharRatio(ch: Char) = getCharCategory(ch).value
        }
    }

    // todo later should be moved to LabelMetrics.kt
    fun PlotLabelSpec.width(text: String): Double {
        ////
        val FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO = 1.075
        val FONT_WEIGHT_TO_NORMAL_WIDTH_RATIO = 0.63
        val FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED = 0.6
        ////

        val ratioFunc: (Char) -> Double = when {
            isMonospaced -> {
                { FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED }
            }
            else -> CharCategory::getCharRatio
        }
        val width = text.map(ratioFunc).sum() * fontSize * FONT_WEIGHT_TO_NORMAL_WIDTH_RATIO
        return if (isBold) {
            width * FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
        } else {
            width
        }
    }

    fun createModels(): List<GroupComponent> {
        return createModelForLabelSpec(PlotLabelSpec.PLOT_TITLE) +
                createModelForLabelSpec(PlotLabelSpec.PLOT_SUBTITLE) +
                createModelForLabelSpec(PlotLabelSpec.AXIS_TITLE) +
                createModelForLabelSpec(PlotLabelSpec.AXIS_TICK) +
                createModelForLabelSpec(PlotLabelSpec.AXIS_TICK_SMALL) +
                createModelForLabelSpec(PlotLabelSpec.LEGEND_TITLE) +
                createModelForLabelSpec(PlotLabelSpec.LEGEND_ITEM)
    }

    private fun createModelForLabelSpec(labelSpec: PlotLabelSpec): List<GroupComponent> {
        fun titles(charRange: CharRange): List<String> =
            charRange.map { ch -> List(15) { ch }.joinToString("") }

        return listOf(
            createModel(labelSpec, titles('A'..'Z')),
            createModel(labelSpec, titles('a'..'z')),
            createModel(labelSpec, titles('!'..'9')),
        )
    }

    private fun createModel(plotLabelSpec: PlotLabelSpec, titles: List<String>): GroupComponent {
        val groupComponent = GroupComponent()

        val x = 120.0
        var y = 20.0

        val nameSpecElement = TextLabel(labelSpecToString(plotLabelSpec)).rootGroup
        SvgUtils.transformTranslate(nameSpecElement, 10.0, y)
        groupComponent.add(nameSpecElement)

        y += plotLabelSpec.height() * 2

        titles
            .map { title -> LabelSpec(title, plotLabelSpec) }
            .forEach { spec ->
                val textLabel = createTextLabel(spec)

                val element = textLabel.rootGroup
                SvgUtils.transformTranslate(element, x, y)
                groupComponent.add(element)

                val titleSizeNew = titleDimensions(spec, useNewWidthFunction = true)
                val rectNew = DoubleRectangle(x, y - titleSizeNew.y / 2, titleSizeNew.x, titleSizeNew.y)
                val titleSizeOld = titleDimensions(spec, useNewWidthFunction = false)
                val rectOld = DoubleRectangle(x, y - titleSizeOld.y / 2, titleSizeOld.x, titleSizeOld.y)

                groupComponent.add(svgRect(rectOld, Color.LIGHT_BLUE, strokeWidth = 2.0))
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

    private fun titleDimensions(spec: LabelSpec, useNewWidthFunction: Boolean): DoubleVector {
        if (spec.text.isEmpty()) {
            return DoubleVector.ZERO
        }
        val width = if (useNewWidthFunction) {
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
            return plotLabelSpec.run { "$name: fontSize=$fontSize; bold=$isBold; monospaced=$isMonospaced" }
        }
    }
}