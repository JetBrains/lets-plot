/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.swing

import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import javax.swing.*
import kotlin.math.min

abstract class PlotSpecsDemoWindowBase(
    title: String,
    private val specList: List<MutableMap<String, Any>>,
    private val maxCol: Int = 3,
    private val plotSize: Dimension? = null,
    background: Color,
) : JFrame(title) {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, specList.size))
        rootPanel.background = background
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        addWindowListener(object : WindowListener {
            override fun windowActivated(e: WindowEvent?) {
//                repaint()
            }

            override fun windowOpened(e: WindowEvent?) {}
            override fun windowClosing(e: WindowEvent?) {}
            override fun windowClosed(e: WindowEvent?) {}
            override fun windowIconified(e: WindowEvent?) {}
            override fun windowDeiconified(e: WindowEvent?) {}
            override fun windowDeactivated(e: WindowEvent?) {}
        })

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
        }
    }

    private fun createWindowContent() {
        for (spec in specList) {
            rootPanel.add(createPlotComponent(spec, plotSize))
        }
    }

    protected abstract fun createPlotComponent(rawSpec: MutableMap<String, Any>, plotSize: Dimension?): JComponent
}