/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.demoUtils.swing.PlotSpecsDemoWindowBase
import jetbrains.datalore.vis.swing.DefaultPlotPanel
import jetbrains.datalore.vis.swing.jfx.DefaultPlotComponentProviderJfx
import jetbrains.datalore.vis.swing.jfx.DefaultSwingContextJfx
import java.awt.Component
import java.awt.Dimension
import javax.swing.JComponent

class PlotSpecsDemoWindowJfx(
    title: String,
    specList: List<MutableMap<String, Any>>,
    private val stylesheets: List<String> = listOf(Style.JFX_PLOT_STYLESHEET),
    maxCol: Int = 3,
    plotSize: Dimension? = null,
) : PlotSpecsDemoWindowBase(
    title,
    specList = specList,
    maxCol = maxCol,
    plotSize = plotSize,
) {
    override fun createPlotComponent(rawSpec: MutableMap<String, Any>, plotSize: Dimension?): JComponent {
        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        val componentProvider = DefaultPlotComponentProviderJfx(
            processedSpec = processedSpec,
            preserveAspectRatio = false,
            executor = DefaultSwingContextJfx.JFX_EDT_EXECUTOR,
        ) { messages ->
            for (message in messages) {
                println("[Demo Plot Viewer] $message")
            }
        }

        val plotPanel = DefaultPlotPanel(
            plotComponentProvider = componentProvider,
            preferredSizeFromPlot = plotSize == null,
            refreshRate = 300,
            applicationContext = DefaultSwingContextJfx()
        )

        plotSize?.let {
            plotPanel.preferredSize = it
        }

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        return plotPanel
    }
}