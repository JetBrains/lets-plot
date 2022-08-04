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
import jetbrains.datalore.plot.builder.presentation.ClusteringModel
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.SvgUtils
import kotlin.math.max
import kotlin.math.pow


class TextSizeEstimationDemo(demoInnerSize: DoubleVector) : SimpleDemoBase(demoInnerSize) {

    fun createModel(
        textLines: List<String>,
        font: Font,
        actualTextDimensions: List<DoubleVector>,
        sizeRatio: Double,
        boldRatio: Double,
        italicRatio: Double,
        fontAdditiveError: Double
    ): GroupComponent {
        val groupComponent = GroupComponent()
        var x = 0.0
        var y = 20.0
        val lineInterval = 10.0
        val rowsCount = (demoInnerSize.y / (font.size + lineInterval)).toInt() - 1

        fun createRect(size: DoubleVector) = DoubleRectangle(x, y - size.y / 2, size.x, size.y)
        fun Double.round(d: Int = 3): Double {
            val factor = 10.0.pow(d)
            return kotlin.math.round(this * factor) / factor
        }

        textLines
            .forEachIndexed { index, text ->
                // old estimation
                val oldSize = PlotLabelSpec(font.size.toDouble(), font.isBold).dimensions(text.length)
                groupComponent.add(svgRect(createRect(oldSize), Color.LIGHT_GRAY, strokeWidth = 1.0))

                // new estimation
                val estimatedSize = fixEstimation(ClusteringModel.textDimension(text, font, sizeRatio, boldRatio, italicRatio, fontAdditiveError))
                groupComponent.add(svgRect(createRect(estimatedSize), Color.MAGENTA, strokeWidth = 1.5))

                /// actual size
                val actualSize = actualTextDimensions[index]
                groupComponent.add(svgRect(createRect(actualSize), Color.DARK_BLUE, strokeWidth = 1.0))

                // label
                val textLabel = createTextLabel(text, font)
                val element = textLabel.rootGroup
                SvgUtils.transformTranslate(element, x, y)
                groupComponent.add(element)

                // delta
                val delta = estimatedSize.x - actualSize.x

                val deltaStr = "actual=${actualSize.x.round()}, estimated=${estimatedSize.x.round()}, ∆=${delta.round()}"
                val deltaLabel = createTextLabel(deltaStr, Font(FontFamily.MONOSPACED, 10))
                val deltaElement = deltaLabel.rootGroup
                SvgUtils.transformTranslate(
                    deltaElement,
                    x + (listOf(oldSize.x, estimatedSize.x, actualSize.x).maxOrNull() ?: 0.0 ) + 10.0,
                    y
                )
                groupComponent.add(deltaElement)

                y += estimatedSize.y + lineInterval
                if ((index + 1) % rowsCount == 0) {
                    x += max(estimatedSize.x, actualSize.x) + 200.0
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

    private fun fixEstimation(estimatedSize: DoubleVector): DoubleVector {
        val batikCoeff = 0.75
        return DoubleVector(batikCoeff * estimatedSize.x, estimatedSize.y)
    }

    companion object {

        private fun createTextLabel(text: String, font: Font): TextLabel {
            val label = TextLabel(text)

            label.setFontFamily("\"${font.family}\"")
            if (font.isItalic) {
                label.setFontStyle("italic")
            }
            label.setFontSize(font.size.toDouble())
            if (font.isBold) {
                label.setFontWeight("bold")
            }
            label.textColor().set(Color.DARK_BLUE)

            label.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
            label.setVerticalAnchor(Text.VerticalAnchor.CENTER)

            return label
        }

        fun createSvgElement(
            demoInnerSize: DoubleVector,
            textLines: List<String>,
            fontName: String,
            fontSize: Int,
            isBold: Boolean,
            isItalic: Boolean,
            actualTextDimensions: List<DoubleVector>,
            sizeRatio: Double,
            boldRatio: Double,
            italicRatio: Double,
            fontAdditiveError: Double,
        ): SvgSvgElement? {
            return with(TextSizeEstimationDemo(demoInnerSize)) {
                createSvgRoots(
                    listOf(
                        createModel(
                            textLines,
                            Font(
                                FontFamily.forName(fontName),
                                fontSize,
                                isBold,
                                isItalic
                            ),
                            actualTextDimensions,
                            sizeRatio,
                            boldRatio,
                            italicRatio,
                            fontAdditiveError,
                        )
                    )
                ).firstOrNull()
            }
        }
    }
}


