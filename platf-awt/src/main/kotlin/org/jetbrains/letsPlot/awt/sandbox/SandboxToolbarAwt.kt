/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.sandbox

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.*
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * A simple example implementation of an interactive plot toolbar for Swing applications.
 * 
 * This class demonstrates how to create a custom toolbar with pan, zoom, and reset functionality
 * for Lets-Plot figures in Swing-based applications. It provides a basic UI with text-based buttons
 * that can be easily customized for specific application needs.
 * 
 * ## Usage Example
 * ```kotlin
 * val toolbar = SandboxToolbarAwt()
 * val plotPanel = PlotPanel()
 * 
 * // Add toolbar to your UI
 * yourContainer.add(toolbar, BorderLayout.NORTH)
 * yourContainer.add(plotPanel, BorderLayout.CENTER)
 * 
 * // Attach toolbar to the plot's figure model
 * toolbar.attach(plotPanel.figureModel)
 * 
 * // Don't forget to detach when done
 * toolbar.detach()
 * ```
 * 
 * ## Customization
 * Developers integrating Lets-Plot into their Swing applications can copy this code and modify:
 * - Button appearance and styling
 * - Layout and positioning
 * - Error message handling
 *
 */
class SandboxToolbarAwt : JPanel() {

    private val toolbarSupport = object : FigureToolbarSupport() {
        override fun addToggleTool(tool: ToggleTool): ToggleToolView {
            return addSwingToolButton(tool)
        }

        override fun addResetButton(): ActionToolView {
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

    init {
        toolbarSupport.initializeUI()
    }

    /**
     * Attaches this toolbar to a figure model to enable interactive functionality.
     * 
     * Call this method after adding the toolbar to your UI and when you have
     * a plot figure ready for interaction.
     * 
     * @param figureModel The figure model of the plot to control
     */
    fun attach(figureModel: FigureModel) {
        toolbarSupport.attach(figureModel)
    }

    /**
     * Detaches this toolbar from its current figure model.
     * 
     * Call this method when the toolbar is no longer needed or when switching
     * to a different plot. This properly cleans up event listeners and resources.
     */
    fun detach() {
        toolbarSupport.detach()
    }

    private fun addSwingToolButton(tool: ToggleTool): ToggleToolView {
        val buttonText = tool.label
        val button = JButton("$buttonText off")
        this.add(button)
        return object : ToggleToolView {
            override fun setState(selected: Boolean) {
                button.text = "$buttonText ${if (selected) "on" else "off"}"
            }

            override fun onAction(handler: () -> Unit) {
                button.addActionListener { handler() }
            }
        }
    }

    private fun addSwingResetButton(): ActionToolView {
        val button = JButton("Reset")
        this.add(button)
        return object : ActionToolView {
            override fun onAction(handler: () -> Unit) {
                button.addActionListener { handler() }
            }
        }
    }
}