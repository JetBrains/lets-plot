/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.jfx.demoUtils

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import demo.common.util.demoUtils.swing.PlotObjectsDemoWindowBase
import org.jetbrains.letsPlot.jfx.plot.util.SceneMapperJfxPanel
import java.awt.Dimension
import javax.swing.JComponent

class PlotObjectsDemoWindowJfx(
    title: String,
    plotList: List<PlotSvgComponent>,
    private val stylesheets: List<String> = emptyList(),
    maxCol: Int = 2,
    plotSize: Dimension = Dimension(500, 350)
) : PlotObjectsDemoWindowBase(
    title,
    plotList = plotList,
    maxCol = maxCol,
    plotSize = plotSize
) {
    override fun createPlotComponent(plot: PlotSvgComponent, plotSize: Dimension): JComponent {
        val plotContainer = PlotContainer(
            PlotSvgRoot(
                plot,
                liveMapCursorServiceConfig = null,
                DoubleVector.ZERO
            )
        )

        val component = SceneMapperJfxPanel(plotContainer.svg, stylesheets)
        (component as DisposingHub).registerDisposable(plotContainer)
        return component
    }
}