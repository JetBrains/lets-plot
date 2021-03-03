/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

class PlotObjectsDemoWindowJfx(
    title: String,
    private val plotList: List<Plot>,
    private val stylesheets: List<String> = listOf(Style.JFX_PLOT_STYLESHEET),
    private val maxCol: Int = 2,
    private val plotSize: Dimension = Dimension(500, 350)
) : JFrame(title) {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, plotList.size))
//        rootPanel.background = Color.WHITE
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        // Fixed plot size
        val scrollPane = JScrollPane(
            rootPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED

        )
        contentPane.add(scrollPane)
    }

    fun open() {
        SwingUtilities.invokeLater {
            createWindowContent()

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    private fun createWindowContent() {
        for (plot in plotList) {
            rootPanel.add(createPlotComponent(plot, plotSize))
        }
    }

    private fun createPlotComponent(plot: Plot, plotSize: Dimension): JComponent {
        val plotContainer = PlotContainer(
            plot, ValueProperty(
                DoubleVector(
                    plotSize.getWidth(),
                    plotSize.getHeight(),
                )
            )
        )

        plotContainer.ensureContentBuilt()
        return SceneMapperJfxPanel(plotContainer.svg, stylesheets)
    }
}