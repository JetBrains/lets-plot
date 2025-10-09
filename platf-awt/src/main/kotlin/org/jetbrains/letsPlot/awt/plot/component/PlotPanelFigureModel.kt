/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotPanel.Companion.actualPlotComponentFromProvidedComponent
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.FigureImplicitInteractionSpecs
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel
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
) : FigureModel {

    private val toolEventCallbacks = mutableListOf<(Map<String, Any>) -> Unit>()
    private val disposibleTools = mutableListOf<Disposable>()
    private var currSpecOverrideList: List<Map<String, Any>> = emptyList()
    private var defaultInteractions: List<Map<String, Any>> = emptyList()

    private var toolEventDispatcher: ToolEventDispatcher? = null
        set(value) {
            // De-activate and re-activate ongoing interactions when replacing the dispatcher.
            val wereInteractions = field?.deactivateAllSilently() ?: emptyMap()
            field = value
            value?.let { newDispatcher ->
                newDispatcher.initToolEventCallback { event ->
                    toolEventCallbacks.forEach { it(event) }
                }

                // Make sure that 'implicit' interactions are activated.
                newDispatcher.deactivateInteractions(origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT)
                newDispatcher.activateInteractions(
                    origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT,
                    interactionSpecList = FigureImplicitInteractionSpecs.LIST
                )

                // Set default interactions if any were configured
                defaultInteractions.let { defaultInteractionSpecs ->
                    newDispatcher.setDefaultInteractions(defaultInteractionSpecs)
                }

                // Reactivate explicit interactions in the new plot component
                ToolEventDispatcher.filterExplicitOrigins(wereInteractions)
                    .forEach { (origin, interactionSpecList) ->
                        newDispatcher.activateInteractions(origin, interactionSpecList)
                    }
            }
        }

    init {
        toolEventDispatcher = toolEventDispatcherFromProvidedComponent(providedComponent)
    }

    override fun addToolEventCallback(callback: (Map<String, Any>) -> Unit): Registration {
        toolEventCallbacks.add(callback)
        return Registration.onRemove {
            toolEventCallbacks.remove(callback)
        }
    }

    override fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>) {
        toolEventDispatcher?.activateInteractions(origin, interactionSpecList)
    }

    override fun deactivateInteractions(origin: String) {
        toolEventDispatcher?.deactivateInteractions(origin)
    }

    override fun setDefaultInteractions(interactionSpecList: List<Map<String, Any>>) {
        defaultInteractions = interactionSpecList
        toolEventDispatcher?.setDefaultInteractions(interactionSpecList)
    }

    override fun updateView(specOverride: Map<String, Any>?) {
        currSpecOverrideList = FigureModelHelper.updateSpecOverrideList(
            specOverrideList = currSpecOverrideList,
            newSpecOverride = specOverride
        )

        rebuildPlotComponent()
    }

    override fun addDisposible(disposable: Disposable) {
        disposibleTools.add(disposable)
    }

    override fun dispose() {
        toolEventDispatcher?.deactivateAll()
        toolEventDispatcher = null
        toolEventCallbacks.clear()

        val disposibles = ArrayList(disposibleTools)
        disposibleTools.clear()
        disposibles.forEach { it.dispose() }
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