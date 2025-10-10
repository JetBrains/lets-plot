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
import java.awt.Color
import java.awt.Dimension
import javax.swing.*

fun main() {
    SwingUtilities.invokeLater { createAndShowGui() }
}

private fun createAndShowGui() {
    val frame = JFrame("Toolbar, Plot and Scrollers").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = BorderLayout()
    }

    val columnComponent = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.LIGHT_GRAY

        // Add components to the column
        add(JLabel("Click 'Pan off' to enable mouse wheel zooming and to block scrolling."))
        add(JLabel("Click 'Pan on' to disable mouse wheel zooming and to enable scrolling."))

        val toolbar = SandboxToolbarAwt()
        add(toolbar.apply {
            alignmentX = JComponent.LEFT_ALIGNMENT
        })

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


        val fixedHeight = 500
        val plotWrapper = JPanel(BorderLayout()).apply {
            add(plotPanel, BorderLayout.CENTER)
            preferredSize = Dimension(preferredSize.width, fixedHeight)
            maximumSize = Dimension(Int.MAX_VALUE, fixedHeight)
            minimumSize = Dimension(0, fixedHeight)
            alignmentX = JComponent.LEFT_ALIGNMENT
        }
        add(plotWrapper)

        add(JPanel().apply {
            alignmentX = JComponent.LEFT_ALIGNMENT
        })

        // Instructions
        add(JLabel("Use Ctrl+Shift+Wheel to zoom, Ctrl+Shift+Drag to pan"), BorderLayout.SOUTH)
    }

    // Make the column scrollable vertically
    val scrollPane = JScrollPane(columnComponent).apply {
        verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    }

    frame.add(scrollPane, BorderLayout.CENTER)

    frame.apply {
        setSize(400, 400)
//        pack()
        setLocationRelativeTo(null) // Center on screen
        isVisible = true
    }
}

private fun createPlotPanel(rawSpec: MutableMap<String, Any>): JComponent {
    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)
    return DefaultPlotPanelBatik(
        processedSpec = processedSpec,
        preserveAspectRatio = false,
        preferredSizeFromPlot = false,
        repaintDelay = 300,
        computationMessagesHandler = { messages -> messages.forEach { println("[!] $it") } }
    )
}
