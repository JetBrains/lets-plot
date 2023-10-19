/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.customErrorComponent

import org.jetbrains.letsPlot.awt.plot.component.ApplicationContext
import org.jetbrains.letsPlot.awt.plot.component.DefaultPlotContentPane
import org.jetbrains.letsPlot.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.batik.plot.component.PlotViewerWindowBatik
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.awt.Dimension
import javax.swing.JComponent

class MyViewerWindowBatik(
    title: String,
    windowSize: Dimension? = null,
    private val rawSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean = false,
    private val repaintDelay: Int = 300,  // ms,
) : PlotViewerWindowBatik(
    title = title,
    windowSize = windowSize,
    rawSpec = rawSpec,
    preserveAspectRatio = preserveAspectRatio,
    repaintDelay = repaintDelay,
) {

    override fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        return object : DefaultPlotContentPane(
            processedSpec = processedSpec,
            preferredSizeFromPlot = preferredSizeFromPlot,
            repaintDelay = repaintDelay,
            applicationContext = MY_APP_CONTEXT
        ) {
            override fun createPlotPanel(
                processedSpec: MutableMap<String, Any>,
                preferredSizeFromPlot: Boolean,
                repaintDelay: Int,
                applicationContext: ApplicationContext,
                computationMessagesHandler: (List<String>) -> Unit
            ): PlotPanel {
                return MyPlotPanelBatik(
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
