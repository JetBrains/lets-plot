/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.WithFigureModel
import org.jetbrains.letsPlot.core.util.PlotSizeHelper
import java.awt.Color
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Panel containing a plot component and a JLabel showing 'plot computation messages'.
 *
 * In IDEA plugin: inherit and implement 'com.intellij.openapi.Disposable'.
 */
abstract class DefaultPlotContentPane(
    processedSpec: MutableMap<String, Any>,
    private val preferredSizeFromPlot: Boolean,
    private val repaintDelay: Int,  // ms
    private val applicationContext: ApplicationContext

) : Disposable, WithFigureModel, JPanel() {

    final override val figureModel: FigureModel

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        createUI(processedSpec).let { (plotPanel, messagesArea) ->
            this.figureModel = plotPanel.figureModel
            val centeredPanel = CenteredPlotPanel(
                plotPanel = plotPanel,
                figurePanelDefaultSize = PlotSizeHelper.figurePanelSizeDefault(processedSpec),
            )
            this.add(centeredPanel)
            this.add(messagesArea)
        }
    }

    /**
     * In IDEA plugin: override and check for 'com.intellij.openapi.Disposable'.
     */
    override fun dispose() {
//        figureModel.dispose()

        for (component in components) {
            when (component) {
                is Disposable -> component.dispose()
            }
        }
        removeAll()
    }

    private fun createUI(processedSpec: MutableMap<String, Any>): Pair<PlotPanel, JLabel> {
        var shownMessages = HashSet<String>()
        val messagesArea: JLabel = JLabel().apply {
            foreground = Color.BLUE
            isFocusable = true
        }

        val plotPanel = createPlotPanel(
            processedSpec = processedSpec,
            preferredSizeFromPlot = preferredSizeFromPlot,
            repaintDelay = repaintDelay,
            applicationContext = applicationContext
        ) { messages ->
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

//        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        messagesArea.alignmentX = Component.CENTER_ALIGNMENT

        return Pair(plotPanel, messagesArea)
    }

    protected abstract fun createPlotPanel(
        processedSpec: MutableMap<String, Any>,
        preferredSizeFromPlot: Boolean,
        repaintDelay: Int,  // ms
        applicationContext: ApplicationContext,
        computationMessagesHandler: (List<String>) -> Unit
    ): PlotPanel
}