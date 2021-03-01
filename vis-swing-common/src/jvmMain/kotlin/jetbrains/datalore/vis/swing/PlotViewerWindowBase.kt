/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

abstract class PlotViewerWindowBase(
    title: String,
    private val windowSize: Dimension? = null,
    private val refreshRate: Int = 300,  // ms
    private val applicationContext: ApplicationContext

) : JFrame(title) {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.background = Color.WHITE
        rootPanel.layout = BoxLayout(rootPanel, BoxLayout.Y_AXIS)
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        contentPane.add(rootPanel)
    }

    fun open() {
        SwingUtilities.invokeLater {
            createWindowContent()

            if (windowSize != null) {
                this.size = windowSize
            } else {
                this.pack()
            }

            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    protected abstract fun createPlotComponentProvider(
        computationMessagesHandler: (List<String>) -> Unit
    ): PlotComponentProvider

    private fun createWindowContent() {
        var shownMessages = HashSet<String>()
        val messagesArea: JLabel = JLabel().apply {
            foreground = Color.BLUE
            isFocusable = true
        }

        val componentProvider = createPlotComponentProvider { messages ->
            if (messages.isNotEmpty()) {
                val text = messages.joinToString(
                    separator = "<br>",
                    prefix = "<html>",
                    postfix = "</html>"
                )
                if (!shownMessages.contains(text)) {
                    shownMessages.add(text)
                    messagesArea.border = BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 0, 0, 0),
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)
                    )
                    messagesArea.text = text
                }
            }
        }

        val plotPanel = DefaultPlotPanel(
            plotComponentProvider = componentProvider,
            preferredSizeFromPlot = windowSize == null,
            refreshRate = refreshRate,
            applicationContext = applicationContext
        )

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        messagesArea.alignmentX = Component.CENTER_ALIGNMENT

        this.rootPanel.add(plotPanel)
        this.rootPanel.add(messagesArea)
    }
}