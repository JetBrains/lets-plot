/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.*
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

internal class SandboxToolbar : JPanel() {

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

    fun attach(figureModel: FigureModel) {
        toolbarSupport.attach(figureModel)
    }

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