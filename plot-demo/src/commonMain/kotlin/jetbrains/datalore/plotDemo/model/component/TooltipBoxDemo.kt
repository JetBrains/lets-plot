/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

class TooltipBoxDemo : SimpleDemoBase() {

    override val cssStyle: String
        get() = """
            |.$TOOLTIP_TEXT_CLASS { 
            |    font-size: 18px; 
            |    fill: #000000; 
            |}""".trimMargin()

    // TooltipBox uses SvgPeer - split creation and update so update can be called after attach
    fun createModels(): List<Pair<GroupComponent, () -> Unit>> {
        return listOf(
            generalTooltip()
        )
    }

    private fun generalTooltip(): Pair<GroupComponent, () -> Unit>
    {
        val tooltipBox = TooltipBox()
        val groupComponent = GroupComponent()
        groupComponent.add(tooltipBox.rootGroup)

        return Pair(groupComponent) {
            tooltipBox.update(
                fillColor = Color.LIGHT_YELLOW,
                textColor = Color.BLUE,
                borderColor = Color.BLACK,
                strokeWidth = 2.0,
                lines = listOf(
                    TooltipSpec.Line.withLabelAndValue("some label:", "value"),
                    TooltipSpec.Line.withValue("only value"),
                ),
                title = null,
                textClassName = "plot-text",
                rotate = false,
                tooltipMinWidth = null,
                borderRadius = 4.0,
                markerColors = listOf(Color.DARK_GREEN, Color.GRAY)
            )

            tooltipBox.setPosition(
                tooltipCoord = DoubleVector(0.0, 0.0),
                pointerCoord = DoubleVector(83.0, 90.0),
                orientation = TooltipBox.Orientation.VERTICAL
            )
        }
    }

    companion object {
        const val TOOLTIP_TEXT_CLASS = "tootip-text"
    }
}
