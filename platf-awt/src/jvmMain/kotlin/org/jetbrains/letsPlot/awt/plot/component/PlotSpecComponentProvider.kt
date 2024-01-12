/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.MonolithicAwt
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.PlotSizeUtil.preferredFigureSize
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

abstract class PlotSpecComponentProvider(
    private val processedSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean,
    private val svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
    private val executor: (() -> Unit) -> Unit,
    private val computationMessagesHandler: (List<String>) -> Unit
) : PlotComponentProvider {

    private val errorMessageComponentFactory: (String) -> JComponent = { errorMessage: String ->
        createErrorMessageComponent(errorMessage)
    }

    override fun getPreferredSize(containerSize: Dimension): Dimension {
        val outerSize = DoubleVector(containerSize.width.toDouble(), containerSize.height.toDouble())
        return preferredFigureSize(processedSpec, preserveAspectRatio, outerSize).let {
            Dimension(
                it.x.toInt(),
                it.y.toInt()
            )
        }
    }

    override fun createComponent(containerSize: Dimension?): JComponent {
        val plotSize = containerSize?.let {
            val preferredSize = getPreferredSize(containerSize)
            DoubleVector(preferredSize.width.toDouble(), preferredSize.height.toDouble())
        }

        val plotComponent = createPlotComponent(
            processedSpec, plotSize,
            svgComponentFactory,
            executor,
            errorMessageComponentFactory,
            computationMessagesHandler
        )

        val isGGBunch =
            !PlotConfig.isFailure(processedSpec) && PlotConfig.figSpecKind(processedSpec) == FigKind.GG_BUNCH_SPEC
        return if (isGGBunch) {
            // GGBunch is always 'original' size => add a scroll pane.
            val scrollPane = createScrollPane(plotComponent)
            containerSize?.run {
                scrollPane.preferredSize = containerSize
                scrollPane.size = containerSize
            }
            scrollPane
        } else {
            plotComponent
        }
    }

    /**
     * Override in "Lets-Plot in SciView" IDEA plugin: use JBScrollPane.
     */
    protected open fun createScrollPane(plotComponent: JComponent): JScrollPane {
        return JScrollPane(
            plotComponent,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ).apply {
            border = null
        }
    }

    /**
     * Provids an "error message" component
     * in the case of a failure while building a plot.
     *
     * Override this method if your application requires better "error message" component.
     */
    protected open fun createErrorMessageComponent(message: String): JComponent {
        return DefaultErrorMessageComponent(message)
    }

    companion object {
        private fun createPlotComponent(
            figureSpecProcessed: MutableMap<String, Any>,
            preferredSize: DoubleVector?,
            svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
            executor: (() -> Unit) -> Unit,
            errorMessageComponentFactory: (message: String) -> JComponent,
            computationMessagesHandler: ((List<String>) -> Unit)
        ): JComponent {
            return MonolithicAwt.buildPlotFromProcessedSpecs(
                plotSpec = figureSpecProcessed,
                plotSize = preferredSize,
                plotMaxWidth = null,
                svgComponentFactory = svgComponentFactory,
                executor = executor,
                errorMessageComponentFactory = errorMessageComponentFactory,
                computationMessagesHandler = computationMessagesHandler,
            )
        }
    }
}