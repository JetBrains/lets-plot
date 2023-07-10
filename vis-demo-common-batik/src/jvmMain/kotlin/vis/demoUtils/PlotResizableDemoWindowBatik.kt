/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.vis.demoUtils.swing.PlotResizableDemoWindowBase
import org.jetbrains.letsPlot.platf.batik.plot.util.BatikMapperComponent
import org.jetbrains.letsPlot.platf.awt.plot.component.PlotComponentProvider
import org.jetbrains.letsPlot.platf.awt.plot.component.PlotPanel
import org.jetbrains.letsPlot.platf.batik.plot.component.DefaultSwingContextBatik
import java.awt.Dimension
import javax.swing.JComponent

class PlotResizableDemoWindowBatik(
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
            applicationContext = DefaultSwingContextBatik()
        )
    }

    private class MyPlotComponentProvider(
        private val plotAssembler: PlotAssembler,
        private val plotInitialSize: DoubleVector
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

            val layoutInfo = plotAssembler.layoutByOuterSize(plotSize)

            val plotSvgComponent = plotAssembler.createPlot(layoutInfo)
            val plotContainer = PlotContainer(
                PlotSvgRoot(
                    plotSvgComponent,
                    liveMapCursorServiceConfig = null,
                    DoubleVector.ZERO
                )
            )

            val component = BatikMapperComponent(plotContainer.svg, BatikMapperComponent.DEF_MESSAGE_CALLBACK)
            (component as DisposingHub).registerDisposable(plotContainer)
            return component
        }
    }
}