/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.DefaultFigureToolsController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleToolView
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.BBOX_ZOOM_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.CBOX_ZOOM_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.PAN_TOOL_SPEC
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

internal class PlotPanelToolbar constructor(
    figureModel: FigureModel
) : JPanel() {

    private val controller = DefaultFigureToolsController(
        figure = figureModel,
        errorMessageHandler = { msg ->
            SwingUtilities.invokeLater {
                JOptionPane.showMessageDialog(
                    null,
                    msg,
                    "Situation",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    )

    init {
        layout = FlowLayout(FlowLayout.CENTER)
        preferredSize = java.awt.Dimension(preferredSize.width, 33)
        minimumSize = java.awt.Dimension(minimumSize.width, 33)
        maximumSize = java.awt.Dimension(maximumSize.width, 33)

        listOf(
            PAN_TOOL_SPEC,
            BBOX_ZOOM_TOOL_SPEC,
            CBOX_ZOOM_TOOL_SPEC,
        ).forEach {
            val button = createToolButton(it)
            this.add(button)
        }

        this.add(resetButton())

        figureModel.onToolEvent { event ->
            controller.handleToolFeedback(event)
        }
    }

    private fun createToolButton(toolSpec: Map<String, Any>): JButton {
        val tool = ToggleTool(toolSpec)
        val button = JButton("${tool.label} off")

        val view = object : ToggleToolView {
            override fun setState(selected: Boolean) {
                button.text = "${tool.label} ${if (selected) "on" else "off"}"
            }

            override fun onAction(handler: () -> Unit) {
                button.addActionListener {
                    handler()
                }
            }
        }
        controller.registerTool(tool, view)
        return button
    }

    private fun resetButton(): JButton {
        val button = JButton("Reset")
        button.addActionListener {
            controller.resetFigure(deactiveTools = true)
        }
        return button
    }
}