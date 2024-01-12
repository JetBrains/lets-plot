/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.*

fun main() {
    // Plot spec can be set by PLOT_SPEC env var via IDEA run configuration.
    val spec = System.getenv("PLOT_SPEC")
        ?: """
        {
            'kind': 'plot',
            'data': { 'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner'] },
            'mapping': { 'x': 'time', 'color': 'time', 'fill': 'time' },
            'layers': [ { 'geom': 'bar', 'alpha': '0.5' } ]
        }
        """.trimIndent()

    // TODO: add pretty print for JsonSupport
    val plotSpecDebugger = PlotSpecDebugger()
    plotSpecDebugger.setSpec(spec)
    plotSpecDebugger.evaluate()
    plotSpecDebugger.isVisible = true
}


class PlotSpecDebugger : JFrame("PlotSpec Debugger") {
    private val specEditorPane = JScrollPane()
    private val plotPanel = JPanel()
    private val evaluateButton = JButton("Evaluate")
    private val plotSpecTextArea = JTextArea().apply {
        wrapStyleWord = true
        lineWrap = true
        autoscrolls = true
    }

    init {
        isResizable = true
        defaultCloseOperation = EXIT_ON_CLOSE
        preferredSize = Dimension(1400, 600)
        layout = null

        contentPane.apply {
            add(plotPanel)
            add(specEditorPane.apply {
                viewport.add(plotSpecTextArea)
            })
            add(evaluateButton.apply {
                addActionListener { evaluate() }
            })
        }

        addComponentListener(object : ComponentListener {
            override fun componentResized(e: ComponentEvent?) = doLayout(size)
            override fun componentMoved(e: ComponentEvent?) = Unit
            override fun componentShown(e: ComponentEvent?) = Unit
            override fun componentHidden(e: ComponentEvent?) = Unit
        })

        pack()
    }

    private fun doLayout(size: Dimension) {
        val systemMenuHeight = 40
        val evalButtonHeight = 30

        val specEditorPos = Point(10, 10)
        val specEditorSize = Dimension(400, size.height - systemMenuHeight - evalButtonHeight - specEditorPos.y)
        specEditorPane.bounds = Rectangle(specEditorPos, specEditorSize)

        val evaluatePos = Point(10, specEditorPos.y + specEditorSize.height)
        val evaluateSize = Dimension(specEditorSize.width, evalButtonHeight)
        evaluateButton.bounds = Rectangle(evaluatePos, evaluateSize)

        val plotPanelPos = Point(specEditorPos.x + specEditorSize.width + 10, 5)
        val plotPanelSize = Dimension(size.width - plotPanelPos.x - 10, size.height - systemMenuHeight)
        plotPanel.bounds = Rectangle(plotPanelPos, plotPanelSize)
        plotPanel.components.firstOrNull()?.let { it.preferredSize = plotPanelSize }

        preferredSize = size
    }

    fun evaluate() {
        plotPanel.components.forEach(plotPanel::remove)
        plotPanel.add(DefaultPlotPanelBatik(
            processedSpec = MonolithicCommon.processRawSpecs(
                parsePlotSpec(plotSpecTextArea.text),
                frontendOnly = false
            ),
            preferredSizeFromPlot = false,
            repaintDelay = 300,
            preserveAspectRatio = false,
        ) { messages ->
            for (message in messages) {
                println("[Demo Plot Viewer] $message")
            }
        }.apply {
            preferredSize = plotPanel.size
            alignmentX = CENTER_ALIGNMENT
        })
        pack()
    }

    fun setSpec(spec: String) {
        plotSpecTextArea.text = spec
    }
}
