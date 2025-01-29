/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.plot.component.DefaultErrorMessageComponent
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import javax.swing.JComponent

private val LOG = PortableLogging.logger("MonolithicAwt")

object MonolithicAwt {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        errorMessageComponentFactory: (String) -> JComponent = DefaultErrorMessageComponent.factory,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {

        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(
                plotSpec,
                plotSize,
                svgComponentFactory,
                executor,
                errorMessageComponentFactory = errorMessageComponentFactory,
                computationMessagesHandler
            )
        } catch (e: RuntimeException) {
            handleException(e, errorMessageComponentFactory)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        errorMessageComponentFactory: (message: String) -> JComponent = DefaultErrorMessageComponent.factory,
        computationMessagesHandler: (List<String>) -> Unit,
    ): JComponent {

        return try {
            val sizingPolicy = plotSize?.let { SizingPolicy.fixed(plotSize.x, plotSize.y) }
            val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
                plotSpec,
                sizingPolicy
            )
            if (buildResult.isError) {
                val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
                return errorMessageComponentFactory(errorMessage)
            }

            val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
            val computationMessages = success.buildInfo.computationMessages
            computationMessagesHandler(computationMessages)
            return FigureToAwt(
                success.buildInfo,
                svgComponentFactory, executor
            ).eval()

        } catch (e: RuntimeException) {
            handleException(e, errorMessageComponentFactory)
        }
    }

    private fun handleException(
        e: RuntimeException,
        errorMessageComponentFactory: (message: String) -> JComponent
    ): JComponent {
        val failureInfo = FailureHandler.failureInfo(e)
        if (failureInfo.isInternalError) {
            LOG.error(e) { "Unexpected situation in 'MonolithicAwt'" }
        }
        return errorMessageComponentFactory(failureInfo.message)
    }
}
