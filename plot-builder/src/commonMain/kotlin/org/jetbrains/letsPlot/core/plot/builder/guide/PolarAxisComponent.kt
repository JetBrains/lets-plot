/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.svg.TextLabel
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.builder.AxisUtil
import org.jetbrains.letsPlot.core.plot.builder.PolarAxisUtil
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

class PolarAxisComponent(
    private val length: Double,
    private val orientation: Orientation,
    private val breaksData: PolarAxisUtil.PolarBreaksData,
    private val labelAdjustments: AxisComponent.TickLabelAdjustments = AxisComponent.TickLabelAdjustments(orientation),
    private val axisTheme: AxisTheme,
    private val gridTheme: PanelGridTheme,
    private val hideAxis: Boolean = false,
    private val hideAxisBreaks: Boolean = false,
) : SvgComponent() {
    override fun buildComponent() {
        buildAxis()
    }

    private fun buildAxis() {
        val rootElement = rootGroup

        // Axis line
        if (!hideAxisBreaks && axisTheme.showLine()) {
            //if (orientation.isHorizontal) {
            //    val cx = length / 2
            //    val cy = length / 2
            //    val circle = SvgCircleElement(cx, cy, length / 2).apply {
            //        strokeWidth().set(gridTheme.majorLineWidth())
            //        strokeColor().set(gridTheme.majorLineColor())
            //        fillColor().set(Color.TRANSPARENT)
            //    }
            //    rootElement.children().add(circle)
            //}
        }

        // Axis
        if (!hideAxis) {
            // Ticks and labels
            if (!hideAxisBreaks && (axisTheme.showLabels() || axisTheme.showTickMarks())) {
                val tickLabelBaseOffset = AxisUtil.tickLabelBaseOffset(axisTheme, orientation)

                for ((i, br) in breaksData.majorBreaks.withIndex()) {
                    //if (br in start..end) {
                        val label = breaksData.majorLabels[i % breaksData.majorLabels.size]

                        val labelOffset = when (orientation.isHorizontal) {
                            false -> tickLabelBaseOffset.add(labelAdjustments.additionalOffset(i))
                            true -> DoubleVector.ZERO
                        }

                        val group = buildTick(label, labelOffset, axisTheme)

                        when (orientation.isHorizontal) {
                            false -> SvgUtils.transformTranslate(group, 0.0, br.y)
                            true -> SvgUtils.transformTranslate(group, br.x, br.y)
                        }

                        rootElement.children().add(group)
                    //}
                }
            }
        }
    }

    private fun buildTick(
        label: String,
        labelOffset: DoubleVector,
        axisTheme: AxisTheme
    ): SvgGElement {

        var tickMark: SvgLineElement? = null
        if (!orientation.isHorizontal && axisTheme.showTickMarks()) {
            tickMark = SvgLineElement()
            tickMark.strokeWidth().set(axisTheme.tickMarkWidth())
            tickMark.strokeColor().set(axisTheme.tickMarkColor())
        }

        var tickLabel: TextLabel? = null
        if (axisTheme.showLabels()) {
            tickLabel = TextLabel(label)
            tickLabel.addClassName("${Style.AXIS_TEXT}-${axisTheme.axis}")
        }

        val markLength = axisTheme.tickMarkLength()
        when (orientation) {
            Orientation.LEFT -> {
                if (tickMark != null) {
                    tickMark.x2().set(-markLength)
                    tickMark.y2().set(0.0)
                }
            }

            Orientation.RIGHT -> {
                if (tickMark != null) {
                    tickMark.x2().set(markLength)
                    tickMark.y2().set(0.0)
                }
            }

            Orientation.TOP -> {
                if (tickMark != null) {
                    tickMark.x2().set(0.0)
                    tickMark.y2().set(-markLength)
                }
            }

            Orientation.BOTTOM -> {
                if (tickMark != null) {
                    tickMark.x2().set(0.0)
                    tickMark.y2().set(markLength)
                }
            }
        }

        val g = SvgGElement()
        if (tickMark != null) {
            g.children().add(tickMark)
        }

        if (tickLabel != null) {
            tickLabel.moveTo(labelOffset.x, labelOffset.y)

            when (orientation.isHorizontal) {
                false -> {
                    tickLabel.setHorizontalAnchor(Text.HorizontalAnchor.RIGHT)
                    tickLabel.setVerticalAnchor(Text.VerticalAnchor.CENTER)
                }
                true -> {
                    tickLabel.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
                    tickLabel.setVerticalAnchor(Text.VerticalAnchor.CENTER)
                }
            }

            tickLabel.rotate(labelAdjustments.rotationDegree)
            g.children().add(tickLabel.rootGroup)
        }
        return g
    }

}
