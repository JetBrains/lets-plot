/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.jfx

import jetbrains.datalore.vis.swing.ApplicationContext
import jetbrains.datalore.vis.swing.DefaultPlotContentPaneBase
import jetbrains.datalore.vis.swing.PlotComponentProvider
import jetbrains.datalore.vis.swing.PlotViewerWindowBase
import java.awt.Dimension
import javax.swing.JComponent

class PlotViewerWindowJfx(
    title: String,
    windowSize: Dimension? = null,
    private val rawSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean = false,
    private val refreshRate: Int = 300,  // ms
    private val applicationContext: ApplicationContext = DefaultSwingContextJfx()
) : PlotViewerWindowBase(
    title,
    windowSize = windowSize,
) {

    override fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent {
        return object : DefaultPlotContentPaneBase(
            rawSpec = rawSpec,
            preferredSizeFromPlot = preferredSizeFromPlot,
            refreshRate = refreshRate,
            applicationContext = applicationContext
        ) {
            override fun createPlotComponentProvider(
                processedSpec: MutableMap<String, Any>,
                computationMessagesHandler: (List<String>) -> Unit
            ): PlotComponentProvider {
                return this@PlotViewerWindowJfx.createPlotComponentProvider(
                    processedSpec,
                    computationMessagesHandler
                )
            }
        }
    }

    private fun createPlotComponentProvider(
        processedSpec: MutableMap<String, Any>,
        computationMessagesHandler: (List<String>) -> Unit
    ): PlotComponentProvider {
        return DefaultPlotComponentProviderJfx(
            processedSpec = processedSpec,
            preserveAspectRatio = preserveAspectRatio,
            executor = DefaultSwingContextJfx.JFX_EDT_EXECUTOR,
            computationMessagesHandler = computationMessagesHandler
        )
    }
}