/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.jfx

import jetbrains.datalore.plot.MonolithicCommon
import org.jetbrains.letsPlot.platf.awt.plot.component.ApplicationContext
import org.jetbrains.letsPlot.platf.awt.plot.component.DefaultPlotContentPane
import org.jetbrains.letsPlot.platf.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.platf.awt.plot.component.PlotViewerWindowBase
import java.awt.Dimension
import javax.swing.JComponent

class PlotViewerWindowJfx(
    title: String,
    windowSize: Dimension? = null,
    private val rawSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean = false,
    private val repaintDelay: Int = 300,  // ms
    private val applicationContext: ApplicationContext = DefaultSwingContextJfx()
) : PlotViewerWindowBase(
    title,
    windowSize = windowSize,
) {

    override fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        return object : DefaultPlotContentPane(
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
                return DefaultPlotPanelJfx(
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