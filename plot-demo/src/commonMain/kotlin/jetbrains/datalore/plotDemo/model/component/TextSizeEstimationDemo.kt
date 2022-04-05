/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.gcommon.collect.Comparables.max
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Font
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.presentation.CharCategory.Companion.getCharRatio
import jetbrains.datalore.plot.builder.presentation.getOptionsForFont
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.SvgUtils
import kotlin.math.roundToInt

class TextSizeEstimationDemo(demoInnerSize: DoubleVector) : SimpleDemoBase(demoInnerSize) {

    fun width(text: String, font: Font, fontRatio: Double?, categoryRatio: Double?, boldRatio: Double?): Double {
        val options = getOptionsForFont(font.family.toString())
        val width = text.map {
            categoryRatio ?: getCharRatio(it, options)
        }.sum() *
                font.size *
                (fontRatio ?: options.fontRatio)
        return if (font.isBold) {
            width * (boldRatio ?: options.fontBoldRatio)
        } else {
            width
        }
    }

    private fun titleDimensions(
        spec: LabelSpec,
        widthRatio: Double?,
        categoryRatio: Double?,
        boldRatio: Double?
    ): DoubleVector {
        if (spec.text.isEmpty()) {
            return DoubleVector.ZERO
        }
        return DoubleVector(
            width(spec.text, spec.font, widthRatio, categoryRatio, boldRatio),
            spec.font.size.toDouble()
        )
    }

    fun createModel(
        lines: List<String>,
        font: Font,
        fontWidthRatio: Double?,
        categoryRatio: Double?,
        boldRatio: Double?,
        lineBounds: List<DoubleVector>
    ): GroupComponent {
        val groupComponent = GroupComponent()
        var x = 0.0
        var y = 20.0
        val lineInterval = 10.0
        val rowsCount = (demoInnerSize.y / (font.size + lineInterval)).toInt() - 1

        lines
            .map { line -> LabelSpec(line, font) }
            .forEachIndexed { index, spec ->
                val estimatedSize = titleDimensions(spec, fontWidthRatio, categoryRatio, boldRatio)
                val estimatedRect =
                    DoubleRectangle(x, y - estimatedSize.y / 2, estimatedSize.x, estimatedSize.y)
                groupComponent.add(svgRect(estimatedRect, Color.MAGENTA, strokeWidth = 1.5))

                /// actual size
                val bounds = lineBounds[index]
                val rect = DoubleRectangle(x, y - bounds.y / 2, bounds.x, bounds.y)
                groupComponent.add(svgRect(rect, Color.BLUE, strokeWidth = 1.0))

                // label
                val textLabel = createTextLabel(spec)
                val element = textLabel.rootGroup
                SvgUtils.transformTranslate(element, x, y)
                groupComponent.add(element)

                // delta
                val delta = estimatedSize.x - bounds.x
                val deltaStr = "∆=${(delta * 10000).roundToInt().toDouble() / 10000}"
                val deltaLabel = createTextLabel(LabelSpec(deltaStr, Font(FontFamily.MONOSPACED, 10)))
                val deltaElement = deltaLabel.rootGroup
                SvgUtils.transformTranslate(deltaElement, x + max(estimatedSize.x, bounds.x) + 10.0, y)
                groupComponent.add(deltaElement)

                y += estimatedSize.y + lineInterval
                if ((index + 1) % rowsCount == 0) {
                    x += max(estimatedSize.x, bounds.x) + 200.0
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
            fontWidthRatio: Double?,
            categoryRatio: Double?,
            boldRatio: Double?,
            lineBounds: List<DoubleVector>
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
                            categoryRatio,
                            boldRatio,
                            lineBounds
                        )
                    )
                ).firstOrNull()
            }
        }
    }
}


