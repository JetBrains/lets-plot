/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.demoUtils.swing.PlotSpecsDemoWindowBase
import jetbrains.datalore.vis.swing.jfx.DefaultPlotPanelJfx
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.JComponent

class PlotSpecsDemoWindowJfx(
    title: String,
    specList: List<MutableMap<String, Any>>,
    private val stylesheets: List<String> = listOf(Style.JFX_PLOT_STYLESHEET),
    maxCol: Int = 3,
    plotSize: Dimension? = null,
    background: Color = Color.WHITE
) : PlotSpecsDemoWindowBase(
    title,
    specList = specList,
    maxCol = maxCol,
    plotSize = plotSize,
    background = background
) {
    override fun createPlotComponent(rawSpec: MutableMap<String, Any>, plotSize: Dimension?): JComponent {
        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        val plotPanel = DefaultPlotPanelJfx(
            processedSpec = processedSpec,
            preferredSizeFromPlot = plotSize == null,
            repaintDelay = 300,
            preserveAspectRatio = false,
        ) { messages ->
            for (message in messages) {
                println("[Demo Plot Viewer] $message")
            }
        }

        plotSize?.let {
            plotPanel.preferredSize = it
        }

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        return plotPanel
    }
}