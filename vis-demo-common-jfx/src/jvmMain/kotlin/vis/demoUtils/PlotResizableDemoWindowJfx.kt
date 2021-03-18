/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.demoUtils.swing.PlotResizableDemoWindowBase
import jetbrains.datalore.vis.swing.PlotComponentProvider
import jetbrains.datalore.vis.swing.PlotPanel
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.datalore.vis.swing.jfx.DefaultSwingContextJfx
import java.awt.Dimension
import javax.swing.JComponent

class PlotResizableDemoWindowJfx(
    title: String,
    plot: Plot,
    plotSize: Dimension = Dimension(500, 350)
) : PlotResizableDemoWindowBase(
    title,
    plot = plot,
    plotSize = plotSize
) {

    override fun createPlotComponent(plot: Plot, plotSize: Dimension): JComponent {
        val plotSizeProperty = ValueProperty(
            DoubleVector(
                plotSize.getWidth(),
                plotSize.getHeight(),
            )
        )

        val plotContainer = PlotContainer(plot, plotSizeProperty)

        return PlotPanel(
            plotComponentProvider = MyPlotComponentProvider(plotContainer, plotSizeProperty),
            preferredSizeFromPlot = true,
            repaintDelay = 100,
            applicationContext = DefaultSwingContextJfx()
        )
    }

    private class MyPlotComponentProvider(
        private val plotContainer: PlotContainer,
        private val plotSizeProperty: WritableProperty<DoubleVector>,
    ) : PlotComponentProvider {
        override fun getPreferredSize(containerSize: Dimension): Dimension {
            return containerSize
        }

        override fun createComponent(containerSize: Dimension?): JComponent {
            plotContainer.clearContent()
            containerSize?.run {
                plotSizeProperty.set(
                    DoubleVector(getWidth(), getHeight())
                )
            }
            plotContainer.ensureContentBuilt()
            return SceneMapperJfxPanel(
                plotContainer.svg,
                listOf(Style.JFX_PLOT_STYLESHEET)
            )
        }
    }
}