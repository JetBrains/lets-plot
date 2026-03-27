/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelBase
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.SpecOverrideState
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

class PlotCanvasFigureModel(
    private val plotDrawable: PlotCanvasDrawable,
    private val processedSpec: Map<String, Any>,
    private val sizingPolicyProvider: () -> SizingPolicy,
    private val computationMessagesHandler: (List<String>) -> Unit = { _ -> },
) : FigureModelBase() {
    private var specOverrideList: List<Map<String, Any>> = emptyList()

    init {
        toolEventDispatcher = plotDrawable.toolEventDispatcher
    }

    override fun updateView(specOverride: Map<String, Any>?) {
        specOverrideList = FigureModelHelper.updateSpecOverrideList(
            specOverrideList = specOverrideList,
            newSpecOverride = specOverride
        )

        val activeTargetId = specOverride?.get(TARGET_ID) as? String
        val state = SpecOverrideState(specOverrideList, activeTargetId)
        val plotSpec = SpecOverrideUtil.applySpecOverride(processedSpec, state)

        plotDrawable.update(
            processedSpec = plotSpec,
            sizingPolicy = sizingPolicyProvider(),
            computationMessagesHandler = computationMessagesHandler
        )

        if (state.expandedOverrides.isNotEmpty()) {
            specOverrideList = state.expandedOverrides
        }

        toolEventDispatcher = plotDrawable.toolEventDispatcher
    }
}
