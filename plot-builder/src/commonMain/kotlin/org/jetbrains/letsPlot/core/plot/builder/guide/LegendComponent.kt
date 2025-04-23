/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

class LegendComponent(
    override val spec: LegendComponentSpec,
    private val panelTheme: PanelTheme
) : LegendBox() {

    override fun appendGuideContent(contentRoot: SvgNode): DoubleVector {
        val layout = spec.layout

        val keyLabelBoxes = layout.keyLabelBoxes.iterator()
        val labelBoxes = layout.labelBoxes.iterator()
        val keySizes = layout.keySizes.iterator()
        for (br in spec.breaks) {
            val keyLabelBox = keyLabelBoxes.next()
            val labelBox = labelBoxes.next()
            val keySize = keySizes.next()
            val breakElement = createBreakElement(br, keySize, keyLabelBox, labelBox)
            contentRoot.children().add(breakElement)
        }

        if (debug) {
            val graphBounds = DoubleRectangle(DoubleVector.ZERO, layout.graphSize)
            contentRoot.children().add(
                createTransparentRect(
                    graphBounds,
                    Color.DARK_BLUE,
                    1.0
                )
            )
        }
        return layout.size
    }

    private fun createBreakElement(
        br: LegendBreak,
        keySize: DoubleVector,
        keyLabelBox: DoubleRectangle,
        labelBox: DoubleRectangle
    ): SvgElement {
        val breakComponent = GroupComponent()

        // key element
        breakComponent.add(createKeyElement(br, keySize))

        // add label at position as was layout
        val label = MultilineLabel(br.label)
        val lineHeight = PlotLabelSpecFactory.legendItem(theme).height()
        label.addClassName(Style.LEGEND_ITEM)
        label.setLineHeight(lineHeight)
        label.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.moveTo(labelBox.origin)
        breakComponent.add(label)

        breakComponent.moveTo(keyLabelBox.origin)

        breakComponent.moveTo(keyLabelBox.origin.add(DoubleVector(0.0,keyLabelBox.height * 0.5 - keySize.y * 0.5)))
        return breakComponent.rootGroup
    }

    private fun createKeyElement(legendBreak: LegendBreak, size: DoubleVector): SvgGElement {
        val g = SvgGElement()

        // background rect for the legend icon
        if (theme.showKeyRect()) {
            val backgroundRect = LegendKeyElementFactory.createBackgroundRect(
                size,
                color = theme.keyRectColor(),
                fill = theme.keyRectFill(),
                strokeWidth = theme.keyRectStrokeWidth(),
                lineType = theme.keyLineType()
            )
            g.children().add(backgroundRect)
        } else if (panelTheme.showRect()) {
            // use "plot panel" color
            val backgroundRect = LegendKeyElementFactory.createBackgroundRect(
                size,
                color = panelTheme.rectFill(),
                fill = panelTheme.rectFill(),
                strokeWidth = 0.0,
                lineType = NamedLineType.SOLID
            )
            g.children().add(backgroundRect)
        }
        // key
        val innerSize = DoubleVector(size.x - 2, size.y - 2)
        val keyElement = legendBreak.createKeyElement(innerSize)
        val keyElementTransform = buildTransform(DoubleVector(1.0, 1.0), 0.0)
        keyElement.transform().set(keyElementTransform)

        g.children().add(keyElement)

//        // add a frame (To nicely trim internals?)
//        val frame = createTransparentRect(
//            keyBounds,
//            strokeColor = theme.backgroundFill(),
//            1.0
//        )
//
//        g.children().add(frame)
        return g
    }
}
