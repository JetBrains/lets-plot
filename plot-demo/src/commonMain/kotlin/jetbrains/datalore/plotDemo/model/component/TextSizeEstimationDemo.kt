/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Font
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.component.CharCategory.Companion.getCharRatio
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.SvgUtils

class TextSizeEstimationDemo(demoInnerSize: DoubleVector) : SimpleDemoBase(demoInnerSize) {

    fun width(text: String, font: Font, fontRatio: Double, categoryRatio: Double?): Double {

        //   val FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED = 1.0
        //   val FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO = 1.075

        val options = getOptionsForFont(font.family.toString())
        val width = text.map {
            categoryRatio ?: getCharRatio(it, options)
        }.sum() * font.size * fontRatio
        return if (font.isBold) {
            width //* FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
        } else {
            width
        }
    }

    private fun titleDimensions(spec: LabelSpec, widthRatio: Double, categoryRatio: Double?): DoubleVector {
        if (spec.text.isEmpty()) {
            return DoubleVector.ZERO
        }
        return DoubleVector(
            width(spec.text, spec.font, widthRatio, categoryRatio),
            spec.font.size.toDouble()
        )
    }

    fun createModel(lines: List<String>, font: Font, fontWidthRatio: Double, categoryRatio: Double?): GroupComponent {
        val groupComponent = GroupComponent()
        var x = 0.0
        var y = 20.0
        val lineInterval = 10.0
        val rowsCount = (demoInnerSize.y / (font.size + lineInterval)).toInt() - 1

        lines
            .map { line -> LabelSpec(line, font) }
            .forEachIndexed { index, spec ->
                val textLabel = createTextLabel(spec)

                val element = textLabel.rootGroup
                SvgUtils.transformTranslate(element, x, y)
                groupComponent.add(element)

                val titleSize = titleDimensions(spec, fontWidthRatio, categoryRatio)
                val rectNew = DoubleRectangle(x, y - titleSize.y / 2, titleSize.x, titleSize.y)

                groupComponent.add(svgRect(rectNew, Color.MAGENTA, strokeWidth = 1.5))

                y += titleSize.y + lineInterval

                if ((index + 1) % rowsCount == 0) {
                    x += titleSize.x + 150.0
                    y = 20.0
                }
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

    companion object {

        private class LabelSpec(
            val text: String,
            val font: Font,
        )

        private fun createTextLabel(spec: LabelSpec): TextLabel {
            val label = TextLabel(spec.text)

            label.setFontFamily("\"${spec.font.family}\"")
            if (spec.font.isItalic) {
                label.setFontStyle("italic")
            }
            label.setFontSize(spec.font.size.toDouble())
            if (spec.font.isBold) {
                label.setFontWeight("bold")
            }
            label.textColor().set(Color.DARK_BLUE)

            label.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
            label.setVerticalAnchor(Text.VerticalAnchor.CENTER)

            return label
        }

        fun createSvgElement(
            demoInnerSize: DoubleVector,
            lines: List<String>,
            fontName: String,
            fontSize: Int,
            isBold: Boolean,
            isItalic: Boolean,
            fontWidthRatio: Double,
            categoryRatio: Double?
        ): SvgSvgElement? {
            return with(TextSizeEstimationDemo(demoInnerSize)) {
                createSvgRoots(
                    listOf(
                        createModel(
                            lines,
                            Font(
                                FontFamily.forName(fontName),
                                fontSize,
                                isBold,
                                isItalic
                            ),
                            fontWidthRatio,
                            categoryRatio
                        )
                    )
                ).firstOrNull()
            }
        }
    }
}


