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
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.SvgUtils

class TextSizesEstimationDemo(demoInnerSize: DoubleVector) : SimpleDemoBase(demoInnerSize) {

    // todo experimental rule for text width calculating
    enum class CharCategory(val value: Double) {
        EXTRA_NARROW(0.5),
        NARROW(0.6),
        NORMAL(1.0),
        WIDE(1.3),
        EXTRA_WIDE(1.5);

        val nameWithRatio = "$name ($value)"

        companion object {
            private val EXTRA_NARROW_CHARS = listOf('I', 'i', 'j', 'l', '.', ',', '\'', '|', '!', ':', ';', ' ')
            private val NARROW_CHARS =
                listOf('f', 'r', 't', '(', ')', '[', ']', '{', '}', '\\', '/', '*', '-', '"', '`')
            private val EXTRA_WIDE_CHARS = listOf('m', 'M', 'W', '%', '@')
            private val CAPITALS_OF_NORMAL_WIDTH = listOf('E', 'F', 'J', 'L', 'X', 'Z')
            private val WIDE_CHARS =
                listOf('w', '+', '&') + ('A'..'Z') - EXTRA_NARROW_CHARS - CAPITALS_OF_NORMAL_WIDTH - EXTRA_WIDE_CHARS
            private val NORMAL_CHARS = (32..126).map(Int::toChar) -
                    EXTRA_NARROW_CHARS - NARROW_CHARS - EXTRA_WIDE_CHARS - WIDE_CHARS

            private fun getCharCategory(ch: Char): CharCategory {
                return when (ch) {
                    in EXTRA_NARROW_CHARS -> EXTRA_NARROW
                    in NARROW_CHARS -> NARROW
                    in EXTRA_WIDE_CHARS -> EXTRA_WIDE
                    in WIDE_CHARS -> WIDE
                    else -> NORMAL
                }
            }

            private fun getCharListByCategory(category: CharCategory): List<Char> {
                return when (category) {
                    EXTRA_NARROW -> EXTRA_NARROW_CHARS
                    NARROW -> NARROW_CHARS
                    NORMAL -> NORMAL_CHARS
                    WIDE -> WIDE_CHARS
                    EXTRA_WIDE -> EXTRA_WIDE_CHARS
                }
            }

            fun getCharRatio(ch: Char) = getCharCategory(ch).value

            fun getCharCategoryNames() = values().map(CharCategory::name)
            fun getCharCategoryNamesWithRatios() = values().map(CharCategory::nameWithRatio)

            fun getCharsForCategory(catName: String?): List<Char> {
                val category = values().find { it.name == catName || it.nameWithRatio == catName }
                return category?.let { getCharListByCategory(it) } ?: emptyList()
            }
        }
    }

    fun width(text: String, font: Font, weightRatio: Double): Double {
/*
        val ratioFunc: (Char) -> Double = when {
            isMonospaced -> { { FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED } }
            else -> CharCategory::getCharRatio
        }
        val width = text.map(ratioFunc).sum() * font.size * FONT_WEIGHT_TO_NORMAL_WIDTH_RATIO
        return if (font.isBold) {
            width * FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
        } else {
            width
        }
*/
        return text.map(CharCategory::getCharRatio).sum() * font.size * weightRatio
    }

    private fun titleDimensions(spec: LabelSpec, weightRatio: Double): DoubleVector {
        if (spec.text.isEmpty()) {
            return DoubleVector.ZERO
        }
        return DoubleVector(
            width(spec.text, spec.font, weightRatio),
            spec.font.size.toDouble()
        )
    }

    fun createModel(lines: List<String>, font: Font, fontWeightRatio: Double): GroupComponent {
        val groupComponent = GroupComponent()
        var x = 0.0
        var y = 20.0
        lines
            .map { line -> LabelSpec(line, font) }
            .forEachIndexed { index, spec ->
                val textLabel = createTextLabel(spec)

                val element = textLabel.rootGroup
                SvgUtils.transformTranslate(element, x, y)
                groupComponent.add(element)

                val titleSize = titleDimensions(spec, fontWeightRatio)
                val rectNew = DoubleRectangle(x, y - titleSize.y / 2, titleSize.x, titleSize.y)

                groupComponent.add(svgRect(rectNew, Color.MAGENTA, strokeWidth = 1.5))

                y += titleSize.y + 10.0

                if ((index + 1) % 30 == 0) {
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
            val font: Font
        )

        private fun createTextLabel(spec: LabelSpec): TextLabel {
            val label = TextLabel(spec.text)

            label.setFontFamily(spec.font.family.toString())
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
            fontWeightRatio: Double
        ): SvgSvgElement? {
            return with(TextSizesEstimationDemo(demoInnerSize)) {
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
                            fontWeightRatio
                        )
                    )
                ).firstOrNull()
            }
        }
    }
}


