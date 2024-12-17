/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import org.jetbrains.letsPlot.awt.plot.FigureModel
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.DefaultToolbarController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelAdapter
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleToolView
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.BBOX_ZOOM_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.CBOX_ZOOM_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.PAN_TOOL_SPEC
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

internal class SandboxToolbar(
    figureModel: FigureModel
) : JPanel() {

    private val controller = DefaultToolbarController(
        figure = asFigureAdapter(figureModel)
    )

    init {
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

    companion object {
        private val LOG = PortableLogging.logger("SandboxToolbar")

        fun asFigureAdapter(figureModel: FigureModel): FigureModelAdapter {
            return object : FigureModelAdapter {
                override fun activateTool(tool: ToggleTool) {
                    if (!tool.active) {
                        figureModel.activateInteractions(
                            origin = tool.name,
                            interactionSpecList = tool.interactionSpecList
                        )
                    }
                }

                override fun deactivateTool(tool: ToggleTool) {
                    if (tool.active) {
                        figureModel.deactivateInteractions(tool.name)
                    }
                }

                override fun updateView(specOverride: Map<String, Any>?) {
                    figureModel.updateView(specOverride)
                }

                override fun showError(msg: String) {
                    SwingUtilities.invokeLater {
                        JOptionPane.showMessageDialog(
                            null,
                            msg,
                            "Situation",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                }
            }
        }
    }
}