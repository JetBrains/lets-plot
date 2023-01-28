/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.PlotSvgContainer
import jetbrains.datalore.vis.demoUtils.swing.PlotResizableDemoWindowBase
import jetbrains.datalore.vis.swing.PlotComponentProvider
import jetbrains.datalore.vis.swing.PlotPanel
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.datalore.vis.swing.jfx.DefaultSwingContextJfx
import java.awt.Dimension
import javax.swing.JComponent

class PlotResizableDemoWindowJfx(
    title: String,
    plot: PlotSvgComponent,
    plotSize: Dimension = Dimension(500, 350)
) : PlotResizableDemoWindowBase(
    title,
    plot = plot,
    plotSize = plotSize
) {

    override fun createPlotComponent(plot: PlotSvgComponent, plotSize: Dimension): JComponent {
        @Suppress("NAME_SHADOWING")
        val plotSize = DoubleVector(
            plotSize.getWidth(),
            plotSize.getHeight(),
        )

        val plotContainer = PlotContainer(
            PlotSvgContainer(
                plot,
                DoubleRectangle(DoubleVector.ZERO, plotSize)
            )
        )

        return PlotPanel(
            plotComponentProvider = MyPlotComponentProvider(plotContainer, plotSize),
            preferredSizeFromPlot = true,
            repaintDelay = 100,
            applicationContext = DefaultSwingContextJfx()
        )
    }

    private class MyPlotComponentProvider(
        private val plotContainer: PlotContainer,
        private val plotSize: DoubleVector,
    ) : PlotComponentProvider {
        override fun getPreferredSize(containerSize: Dimension): Dimension {
            return containerSize
        }

        override fun createComponent(containerSize: Dimension?): JComponent {
            plotContainer.clearContent()
            containerSize?.run {
                plotContainer.resize(DoubleVector(getWidth(), getHeight()))
            }
            plotContainer.ensureContentBuilt()
            return SceneMapperJfxPanel(plotContainer.svg, stylesheets = emptyList())
        }
    }
}