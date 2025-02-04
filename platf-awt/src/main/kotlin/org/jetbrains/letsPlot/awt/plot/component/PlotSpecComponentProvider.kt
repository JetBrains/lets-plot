/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.MonolithicAwt
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

abstract class PlotSpecComponentProvider(
    private val processedSpec: MutableMap<String, Any>,
    private val svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
    private val executor: (() -> Unit) -> Unit,
    private val computationMessagesHandler: (List<String>) -> Unit
) : PlotComponentProvider {

    private val errorMessageComponentFactory: (String) -> JComponent = { errorMessage: String ->
        createErrorMessageComponent(errorMessage)
    }

    override fun createComponent(
        containerSize: Dimension?,
        sizingPolicy: SizingPolicy,
        specOverrideList: List<Map<String, Any>>
    ): JComponent {

        val plotSpec = SpecOverrideUtil.applySpecOverride(processedSpec, specOverrideList)
            .toMutableMap() // ToDo: get rid of "mutable"

        val plotComponent = createPlotComponent(
            plotSpec,
            containerSize,
            sizingPolicy,
            svgComponentFactory,
            executor,
            errorMessageComponentFactory,
            computationMessagesHandler
        )

        // ToDo: create 'scrollPane' if 'sizing policy' is 'fixed'.
//        val isGGBunch =
//            !PlotConfig.isFailure(processedSpec) && PlotConfig.figSpecKind(processedSpec) == FigKind.GG_BUNCH_SPEC
//        return if (isGGBunch) {
//            // GGBunch is always 'original' size => add a scroll pane.
//            val scrollPane = createScrollPane(plotComponent)
//            containerSize?.run {
//                scrollPane.preferredSize = containerSize
//                scrollPane.size = containerSize
//            }
//            scrollPane
//        } else {
//            plotComponent
//        }
        return plotComponent
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
            containerSize: Dimension?,
            sizingPolicy: SizingPolicy,
            svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
            executor: (() -> Unit) -> Unit,
            errorMessageComponentFactory: (message: String) -> JComponent,
            computationMessagesHandler: ((List<String>) -> Unit)
        ): JComponent {
            return MonolithicAwt.buildPlotFromProcessedSpecs(
                plotSpec = figureSpecProcessed,
                containerSize,
                sizingPolicy,
                svgComponentFactory = svgComponentFactory,
                executor = executor,
                errorMessageComponentFactory = errorMessageComponentFactory,
                computationMessagesHandler = computationMessagesHandler,
            )
        }
    }
}