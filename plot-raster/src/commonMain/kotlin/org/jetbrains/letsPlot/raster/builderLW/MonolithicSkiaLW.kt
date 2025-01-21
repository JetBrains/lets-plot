/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builderLW

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.event.UnsupportedToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

/**
 * "lightweight" - no JComponents or Views are created here.
 */
object MonolithicSkiaLW {
    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: (List<String>) -> Unit
    ): ViewModel {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, null)
        if (buildResult is MonolithicCommon.PlotsBuildResult.Error) {
            return SimpleModel(createErrorSvgText(buildResult.error), UnsupportedToolEventDispatcher())
        }

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
        val computationMessages = success.buildInfos.flatMap(FigureBuildInfo::computationMessages)
        computationMessagesHandler(computationMessages)

        require(success.buildInfos.size == 1) { "GGBunch is not supported." }

        val buildInfo = success.buildInfos.single()
        return FigureToViewModel.eval(buildInfo)
    }

    private fun createErrorSvgText(s: String): SvgSvgElement {
        return SvgSvgElement().apply {
            children().add(
                SvgTextElement(s)
            )
        }
    }
}
