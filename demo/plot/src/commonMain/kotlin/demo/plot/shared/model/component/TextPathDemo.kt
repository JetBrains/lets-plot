/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.component

import demo.plot.common.model.SimpleDemoBase
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.datamodel.svg.dom.*

class TextPathDemo : SimpleDemoBase() {
    override val cssStyle: String
        get() = ".${LABEL_CLASS_NAME} { font-size: ${FONT_SIZE}px; }"

    fun createModel(): GroupComponent {
        val groupComponent = GroupComponent()

        val pathId = "path"

        val pathData: SvgPathData = SvgPathDataBuilder().apply {
            moveTo(50.0, 90.0)
            ellipticalArc(100.0, 50.0, 0.0, largeArc = false, sweep = true, to = DoubleVector(300.0, 250.0))
            //  lineTo(500.0, 250.0)
        }.build()

        val path = SvgPathElement(pathData).apply {
            strokeColor().set(Color.DARK_BLUE)
            fillOpacity().set(0.0)
            id().set(pathId)
        }

        fun text(startOffset: Number) = "Text on a path\nwith multiple lines\nand offset=$startOffset"
        val labels = mutableListOf<MultilineLabel>()

        val lineHeight = FONT_SIZE
        val textHeight = lineHeight * MultilineLabel.splitLines(text(0)).size

        labels.add(
            MultilineLabel.createCurvedLabel(
                text = text(0),
                textHeight,
                pathId,
                startOffset = 0.0
            )
        )
        labels.add(
            MultilineLabel.createCurvedLabel(
                text = text(50),
                textHeight,
                pathId,
                startOffset = 50.0
            )
        )
        labels.add(
            MultilineLabel.createCurvedLabel(
                text = text(100),
                textHeight,
                pathId,
                startOffset = 100.0
            )
        )
        labels.forEach { it.addClassName(LABEL_CLASS_NAME) }

        groupComponent.add(path)
        labels.forEach { groupComponent.add(it.rootGroup) }

        return groupComponent
    }

    companion object {
        private const val LABEL_CLASS_NAME = "label"
        private const val FONT_SIZE = 16.0
    }
}