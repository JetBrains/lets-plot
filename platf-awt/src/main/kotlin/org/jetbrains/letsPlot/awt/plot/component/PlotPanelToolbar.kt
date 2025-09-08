/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import com.github.weisj.jsvg.SVGDocument
import com.github.weisj.jsvg.parser.SVGLoader
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.DefaultFigureToolsController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleToolView
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.BBOX_ZOOM_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.CBOX_ZOOM_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.PAN_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.res.ToolbarIcons
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*

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
        preferredSize = java.awt.Dimension(preferredSize.width, TOOLBAR_HEIGHT)
        minimumSize = java.awt.Dimension(minimumSize.width, TOOLBAR_HEIGHT)
        maximumSize = java.awt.Dimension(maximumSize.width, TOOLBAR_HEIGHT)

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
        val iconSvg = toolSpec["icon"] as? String
        val icon = iconSvg?.let { createSvgIcon(it) }

        val button = JButton(tool.label).apply {
            icon?.let {
                this.icon = it
                text = null // Remove text when the icon is present
                toolTipText = tool.label
            }
        }

        val view = object : ToggleToolView {
            override fun setState(selected: Boolean) {
                button.isSelected = selected
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
        val icon = createSvgIcon(ToolbarIcons.RESET)
        val button = JButton(icon).apply {
            toolTipText = "Reset"
        }
        button.addActionListener {
            controller.resetFigure(deactiveTools = true)
        }
        return button
    }

    private fun createSvgIcon(svgString: String, size: Dimension = Dimension(16, 16)): Icon? {
        return try {
            val loader = SVGLoader()
            val inputStream = svgString.byteInputStream()
            val document: SVGDocument = loader.load(inputStream) ?: return null

            // Create BufferedImage and Graphics2D for rendering
            val bufferedImage = java.awt.image.BufferedImage(
                size.width,
                size.height,
                java.awt.image.BufferedImage.TYPE_INT_ARGB
            )

            val graphics = bufferedImage.createGraphics()
            try {
                // Render SVG to the graphics context with null ViewBox (uses document's default)
                document.render(null, graphics, null)
            } finally {
                graphics.dispose()
            }

            ImageIcon(bufferedImage)
        } catch (e: Exception) {
            null // Return null if SVG parsing/rendering fails
        }
    }

    companion object {
        const val TOOLBAR_HEIGHT = 33
    }
}