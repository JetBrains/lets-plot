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
import jetbrains.datalore.vis.swing.DefaultPlotPanel
import jetbrains.datalore.vis.swing.PlotComponentProvider
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.datalore.vis.swing.jfx.DefaultSwingContextJfx
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class PlotResizableDemoWindowJfx(
    title: String,
    private val plot: Plot,
    plotSize: Dimension = Dimension(500, 350)
) : JFrame(title) {

    private val rootPanel: JPanel
    private val plotSizeProperty = ValueProperty(
        DoubleVector(
            plotSize.getWidth(),
            plotSize.getHeight(),
        )
    )

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout()
        rootPanel.background = Color.WHITE
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        contentPane.add(rootPanel)
    }

    fun open() {
        SwingUtilities.invokeLater {
            createWindowContent()

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    private fun createWindowContent() {
        rootPanel.add(createPlotComponent(plot))
    }

    private fun createPlotComponent(plot: Plot): JComponent {
        val plotContainer = PlotContainer(plot, plotSizeProperty)

        return DefaultPlotPanel(
            plotComponentProvider = MyPlotComponentProvider(plotContainer, plotSizeProperty),
            preferredSizeFromPlot = true,
            refreshRate = 100,
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