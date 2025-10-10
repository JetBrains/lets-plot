/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import org.jetbrains.letsPlot.awt.sandbox.SandboxToolbarAwt
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.WithFigureModel
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater { createAndShowGui() }
}

private fun createAndShowGui() {
    val frame = JFrame("Toolbar and Plot").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = BorderLayout()
    }

    // Toolbar
    val toolbar = SandboxToolbarAwt()
    frame.add(toolbar, BorderLayout.NORTH)

    // Plot
    val plotPanel = createPlotPanel(ScatterModel().plotSpec())
    val figureModel = (plotPanel as WithFigureModel).figureModel
    toolbar.attach(figureModel)

    // Figure default interactions
    val defaultInteractions = listOf(
        mapOf(
            ToolInteractionSpec.NAME to ToolInteractionSpec.WHEEL_ZOOM,
            ToolInteractionSpec.KEY_MODIFIERS to listOf(
                ToolInteractionSpec.KeyModifier.CTRL,
                ToolInteractionSpec.KeyModifier.SHIFT
            )
        ),
        mapOf(
            ToolInteractionSpec.NAME to ToolInteractionSpec.DRAG_PAN,
            ToolInteractionSpec.KEY_MODIFIERS to listOf(
                ToolInteractionSpec.KeyModifier.CTRL,
                ToolInteractionSpec.KeyModifier.SHIFT
            )
        )
    )

    figureModel.setDefaultInteractions(defaultInteractions)

    frame.add(plotPanel, BorderLayout.CENTER)

    // Instructions
    frame.add(JLabel("Use Ctrl+Shift+Wheel to zoom, Ctrl+Shift+Drag to pan"), BorderLayout.SOUTH)

    frame.apply {
//        setSize(400, 400)
        pack()
        setLocationRelativeTo(null) // Center on screen
        isVisible = true
    }
}

private fun createPlotPanel(rawSpec: MutableMap<String, Any>): JComponent {
    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)
    return DefaultPlotPanelBatik(
        processedSpec = processedSpec,
        preserveAspectRatio = false,
        preferredSizeFromPlot = true,
        repaintDelay = 300,
        computationMessagesHandler = { messages -> messages.forEach { println("[!] $it") } }
    )
}
