/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.simple.batik

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.simple.SimplePlotPanel
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class PlotViewerWindow(
    title: String,
    private val rawSpec: MutableMap<String, Any>,
    private val winsowSize: Dimension? = null,
    private val preserveAspectRatio: Boolean = false
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

            if (winsowSize != null) {
                this.size = winsowSize
            } else {
                this.pack()
            }

            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    private fun createWindowContent() {
        var shownMessages = HashSet<String>()
        val messagesArea: JLabel = JLabel().apply {
            foreground = Color.BLUE
            isFocusable = true
        }

        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)

        val componentProvider = PlotComponentProviderBatik(
            processedSpec = processedSpec,
            preserveAspectRatio = preserveAspectRatio,
            computationMessagesHandler = { messages ->
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
        )

        val plotPanel = SimplePlotPanel(
            plotComponentProvider = componentProvider,
            preferredSizeFromPlot = winsowSize == null,
        )

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        messagesArea.alignmentX = Component.CENTER_ALIGNMENT

        this.rootPanel.add(plotPanel)
        this.rootPanel.add(messagesArea)
    }
}