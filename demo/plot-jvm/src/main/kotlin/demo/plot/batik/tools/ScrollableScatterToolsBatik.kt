/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.plot.component.ApplicationContext
import org.jetbrains.letsPlot.awt.plot.component.DefaultPlotContentPane
import org.jetbrains.letsPlot.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.awt.sandbox.SandboxToolbarAwt
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.batik.plot.component.DefaultSwingContextBatik
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*
import kotlin.math.PI
import kotlin.math.sin

fun main() {
    SwingUtilities.invokeLater { createAndShowGui() }
}

fun plotWithToolbar(rawSpec: MutableMap<String, Any>): JComponent {
    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)
    val figureComponent = object : DefaultPlotContentPane(
        processedSpec = processedSpec,
        preferredSizeFromPlot = true,
        repaintDelay = 300,  // ms
        applicationContext = DefaultSwingContextBatik()
    ) {
        override fun createPlotPanel(
            processedSpec: MutableMap<String, Any>,
            preferredSizeFromPlot: Boolean,
            repaintDelay: Int,
            applicationContext: ApplicationContext,
            computationMessagesHandler: (List<String>) -> Unit
        ): PlotPanel {
            return DefaultPlotPanelBatik(
                processedSpec = processedSpec,
                preserveAspectRatio = false,
                preferredSizeFromPlot = preferredSizeFromPlot,
                repaintDelay = repaintDelay,
                computationMessagesHandler = computationMessagesHandler
            )
        }
    }

    val contentPanel = JPanel(BorderLayout())
    val figureModel = figureComponent.figureModel
    val toolbar = SandboxToolbarAwt()
    toolbar.attach(figureModel)

    contentPanel.add(toolbar, BorderLayout.NORTH)
    contentPanel.add(figureComponent, BorderLayout.CENTER)
    return contentPanel
}

private fun createAndShowGui() {
    val frame = JFrame("Scroll Demo").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = BorderLayout()
    }

    val container = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.LIGHT_GRAY

        add(JLabel("Click 'Pan off' to enable mouse wheel zooming and to block scrolling.", SwingConstants.LEFT))
        add(JLabel("Click 'Pan on' to disable mouse wheel zooming and to enable scrolling."))

        add(plotWithToolbar(ScatterModel().plotSpec()))
        add(Box.createVerticalStrut(20))
    }

    val scrollPane = JScrollPane(container).apply {
        verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    }

    frame.add(scrollPane, BorderLayout.CENTER)

    frame.apply {
        setSize(800, 450)
        setLocationRelativeTo(null) // Center on screen
        isVisible = true
    }
}

@Suppress("DuplicatedCode")
private class ScatterModel {
    fun plotSpec(): MutableMap<String, Any> {
        val n = 50
        val step = 4 * PI / n
        val x = List(n) { it * step }
        val y = List(n) { sin(it * step) }

        val spec = """
            {
              'kind': 'plot',
              'ggsize': { 'width': 800, 'height': 500 },
              'data': {
                'x': $x,
                'y': $y
              },
              'mapping': {
                'x': 'x',
                'y': 'y',
                'color': 'y'
              },
              'layers': [
                {
                  'geom': 'point',
                  'sampling': 'none'
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }
}
