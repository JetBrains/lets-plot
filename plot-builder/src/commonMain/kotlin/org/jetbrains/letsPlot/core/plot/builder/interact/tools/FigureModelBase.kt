/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher


abstract class FigureModelBase : FigureModel {
    private val toolEventCallbacks = mutableListOf<(Map<String, Any>) -> Unit>()
    private val disposableTools = mutableListOf<Disposable>()
    private var defaultInteractions: List<InteractionSpec> = emptyList()

    var toolEventDispatcher: ToolEventDispatcher? = null
        set(value) {
            val wereInteractions = if (value != null) {
                // De-activate and re-activate ongoing interactions when replacing the dispatcher.
                field?.deactivateAllSilently() ?: emptyMap()
            } else {
                // Shut down all interactions when the dispatcher is set to null
                field?.deactivateAll()
                emptyMap()
            }
            field = value
            value?.let { newDispatcher ->
                newDispatcher.initToolEventCallback { event ->
                    toolEventCallbacks.forEach { it(event) }
                }

                // Make sure that 'implicit' interactions are activated
                newDispatcher.deactivateInteractions(origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT)
                newDispatcher.activateInteractions(
                    origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT,
                    interactionSpecList = FIGURE_IMPLICIT_INTERACTIONS
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

    override fun addToolEventCallback(callback: (Map<String, Any>) -> Unit): Registration {
        toolEventCallbacks.add(callback)
        return Registration.onRemove {
            toolEventCallbacks.remove(callback)
        }
    }

    override fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
        toolEventDispatcher?.activateInteractions(origin, interactionSpecList)
    }

    override fun deactivateInteractions(origin: String) {
        toolEventDispatcher?.deactivateInteractions(origin)
    }

    override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {
        defaultInteractions = interactionSpecList
        toolEventDispatcher?.setDefaultInteractions(interactionSpecList)
    }

    override fun addDisposible(disposable: Disposable) {
        disposableTools.add(disposable)
    }

    override fun dispose() {
        toolEventDispatcher?.deactivateAll()
        toolEventDispatcher = null
        toolEventCallbacks.clear()

        val disposables = ArrayList(disposableTools)
        disposableTools.clear()
        disposables.forEach { it.dispose() }
    }

    abstract override fun updateSpecOverride(specOverride: Map<String, Any>?)
    abstract override fun updateView()

    companion object {
        private val FIGURE_IMPLICIT_INTERACTIONS = listOf(InteractionSpec(InteractionSpec.Name.ROLLBACK_ALL_CHANGES))
    }
}