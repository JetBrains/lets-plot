/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.swing

import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

abstract class PlotResizableDemoWindowBase(
    title: String,
    private val plotSize: Dimension
) : JFrame(title) {

    private val rootPanel: JPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout()
//        rootPanel.background = Color.WHITE
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        contentPane.add(rootPanel)
    }

    fun open() {
        SwingUtilities.invokeLater {
            rootPanel.add(createPlotComponent(plotSize))

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    protected abstract fun createPlotComponent(plotSize: Dimension): JComponent
}