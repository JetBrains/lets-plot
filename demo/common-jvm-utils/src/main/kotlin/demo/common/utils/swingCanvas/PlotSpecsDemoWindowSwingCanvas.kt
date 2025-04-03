/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.swingCanvas

import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.raster.builderLW.MonolithicSkiaLW
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

class PlotSpecsDemoWindowSwingCanvas(
    title: String,
    private val specs: List<MutableMap<String, Any>>,
    maxCol: Int = 3,
    private val plotSize: Dimension? = null,
    background: Color = Color.WHITE,
) : JFrame("$title (Swing Canvas)") {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, specs.size))
        rootPanel.background = background
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
            createWindowContent()

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
            size = Dimension(600, 600)
        }
    }

    private fun createWindowContent() {
        val preferredSizeFromPlot = (plotSize == null)
        val components = specs.map { rawSpec ->
            val figureComponent = rawSpec.createCanvas(
                preferredSizeFromPlot = preferredSizeFromPlot
            ) { messages ->
                for (message in messages) {
                    println("[Demo Plot Viewer] $message")
                }
            }

            plotSize?.let {
                figureComponent.preferredSize = it
            }

            figureComponent.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
            figureComponent
        }

        components.forEach { rootPanel.add(it) }
    }

    fun MutableMap<String, Any>.createCanvas(
        preserveAspectRatio: Boolean = false,
        preferredSizeFromPlot: Boolean = false,
        repaintDelay: Int = 300,  // ms,
        computationMessagesHandler: (List<String>) -> Unit
    ): JComponent {
        val rawSpec = this
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        val vm = MonolithicSkiaLW.buildPlotFromProcessedSpecs(
            processedSpec,
            computationMessagesHandler
        )

        val view = SwingSvgCanvasView().apply {
            this.eventDispatcher = vm.eventDispatcher
            this.svg = vm.svg
        }

        return view.container
    }

}

