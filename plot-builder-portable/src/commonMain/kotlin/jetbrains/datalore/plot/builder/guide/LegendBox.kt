/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgRectElement

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
        addClassName(Style.LEGEND)

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
        if (hasTitle()) {
            val label = createTitleLabel(
                l.titleLocation,
                l.titleHorizontalAnchor,
                l.titleVerticalAnchor
            )
            label.textColor().set(theme.titleColor())
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
            add(createTransparentRect(l.titleBounds.add(spec.contentOrigin), Color.MAGENTA, 1.0))
        }

        add(innerGroup)
    }

    protected abstract fun appendGuideContent(contentRoot: SvgNode): DoubleVector

    private fun createTitleLabel(
        origin: DoubleVector,
        horizontalAnchor: TextLabel.HorizontalAnchor,
        verticalAnchor: TextLabel.VerticalAnchor
    ): TextLabel {
        val label = TextLabel(title)
        label.addClassName(Style.LEGEND_TITLE)
        label.setHorizontalAnchor(horizontalAnchor)
        label.setVerticalAnchor(verticalAnchor)
        label.moveTo(origin)
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
