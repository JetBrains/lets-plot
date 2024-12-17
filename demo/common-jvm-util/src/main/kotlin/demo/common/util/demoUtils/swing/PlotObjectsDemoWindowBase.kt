/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.util.demoUtils.swing

import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponent
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

abstract class PlotObjectsDemoWindowBase(
    title: String,
    private val plotList: List<PlotSvgComponent>,
    private val maxCol: Int,
    private val plotSize: Dimension
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

    protected abstract fun createPlotComponent(plot: PlotSvgComponent, plotSize: Dimension): JComponent
}