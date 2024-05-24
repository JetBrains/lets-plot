/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.FigureModel
import org.jetbrains.letsPlot.awt.plot.component.PlotPanel.Companion.actualPlotComponentFromProvidedComponent
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import java.awt.Dimension
import javax.swing.JComponent

internal class PlotPanelFigureModel(
    private val plotPanel: PlotPanel,
    providedComponent: JComponent?,
    private val plotComponentFactory: (Dimension) -> JComponent,
    private val applicationContext: ApplicationContext,
) : FigureModel {

    private var toolEventCallback: ((Map<String, Any>) -> Unit)? = null

    private var toolEventDispatcher: ToolEventDispatcher? = toolEventDispatcherFromProvidedComponent(providedComponent)
        set(value) {
            // De-activate and re-activate ongoing interactions when replacing the dispatcher.
            val wereInteractions = field?.deactivateAllSilently() ?: emptyMap()
            field = value
            value?.let { newDispatcher ->
                newDispatcher.initToolEventCallback { event -> toolEventCallback?.invoke(event) }

                // reactivate interactions in new plot component
                wereInteractions.forEach { (origin, interactionSpecList) ->
                    newDispatcher.activateInteractions(origin, interactionSpecList)
                }
            }
        }

    override fun onToolEvent(callback: (Map<String, Any>) -> Unit) {
        toolEventCallback = callback
    }

    override fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>) {
        toolEventDispatcher?.activateInteractions(origin, interactionSpecList)
    }

    override fun deactivateInteractions(origin: String) {
        toolEventDispatcher?.deactivateInteractions(origin)
    }

    override fun updateView() {
        rebuildPlotComponent()
    }

    internal fun rebuildPlotComponent(
        onComponentCreated: (JComponent) -> Unit = {},
        expared: () -> Boolean = { false }
    ) {
        val action = Runnable {

            val containerSize = plotPanel.size
            if (containerSize == null) return@Runnable

            val providedComponent = plotComponentFactory(containerSize)
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