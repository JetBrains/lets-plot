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
        val normalIcon = createSvgIcon(iconSvg, color = C_STROKE, backgroundColor = C_BACKGR)
        val hoverIcon = createSvgIcon(iconSvg, color = C_STROKE, backgroundColor = C_BACKGR_HOVER)
        val selectedIcon = createSvgIcon(iconSvg, color = C_STROKE_SEL, backgroundColor = C_BACKGR_SEL)

        val button = createStyledButton(
            normalIcon = normalIcon,
            hoverIcon = hoverIcon, 
            toolTipText = tool.label,
            selected = { tool.active }
        )

        val view = object : ToggleToolView {
            override fun setState(selected: Boolean) {
                button.icon = if (selected) selectedIcon else normalIcon
            }

            override fun onAction(handler: () -> Unit) {
                button.addActionListener { handler() }
            }
        }
        controller.registerTool(tool, view)
        return button
    }

    private fun resetButton(): JButton {
        val normalIcon = createSvgIcon(ToolbarIcons.RESET, color = C_STROKE, backgroundColor = C_BACKGR)
        val hoverIcon = createSvgIcon(ToolbarIcons.RESET, color = C_STROKE, backgroundColor = C_BACKGR_HOVER)

        val button = createStyledButton(
            normalIcon = normalIcon,
            hoverIcon = hoverIcon,
            toolTipText = "Reset",
            selected = { false }
        )

        button.addActionListener {
            controller.resetFigure(deactiveTools = true)
        }
        return button
    }

    private fun createStyledButton(
        normalIcon: Icon,
        hoverIcon: Icon,
        toolTipText: String,
        selected: () -> Boolean
    ): JButton {
        return JButton().apply {
            text = ""
            this.toolTipText = toolTipText
            icon = normalIcon

            // Remove default button styling
            isBorderPainted = false
            isFocusPainted = false
            isContentAreaFilled = false
            isRolloverEnabled = false

            preferredSize = BUTTON_DIM
            minimumSize = BUTTON_DIM
            maximumSize = BUTTON_DIM

            isOpaque = false  // Transparent since the icon has a background

            // Hover effects - swap icons
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) {
                    if (!selected()) {
                        icon = hoverIcon
                    }
                }

                override fun mouseExited(e: java.awt.event.MouseEvent) {
                    if (!selected()) {
                        icon = normalIcon
                    }
                }
            })
        }
    }

    private fun createSvgIcon(svgString: String?, size: Dimension = Dimension(16, 16), color: Color = C_STROKE, backgroundColor: Color? = null): Icon {
        val bufferedImage = java.awt.image.BufferedImage(
            BUTTON_DIM.width,  // Use button size for the full icon
            BUTTON_DIM.height,
            java.awt.image.BufferedImage.TYPE_INT_ARGB
        )

        val graphics = bufferedImage.createGraphics()
        try {
            graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
            
            // Draw a rounded background
            graphics.color = backgroundColor ?: C_BACKGR
            graphics.fillRoundRect(0, 0, BUTTON_DIM.width, BUTTON_DIM.height, 8, 8)
            
            // Try to render the SVG icon if provided
            svgString?.let { svg ->
                try {
                    val loader = SVGLoader()
                    // Replace stroke and fill colors in SVG string
                    val coloredSvg = svg.replace(
                        """stroke="none"""",
                        """stroke="none" fill="${colorToHex(color)}""""
                    )
                    val inputStream = coloredSvg.byteInputStream()
                    val document: SVGDocument? = loader.load(inputStream)
                    
                    document?.let {
                        // Center the SVG icon
                        val iconX = (BUTTON_DIM.width - size.width) / 2
                        val iconY = (BUTTON_DIM.height - size.height) / 2
                        graphics.translate(iconX, iconY)
                        it.render(null, graphics, null)
                    }
                } catch (e: Exception) {
                    // SVG rendering failed, but we still have the background
                }
            }
        } finally {
            graphics.dispose()
        }

        return ImageIcon(bufferedImage)
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
        private val C_BACKGR_SEL = Color(69, 114, 232)
        private val C_STROKE_SEL = Color.WHITE
    }
}