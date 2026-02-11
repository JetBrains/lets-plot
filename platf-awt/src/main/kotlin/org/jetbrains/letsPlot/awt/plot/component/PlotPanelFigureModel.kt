/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotPanel.Companion.actualPlotComponentFromProvidedComponent
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelBase
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import java.awt.Dimension
import javax.swing.JComponent

internal class PlotPanelFigureModel constructor(
    private val plotPanel: PlotPanel,
    providedComponent: JComponent?,
    private val plotComponentFactory: (
        containerSize: Dimension,
        specOverrideList: List<Map<String, Any>>
    ) -> JComponent,
    private val applicationContext: ApplicationContext,
) : FigureModelBase() {

    private var currSpecOverrideList: List<Map<String, Any>> = emptyList()

    init {
        toolEventDispatcher = toolEventDispatcherFromProvidedComponent(providedComponent)
    }

    override fun updateView(specOverride: Map<String, Any>?) {
        currSpecOverrideList = FigureModelHelper.updateSpecOverrideList(
            specOverrideList = currSpecOverrideList,
            newSpecOverride = specOverride
        )

        rebuildPlotComponent()
    }

    internal fun rebuildPlotComponent(
        onComponentCreated: (JComponent) -> Unit = {},
        expared: () -> Boolean = { false }
    ) {
        val specOverrideList = ArrayList(currSpecOverrideList)
        val action = Runnable {

            val containerSize = plotPanel.size
            if (containerSize == null) return@Runnable

            val providedComponent = plotComponentFactory(containerSize, specOverrideList)
            onComponentCreated(providedComponent)

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