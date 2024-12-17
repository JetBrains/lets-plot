/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.util.demoUtils.batik

import demo.common.util.demoUtils.swing.PlotSpecsDemoWindowBase
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.awt.Color
import java.awt.Dimension
import javax.swing.JComponent

class PlotSpecsDemoWindowBatik(
    title: String,
    specList: List<MutableMap<String, Any>>,
    maxCol: Int = 3,
    plotSize: Dimension? = null,
    background: Color = Color.WHITE
) : PlotSpecsDemoWindowBase(
    "$title (Batik)",
    specList = specList,
    maxCol = maxCol,
    plotSize = plotSize,
    background = background
) {

    override fun createPlotComponent(rawSpec: MutableMap<String, Any>, plotSize: Dimension?): JComponent {
        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        val plotPanel = DefaultPlotPanelBatik(
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

        plotPanel.alignmentX = CENTER_ALIGNMENT
        return plotPanel
    }
}