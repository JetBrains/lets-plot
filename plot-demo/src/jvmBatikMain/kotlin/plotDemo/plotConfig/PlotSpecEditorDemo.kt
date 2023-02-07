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
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextArea

fun main() {
    val window = Window()
    window.isResizable = false // temp. Incorrect plot size after resize.
    window.isVisible = true
}


class Window: JFrame("Spec Demo") {
    private val plotSpec = JTextArea(
        """
        {
            'kind': 'plot',
            'data': { 'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner'] },
            'mapping': { 'x': 'time', 'color': 'time', 'fill': 'time' },
            'layers': [
                { 'geom': 'bar', 'alpha': '0.5' }
            ]
        }""".trimIndent()
    )
    private val plotPanel = JPanel()
    private val controlPanel = JPanel().apply {
        layout =  BorderLayout()
        add(plotSpec, BorderLayout.CENTER)
        add(JButton("Evaluate").apply {
            addActionListener {
                evaluate(plotSpec.text)
            }
        }, BorderLayout.PAGE_END)
    }

    init {
        preferredSize = Dimension(1200, 600)
        layout = GridLayout(1, 2)
        contentPane.add(controlPanel)
        contentPane.add(plotPanel)
        pack()

        evaluate(plotSpec.text)
    }

    private fun evaluate(plotSpec: String) {
        plotPanel.components.forEach(plotPanel::remove)
        plotPanel.add(DefaultPlotPanelBatik(
            processedSpec = MonolithicCommon.processRawSpecs(parsePlotSpec(plotSpec), frontendOnly = false),
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
