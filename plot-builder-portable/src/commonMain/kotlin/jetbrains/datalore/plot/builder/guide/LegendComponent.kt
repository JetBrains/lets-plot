/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgRectElement

class LegendComponent(
    override val spec: LegendComponentSpec
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
        val label = TextLabel(br.label)
        label.addClassName(Style.LEGEND_ITEM)
        label.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.moveTo(labelBox.origin.add(DoubleVector(0.0, labelBox.height / 2)))
        breakComponent.add(label)

        breakComponent.moveTo(keyLabelBox.origin)
        return breakComponent.rootGroup
    }

    private fun createKeyElement(legendBreak: LegendBreak, size: DoubleVector): SvgGElement {
        val g = SvgGElement()

        // common background
        val keyBounds = DoubleRectangle(DoubleVector.ZERO, size)
        val backgroundRect = SvgRectElement(keyBounds)
        backgroundRect.strokeWidth().set(0.0)
        backgroundRect.fillColor().set(theme.backgroundFill())

        g.children().add(backgroundRect)

        // key
        val innerSize = DoubleVector(size.x - 2, size.y - 2)
        val keyElement = legendBreak.createKeyElement(innerSize)
        val keyElementTransform = buildTransform(DoubleVector(1.0, 1.0), 0.0)
        keyElement.transform().set(keyElementTransform)

        g.children().add(keyElement)

        // add a frame (To nicely trim internals?)
        val frame = createTransparentRect(
            keyBounds,
            strokeColor = theme.backgroundFill(),
            1.0
        )

        g.children().add(frame)
        return g
    }
}
