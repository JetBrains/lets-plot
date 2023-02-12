/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.vis.demoUtils.swing.PlotObjectsDemoWindowBase
import jetbrains.datalore.vis.swing.BatikMapperComponent
import java.awt.Dimension
import javax.swing.JComponent

class PlotObjectsDemoWindowBatik(
    title: String,
    plotList: List<PlotSvgComponent>,
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

        val component = BatikMapperComponent(plotContainer.svg, BatikMapperComponent.DEF_MESSAGE_CALLBACK)
        (component as DisposingHub).registerDisposable(plotContainer)
        return component
    }
}