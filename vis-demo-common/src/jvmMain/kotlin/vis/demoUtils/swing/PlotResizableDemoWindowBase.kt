/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.swing

import jetbrains.datalore.plot.builder.Plot
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

abstract class PlotResizableDemoWindowBase(
    title: String,
    private val plot: Plot,
    private val plotSize: Dimension
) : JFrame(title) {

    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout()
//        rootPanel.background = Color.WHITE
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        contentPane.add(rootPanel)
    }

    fun open() {
        SwingUtilities.invokeLater {
            rootPanel.add(createPlotComponent(plot, plotSize))

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    protected abstract fun createPlotComponent(plot: Plot, plotSize: Dimension): JComponent
}