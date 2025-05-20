/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builder

import org.jetbrains.letsPlot.core.interact.event.UnsupportedToolEventDispatcher
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure

/**
 * no JComponents or Views are created here.
 */
object MonolithicCanvas {
    fun buildPlotFigureFromRawSpec(
        rawSpec: MutableMap<String, Any>,
        sizingPolicy: SizingPolicy,
        computationMessagesHandler: (List<String>) -> Unit
    ) : PlotCanvasFigure {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)

        val plotCanvasFigure = PlotCanvasFigure(processedSpec, sizingPolicy, computationMessagesHandler)
        return plotCanvasFigure
    }

    internal fun buildPlotFromProcessedSpecs(
        plotSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy,
        computationMessagesHandler: (List<String>) -> Unit
    ): ViewModel {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, containerSize = null, sizingPolicy)
        if (buildResult is MonolithicCommon.PlotsBuildResult.Error) {
            return SimpleModel(createErrorSvgText(buildResult.error), UnsupportedToolEventDispatcher())
        }

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
        val computationMessages = success.buildInfo.computationMessages
        computationMessagesHandler(computationMessages)

        return FigureToViewModel.eval(success.buildInfo)
    }

    private fun createErrorSvgText(s: String): SvgSvgElement {
        return SvgSvgElement().apply {
            children().add(
                SvgTextElement(s)
            )
        }
    }
}
