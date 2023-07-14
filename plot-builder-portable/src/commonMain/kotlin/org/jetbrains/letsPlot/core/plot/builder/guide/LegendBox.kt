/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.applyJustification
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

abstract class LegendBox : SvgComponent() {

    var debug: Boolean = false

    abstract val spec: LegendBoxSpec

    protected val theme: LegendTheme get() = spec.theme

    private val title: String
        get() = spec.title

    val size: DoubleVector
        get() = spec.size

    private fun hasTitle(): Boolean {
        return spec.hasTitle()
    }

    override fun buildComponent() {
        if (theme.showBackground()) {
            add(SvgRectElement(spec.innerBounds).apply {
                strokeColor().set(theme.backgroundColor())
                strokeWidth().set(theme.backgroundStrokeWidth())
                fillColor().set(theme.backgroundFill())
            })
        }

        val innerGroup = SvgGElement()
        innerGroup.transform().set(buildTransform(spec.contentOrigin, 0.0))

        val l = spec.layout

        val titleBoundingRect = let {
            if (!hasTitle()) return@let DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)

            val titleRectSize = when {
                l.isHorizontal -> {
                    spec.contentBounds.dimension.subtract(DoubleVector(l.graphSize.x, 0.0))
                }

                else -> {
                    DoubleVector(spec.contentBounds.width, l.titleSize.y)
                }
            }
            DoubleRectangle(DoubleVector.ZERO, titleRectSize)
        }

        if (hasTitle()) {
            val label = createTitleLabel(
                titleBoundingRect,
                l.titleSize,
                theme.titleJustification()
            )
            innerGroup.children().add(label.rootGroup)
        }

        val graphGroup = SvgGElement()
        graphGroup.transform().set(buildTransform(l.graphOrigin, 0.0))
        appendGuideContent(graphGroup)
        innerGroup.children().add(graphGroup)

        if (debug) {
            // outer bounds
            val outerBounds = DoubleRectangle(DoubleVector.ZERO, spec.size)
            add(createTransparentRect(outerBounds, Color.CYAN, 1.0))
            run {
                // inner bounds
                val rect = SvgRectElement(spec.innerBounds)
                rect.fillColor().set(Color.BLACK)
                rect.strokeWidth().set(0.0)
                rect.fillOpacity().set(0.1)
                add(rect)
            }
            // content bounds
            add(createTransparentRect(spec.contentBounds, Color.DARK_MAGENTA, 1.0))
            // title bounds
            val rect = titleBoundingRect.add(spec.contentOrigin)
            add(createTransparentRect(rect, Color.MAGENTA, 1.0))
            // title bounding box
            val textDimensions = PlotLayoutUtil.textDimensions(title, PlotLabelSpecFactory.legendTitle(theme))
            val titleBoundingBox =
                DoubleRectangle(spec.contentBounds.left, spec.contentBounds.top, textDimensions.x, textDimensions.y)
            add(createTransparentRect(titleBoundingBox, Color.DARK_GREEN, 1.0))
        }

        add(innerGroup)
    }

    protected abstract fun appendGuideContent(contentRoot: SvgNode): DoubleVector

    private fun createTitleLabel(
        boundRect: DoubleRectangle,
        titleSize: DoubleVector,
        justification: TextJustification
    ): MultilineLabel {
        val lineHeight = PlotLabelSpecFactory.legendTitle(theme).height()

        val label = MultilineLabel(title)
        val (pos, hAnchor) = applyJustification(
            boundRect,
            textSize = titleSize,
            lineHeight,
            justification
        )
        label.addClassName(Style.LEGEND_TITLE)
        label.setHorizontalAnchor(hAnchor)
        label.setLineHeight(lineHeight)
        label.moveTo(pos)
        return label
    }

    companion object {
        fun createTransparentRect(
            bounds: DoubleRectangle,
            strokeColor: Color,
            strokeWidth: Double
        ): SvgRectElement {
            val rect = SvgRectElement(bounds)
            rect.strokeColor().set(strokeColor)
            rect.strokeWidth().set(strokeWidth)
            rect.fillOpacity().set(0.0)
            return rect
        }
    }
}
