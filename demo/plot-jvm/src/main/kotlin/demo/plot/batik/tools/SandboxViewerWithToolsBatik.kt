/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.tools

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
import org.jetbrains.letsPlot.batik.plot.component.PlotViewerWindowBatik
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel

class SandboxViewerWithToolsBatik(
    title: String,
    windowSize: Dimension? = null,
    private val rawSpec: MutableMap<String, Any>,
) : PlotViewerWindowBatik(
    title = title,
    windowSize = windowSize,
    rawSpec = rawSpec
) {

    override fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent {
        val contentPanel = JPanel(BorderLayout())
        val figureComponent = super.createWindowContent(preferredSizeFromPlot)

        val toolbar = SandboxToolbar(figureComponent.getClientProperty(FigureModel::class) as FigureModel)

        contentPanel.add(toolbar, BorderLayout.NORTH)
        contentPanel.add(figureComponent, BorderLayout.CENTER)
        return contentPanel
    }
}