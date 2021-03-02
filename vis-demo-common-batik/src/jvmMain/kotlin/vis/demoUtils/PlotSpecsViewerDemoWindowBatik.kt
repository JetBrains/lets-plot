/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.DefaultPlotPanel
import jetbrains.datalore.vis.swing.batik.DefaultPlotComponentProviderBatik
import jetbrains.datalore.vis.swing.batik.DefaultSwingContextBatik
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

class PlotSpecsViewerDemoWindowBatik(
    title: String,
    private val specList: List<MutableMap<String, Any>>,
    private val maxCol: Int = 3,
    private val plotSize: Dimension? = null,
) : JFrame(title) {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.background = Color.WHITE
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        if (plotSize == null) {
            contentPane.add(rootPanel)
        } else {
            // Fixed plot size
            val scrollPane = JScrollPane(
                rootPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED

            )
            contentPane.add(scrollPane)
        }
    }

    fun open() {
        SwingUtilities.invokeLater {
            rootPanel.layout = GridLayout(0, min(maxCol, specList.size))

            createWindowContent()

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    private fun createWindowContent() {
        for (spec in specList) {
            rootPanel.add(createPlotComponent(spec, plotSize))
        }
    }

    private fun createPlotComponent(rawSpec: MutableMap<String, Any>, plotSize: Dimension?): JComponent {
        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        val componentProvider = DefaultPlotComponentProviderBatik(
            processedSpec = processedSpec,
            preserveAspectRatio = false,
            executor = DefaultSwingContextBatik.AWT_EDT_EXECUTOR,
        ) { messages ->
            for (message in messages) {
                println("[Demo Plot Viewer] $message")
            }
        }

        val plotPanel = DefaultPlotPanel(
            plotComponentProvider = componentProvider,
            preferredSizeFromPlot = plotSize == null,
            refreshRate = 300,
            applicationContext = DefaultSwingContextBatik()
        )

        plotSize?.let {
            plotPanel.preferredSize = it
        }

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        return plotPanel
    }

    companion object {
        fun show(
            title: String,
            specList: List<MutableMap<String, Any>>,
            maxCol: Int = 3,
            plotSize: Dimension? = null,
        ) {
            PlotSpecsViewerDemoWindowBatik(title, specList, maxCol, plotSize).open()
        }
    }
}