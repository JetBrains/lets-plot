/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.vis.swing.batik.DefaultPlotPanelBatik
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

fun main() {
    val plotSpecEditor = PlotSpecEditor()
    // Plot spec can be set by PLOT_SPEC env var via IDEA run configuration.
    // TODO: add pretty print for JsonSupport
    plotSpecEditor.plotSpec.text = System.getenv("PLOT_SPEC")
        ?: """
        {
            'kind': 'plot',
            'data': { 'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner'] },
            'mapping': { 'x': 'time', 'color': 'time', 'fill': 'time' },
            'layers': [ { 'geom': 'bar', 'alpha': '0.5' } ]
        }
        """.trimIndent()

    plotSpecEditor.evaluate()
    plotSpecEditor.isVisible = true
}


class PlotSpecEditor : JFrame("PlotSpec Editor") {
    private val plotPanel = JPanel()
    val plotSpec = JTextArea().apply {
        wrapStyleWord = true
        lineWrap = true
        autoscrolls = true
    }

    init {
        isResizable = false // temp. Incorrect plot size after resize.
        defaultCloseOperation = EXIT_ON_CLOSE
        preferredSize = Dimension(1200, 600)
        layout = GridLayout(1, 2)
        contentPane.add(
            JPanel().apply {
                layout = BorderLayout()
                add(JScrollPane(plotSpec), BorderLayout.CENTER)
                add(JButton("Evaluate").apply { addActionListener { evaluate() } }, BorderLayout.PAGE_END)
            })
        contentPane.add(plotPanel)
        pack()
    }

    fun evaluate() {
        plotPanel.components.forEach(plotPanel::remove)
        plotPanel.add(DefaultPlotPanelBatik(
            processedSpec = MonolithicCommon.processRawSpecs(parsePlotSpec(plotSpec.text), frontendOnly = false),
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
}
