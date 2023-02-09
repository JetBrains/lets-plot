/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.vis.demoUtils.swing.PlotResizableDemoWindowBase
import jetbrains.datalore.vis.swing.PlotComponentProvider
import jetbrains.datalore.vis.swing.PlotPanel
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.datalore.vis.swing.jfx.DefaultSwingContextJfx
import java.awt.Dimension
import javax.swing.JComponent

class PlotResizableDemoWindowJfx(
    title: String,
    private val plotAssembler: PlotAssembler,
    plotSize: Dimension = Dimension(500, 350)
) : PlotResizableDemoWindowBase(
    title,
    plotSize = plotSize
) {

    override fun createPlotComponent(plotSize: Dimension): JComponent {
        @Suppress("NAME_SHADOWING")
        val plotSize = DoubleVector(
            plotSize.getWidth(),
            plotSize.getHeight(),
        )

        return PlotPanel(
            plotComponentProvider = MyPlotComponentProvider(plotAssembler, plotSize),
            preferredSizeFromPlot = true,
            repaintDelay = 100,
            applicationContext = DefaultSwingContextJfx()
        )
    }

    private class MyPlotComponentProvider(
        private val plotAssembler: PlotAssembler,
        private val plotInitialSize: DoubleVector,
    ) : PlotComponentProvider {
        override fun getPreferredSize(containerSize: Dimension): Dimension {
            return containerSize
        }

        override fun createComponent(containerSize: Dimension?): JComponent {
            val plotSize = if (containerSize != null) {
                DoubleVector(
                    containerSize.getWidth(),
                    containerSize.getHeight()
                )
            } else {
                plotInitialSize
            }

            val plotSvgComponent = plotAssembler.createPlot()
            val plotContainer = PlotContainer(
                PlotSvgRoot(
                    plotSvgComponent,
                    liveMapCursorServiceConfig = null,
                    DoubleRectangle(DoubleVector.ZERO, plotSize)
                )
            )

            val component = SceneMapperJfxPanel(plotContainer.svg, stylesheets = emptyList())
            (component as DisposingHub).registerDisposable(plotContainer)
            return component
        }
    }
}