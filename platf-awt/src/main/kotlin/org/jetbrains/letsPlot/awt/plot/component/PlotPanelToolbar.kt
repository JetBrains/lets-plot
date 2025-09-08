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
import java.awt.Color
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
        val normalIcon = iconSvg?.let { createSvgIcon(it, color = C_STROKE) }
        val selectedIcon = iconSvg?.let { createSvgIcon(it, color = C_STROKE_SEL) }

        val button = JButton().apply {
            // Remove all text and borders
            text = ""
            toolTipText = tool.label

            // Set icons
            icon = normalIcon
            this.selectedIcon = selectedIcon
            rolloverIcon = normalIcon  // Keep normal icon on hover
            pressedIcon = selectedIcon

            // Remove default button styling
            isBorderPainted = false
            isFocusPainted = false
            isContentAreaFilled = false

            // Set size
            preferredSize = BUTTON_DIM
            minimumSize = BUTTON_DIM
            maximumSize = BUTTON_DIM

            // Set background colors
            background = C_BACKGR
            isOpaque = true

            // Add mouse listener for hover effects (background only)
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) {
                    if (!isSelected) {
                        background = C_BACKGR_HOVER
                    }
                }

                override fun mouseExited(e: java.awt.event.MouseEvent) {
                    if (!isSelected) {
                        background = C_BACKGR
                    }
                }
            })

            // Handle selection state changes
            addChangeListener {
                background = if (isSelected) C_BACKGR_SEL else C_BACKGR
            }
        }

        val view = object : ToggleToolView {
            override fun setState(selected: Boolean) {
                button.isSelected = selected
            }

            override fun onAction(handler: () -> Unit) {
                button.addActionListener { handler() }
            }
        }
        controller.registerTool(tool, view)
        return button
    }

    private fun resetButton(): JButton {
        val icon = createSvgIcon(ToolbarIcons.RESET, color = C_STROKE)

        val button = JButton().apply {
            // Remove all text and borders
            text = ""
            toolTipText = "Reset"

            // Set icon
            this.icon = icon

            // Remove default button styling
            isBorderPainted = false
            isFocusPainted = false
            isContentAreaFilled = false

            // Set size
            preferredSize = BUTTON_DIM
            minimumSize = BUTTON_DIM
            maximumSize = BUTTON_DIM

            // Set background colors
            background = C_BACKGR
            isOpaque = true

            // Add mouse listener for hover effects
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) {
                    background = C_BACKGR_HOVER
                }

                override fun mouseExited(e: java.awt.event.MouseEvent) {
                    background = C_BACKGR
                }
            })
        }

        button.addActionListener {
            controller.resetFigure(deactiveTools = true)
        }
        return button
    }

    private fun createSvgIcon(svgString: String, size: Dimension = Dimension(16, 16), color: Color = C_STROKE): Icon? {
        return try {
            val loader = SVGLoader()
            // Replace stroke and fill colors in SVG string
            val coloredSvg = svgString.replace(
                """stroke="none"""",
                """stroke="none" fill="${colorToHex(color)}""""
            )
            val inputStream = coloredSvg.byteInputStream()
            val document: SVGDocument = loader.load(inputStream) ?: return null

            val bufferedImage = java.awt.image.BufferedImage(
                size.width,
                size.height,
                java.awt.image.BufferedImage.TYPE_INT_ARGB
            )

            val graphics = bufferedImage.createGraphics()
            try {
                document.render(null, graphics, null)
            } finally {
                graphics.dispose()
            }

            ImageIcon(bufferedImage)
        } catch (e: Exception) {
            null
        }
    }

    private fun colorToHex(color: Color): String {
        return "#%02x%02x%02x".format(color.red, color.green, color.blue)
    }

    companion object {
        const val TOOLBAR_HEIGHT = 33
        val BUTTON_DIM = Dimension(22, 22)

        private val C_BACKGR = Color(247, 248, 250)
        private val C_STROKE = Color(110, 110, 110)
        private val C_BACKGR_HOVER = Color(218, 219, 221)
        private val C_BACKGR_SEL = Color(54, 89, 226)
        private val C_STROKE_SEL = Color.WHITE
    }
}