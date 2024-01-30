/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.TextLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.builder.AxisUtil
import org.jetbrains.letsPlot.core.plot.builder.PolarAxisUtil
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils
import kotlin.math.atan2

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


        // Axis
        if (!hideAxis) {
            // Ticks and labels
            if (!hideAxisBreaks && (axisTheme.showLabels() || axisTheme.showTickMarks())) {
                val tickLabelBaseOffset = AxisUtil.tickLabelBaseOffset(axisTheme, orientation)

                for ((i, v) in breaksData.majorBreaks.withIndex()) {
                    val label = breaksData.majorLabels[i % breaksData.majorLabels.size]
                    val labelOffset = tickLabelBaseOffset.add(labelAdjustments.additionalOffset(i))

                    val (tickLabel, tickMark) = buildTick(label, labelOffset, axisTheme, v, breaksData.center)

                    tickMark?.let { rootElement.children().add(it) }
                    tickLabel?.let { rootElement.children().add(it.rootGroup) }
                }
            }

            // Axis line
            if (!hideAxisBreaks && axisTheme.showLine()) {
                if (orientation.isHorizontal) {
                    val axisLine = SvgPathElement(SvgPathDataBuilder().lineString(breaksData.axisLine).build()).apply {
                        strokeWidth().set(axisTheme.lineWidth())
                        strokeColor().set(axisTheme.lineColor())
                        fillColor().set(Color.TRANSPARENT)
                    }
                    rootElement.children().add(axisLine)
                } else {
                    val axisLine = SvgLineElement(0.0, 0.0, 0.0, length / 2.0).apply {
                        strokeWidth().set(axisTheme.lineWidth())
                        strokeColor().set(axisTheme.lineColor())
                    }
                    rootElement.children().add(axisLine)
                }
            }

        }
    }

    private fun buildTick(
        label: String,
        labelOffset: DoubleVector,
        axisTheme: AxisTheme,
        breakVector: DoubleVector,
        center: DoubleVector
    ): Pair<TextLabel?, SvgLineElement?> {
        val breakCoord = breakVector.add(breaksData.center)

        val tickMark: SvgLineElement? = if (axisTheme.showTickMarks()) {
            val tickMark = SvgLineElement()
            tickMark.strokeWidth().set(axisTheme.tickMarkWidth())
            tickMark.strokeColor().set(axisTheme.tickMarkColor())
            val markLength = axisTheme.tickMarkLength()

            when (orientation) {
                Orientation.LEFT -> {
                    tickMark.x2().set(-markLength)
                    tickMark.y2().set(0.0)

                    SvgUtils.transformTranslate(tickMark, 0.0, breakCoord.y)
                }

                Orientation.BOTTOM -> {
                    val tickMarkVector = breakVector.mul(1 + markLength / breakVector.length())
                    tickMark.x2().set(tickMarkVector.add(center).x)
                    tickMark.y2().set(tickMarkVector.add(center).y)

                    tickMark.x1().set(breakVector.add(center).x)
                    tickMark.y1().set(breakVector.add(center).y)
                }

                Orientation.RIGHT -> error("Unsupported orientation $orientation")
                Orientation.TOP -> error("Unsupported orientation $orientation")
            }
            tickMark
        } else {
            null
        }

        val tickLabel = if (axisTheme.showLabels()) {
            val tickLabel = TextLabel(label)
            tickLabel.addClassName("${Style.AXIS_TEXT}-${axisTheme.axis}")

            when (orientation.isHorizontal) {
                false -> {
                    // Vertical axis is always on the left side of the plot for now - ignore breakCoord.x
                    tickLabel.moveTo(labelOffset.x, labelOffset.y + breakCoord.y)
                    tickLabel.setHorizontalAnchor(HorizontalAnchor.RIGHT)
                    tickLabel.setVerticalAnchor(VerticalAnchor.CENTER)
                }

                true -> {
                    val pos = breakVector.mul(1 + labelOffset.length() / breakVector.length())
                    tickLabel.moveTo(pos.add(center))
                    val degrees = toDegrees(atan2(pos.y, pos.x))
                    val (hAnchor, vAnchor) = when (degrees) {
                        in -5.0..5.0 -> HorizontalAnchor.LEFT to VerticalAnchor.CENTER
                        in 85.0..95.0 -> HorizontalAnchor.MIDDLE to VerticalAnchor.TOP
                        in -95.0..-85.0 -> HorizontalAnchor.MIDDLE to VerticalAnchor.BOTTOM
                        in 175.0..185.0 -> HorizontalAnchor.RIGHT to VerticalAnchor.CENTER
                        in -185.0..-175.0 -> HorizontalAnchor.RIGHT to VerticalAnchor.CENTER

                        in 0.0..90.0 -> HorizontalAnchor.LEFT to VerticalAnchor.TOP
                        in 90.0..180.0 -> HorizontalAnchor.RIGHT to VerticalAnchor.TOP
                        in -180.0..-90.0 -> HorizontalAnchor.RIGHT to VerticalAnchor.BOTTOM
                        in -90.0..0.0 -> HorizontalAnchor.LEFT to VerticalAnchor.BOTTOM
                        else -> HorizontalAnchor.MIDDLE to VerticalAnchor.CENTER
                    }

                    tickLabel.setHorizontalAnchor(hAnchor)
                    tickLabel.setVerticalAnchor(vAnchor)
                }
            }

            tickLabel.rotate(labelAdjustments.rotationDegree)
            tickLabel
        } else {
            null
        }

        return tickLabel to tickMark
    }

}
