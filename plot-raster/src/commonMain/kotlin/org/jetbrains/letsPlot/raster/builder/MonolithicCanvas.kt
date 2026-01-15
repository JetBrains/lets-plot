/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.event.UnsupportedToolEventDispatcher
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

/**
 * no JComponents or Views are created here.
 */
object MonolithicCanvas {
    internal fun buildViewModelFromProcessedSpecs(
        plotSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy,
        computationMessagesHandler: (List<String>) -> Unit,
        containerSize: DoubleVector? = null,
    ): ViewModel {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, containerSize = containerSize, sizingPolicy)
        if (buildResult is MonolithicCommon.PlotsBuildResult.Error) {
            return SimpleModel(createErrorSvgText(buildResult.error), UnsupportedToolEventDispatcher())
        }

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success

        val figure = FigureToViewModel.eval(success.buildInfo)

        val computationMessages = success.buildInfo.computationMessages
        computationMessagesHandler(computationMessages)

        return figure
    }

    private fun createErrorSvgText(s: String): SvgSvgElement {
        return SvgSvgElement().apply {
            children().add(
                SvgTextElement(s)
            )
        }
    }
}
