/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.util

import org.jetbrains.letsPlot.awt.plot.component.ApplicationContext
import org.jetbrains.letsPlot.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.awt.plot.swing.SwingAppContext
import org.jetbrains.letsPlot.awt.plot.swing.SwingPlotPanel
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.awt.Dimension
import javax.swing.JComponent

open class SimplePlotViewerWindow(
    title: String,
    windowSize: Dimension? = null,
    private val rawSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean = false,
    private val repaintDelay: Int = 300,  // ms,
    private val applicationContext: ApplicationContext = SwingAppContext()
) : PlotViewerWindowBase(
    title,
    windowSize = windowSize,
) {

    override fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec)
        return object : PlotContentPaneBase(
            processedSpec = processedSpec,
            preferredSizeFromPlot = preferredSizeFromPlot,
            repaintDelay = repaintDelay,
            applicationContext = applicationContext
        ) {
            override fun createPlotPanel(
                processedSpec: MutableMap<String, Any>,
                preferredSizeFromPlot: Boolean,
                repaintDelay: Int,
                applicationContext: ApplicationContext,
                computationMessagesHandler: (List<String>) -> Unit
            ): PlotPanel {
                return SwingPlotPanel(
                    processedSpec = processedSpec,
                    preserveAspectRatio = preserveAspectRatio,
                    preferredSizeFromPlot = preferredSizeFromPlot,
                    repaintDelay = repaintDelay,
                    computationMessagesHandler = computationMessagesHandler
                )
            }
        }
    }
}
