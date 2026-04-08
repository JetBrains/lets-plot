/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotPanel.Companion.actualPlotComponentFromProvidedComponent
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelBase
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.SpecOverrideState
import java.awt.Dimension
import javax.swing.JComponent

internal class PlotPanelFigureModel constructor(
    private val plotPanel: PlotPanel,
    providedComponent: JComponent?,
    private val plotComponentFactory: (
        containerSize: Dimension,
        state: SpecOverrideState
    ) -> JComponent,
    private val applicationContext: ApplicationContext,
) : FigureModelBase() {

    private var currSpecOverrideList: List<Map<String, Any>> = emptyList()
    private var currSpecOverrideState: SpecOverrideState = SpecOverrideState(emptyList(), null)

    init {
        toolEventDispatcher = toolEventDispatcherFromProvidedComponent(providedComponent)
    }

    override fun updateSpecOverride(specOverride: Map<String, Any>?) {
        currSpecOverrideList = FigureModelHelper.updateSpecOverrideList(
            specOverrideList = currSpecOverrideList,
            newSpecOverride = specOverride
        )
        val activeTargetId = specOverride?.get(TARGET_ID) as? String
        currSpecOverrideState = SpecOverrideState(currSpecOverrideList, activeTargetId)
    }

    override fun updateView() {
        rebuildPlotComponent(state = currSpecOverrideState)
    }

    internal fun rebuildPlotComponent(
        state: SpecOverrideState = SpecOverrideState(currSpecOverrideList, null),
        onComponentCreated: (JComponent) -> Unit = {},
        expared: () -> Boolean = { false }
    ) {
        val action = Runnable {

            val containerSize = plotPanel.size
            if (containerSize == null) return@Runnable

            val providedComponent = plotComponentFactory(containerSize, state)
            onComponentCreated(providedComponent)

            // Read back expanded overrides (non-empty only when expansion occurred).
            if (state.expandedOverrides.isNotEmpty()) {
                currSpecOverrideList = state.expandedOverrides
            }

            toolEventDispatcher = toolEventDispatcherFromProvidedComponent(providedComponent)
            plotPanel.revalidate()
        }

        applicationContext.invokeLater(action, expared)
    }

    companion object {
        fun toolEventDispatcherFromProvidedComponent(providedComponent: JComponent?): ToolEventDispatcher? {
            if (providedComponent == null) return null
            val actualPlotComponent = actualPlotComponentFromProvidedComponent(providedComponent)
            return actualPlotComponent.getClientProperty(ToolEventDispatcher::class) as? ToolEventDispatcher
        }
    }
}