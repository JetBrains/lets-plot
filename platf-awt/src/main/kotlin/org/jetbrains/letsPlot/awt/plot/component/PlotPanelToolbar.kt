/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import com.github.weisj.jsvg.SVGDocument
import com.github.weisj.jsvg.parser.SVGLoader
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.*
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.res.ToolbarIcons
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*

internal class PlotPanelToolbar : JPanel() {

    private val toolbarSupport = object : FigureToolbarSupport() {
        override fun addToggleTool(tool: ToggleTool): ToggleToolModel {
            return addSwingToolButton(tool)
        }

        override fun addResetButton(): ActionToolModel {
            return addSwingResetButton()
        }

        override fun errorMessageHandler(message: String) {
            SwingUtilities.invokeLater {
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Situation",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private val innerContainer: JPanel

    init {
        layout = FlowLayout(FlowLayout.CENTER, 0, 2)  // 2px vertical gap to center the inner container

        preferredSize = Dimension(preferredSize.width, TOOLBAR_HEIGHT)
        minimumSize = Dimension(minimumSize.width, TOOLBAR_HEIGHT)
        maximumSize = Dimension(maximumSize.width, TOOLBAR_HEIGHT)

        isOpaque = false

        // Create an inner container with a custom rounded background
        innerContainer = object : JPanel(FlowLayout(FlowLayout.CENTER, 6, 0)) {
            override fun paintComponent(g: java.awt.Graphics) {
                val g2 = g.create() as java.awt.Graphics2D
                g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
                )

                g2.color = C_BACKGR_TRANSPARENT
                g2.fillRoundRect(0, 0, width, height, 16, 16)

                // Draw border
                g2.color = Color(200, 200, 200)
                g2.stroke = java.awt.BasicStroke(1f)
                g2.drawRoundRect(0, 0, width - 1, height - 1, 16, 16)

                g2.dispose()
            }
        }.apply {
            isOpaque = false
            // Add padding equivalent
            border = BorderFactory.createEmptyBorder(3, 3, 3, 3)
        }

        // Add the inner container to the main toolbar
        add(innerContainer)

        toolbarSupport.initializeUI()
    }

    fun attach(figureModel: FigureModel) {
        toolbarSupport.attach(figureModel)
    }

    fun detach() {
        toolbarSupport.detach()
    }

    private fun addSwingToolButton(tool: ToggleTool): ToggleToolModel {
        val iconSvg = tool.spec["icon"] as? String
        val normalIcon = createSvgIcon(
            iconSvg, color = C_STROKE,
            backgroundColor = Color(0, 0, 0, 0) // Fully transparent
        )
        val hoverIcon = createSvgIcon(iconSvg, color = C_STROKE, backgroundColor = C_BACKGR_HOVER)
        val selectedIcon = createSvgIcon(iconSvg, color = C_STROKE_SEL, backgroundColor = C_BACKGR_SEL)

        // Create JButton and add it to the container
        val button = createStyledButton(
            normalIcon = normalIcon,
            hoverIcon = hoverIcon,
            toolTipText = tool.label,
            selected = { tool.active }
        )

        innerContainer.add(button)

        return object : ToggleToolModel() {
            override fun setState(selected: Boolean) {
                button.icon = if (selected) selectedIcon else normalIcon
            }
        }.also { toolModel ->
            button.addActionListener {
                toolModel.action()
            }
        }
    }

    private fun addSwingResetButton(): ActionToolModel {
        val normalIcon = createSvgIcon(
            ToolbarIcons.RESET,
            color = C_STROKE,
            backgroundColor = Color(0, 0, 0, 0) // Fully transparent
        )
        val hoverIcon = createSvgIcon(ToolbarIcons.RESET, color = C_STROKE, backgroundColor = C_BACKGR_HOVER)

        // Create JButton and add it to the container
        val button = createStyledButton(
            normalIcon = normalIcon,
            hoverIcon = hoverIcon,
            toolTipText = "Reset",
            selected = { false }
        )

        innerContainer.add(button)

        return ActionToolModel().also { toolModel ->
            button.addActionListener { toolModel.action() }
        }
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

    private fun createSvgIcon(
        svgString: String?,
        size: Dimension = Dimension(16, 16),
        color: Color = C_STROKE,
        backgroundColor: Color? = null
    ): Icon {
        val bufferedImage = java.awt.image.BufferedImage(
            BUTTON_DIM.width,  // Use button size for the full icon
            BUTTON_DIM.height,
            java.awt.image.BufferedImage.TYPE_INT_ARGB
        )

        val graphics = bufferedImage.createGraphics()
        try {
            graphics.setRenderingHint(
                java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            )

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

        // Transparency settings
        private const val ALPHA = (255 * 0.8).toInt()

        // C_BACKGR with an alpha channel which on a white background looks the same as the solid C_BACKGR
        // and slightly darkens any darker background.
        private val C_BACKGR_TRANSPARENT = Color(
            ((C_BACKGR.red - 255 * (255 - ALPHA) / 255.0) * 255.0 / ALPHA).toInt().coerceIn(0, 255),
            ((C_BACKGR.green - 255 * (255 - ALPHA) / 255.0) * 255.0 / ALPHA).toInt().coerceIn(0, 255),
            ((C_BACKGR.blue - 255 * (255 - ALPHA) / 255.0) * 255.0 / ALPHA).toInt().coerceIn(0, 255),
            ALPHA
        )
    }
}