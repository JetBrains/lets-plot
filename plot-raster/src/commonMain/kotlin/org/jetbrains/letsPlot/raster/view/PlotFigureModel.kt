package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel

// TODO: doesn't look right. Shouldn't be a part of the PlotCanvasFigure? duped from compose module.
class PlotFigureModel(
    val onUpdateView: (Map<String, Any>?) -> Unit
) : FigureModel {
    private val toolEventCallbacks = mutableListOf<(Map<String, Any>) -> Unit>()
    private var defaultInteractions: List<InteractionSpec> = emptyList()

    var toolEventDispatcher: ToolEventDispatcher? = null
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

    init {
        toolEventDispatcher?.initToolEventCallback { event -> toolEventCallbacks.forEach { it.invoke(event) } }
    }

    override fun addToolEventCallback(callback: (Map<String, Any>) -> Unit): Registration {
        toolEventCallbacks.add(callback)

        // Make snsure that 'implicit' interaction activated.
        deactivateInteractions(origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT)
        activateInteractions(
            origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT,
            interactionSpecList = FIGURE_IMPLICIT_INTERACTIONS
        )

        return object : Registration() {
            override fun doRemove() {
                toolEventCallbacks.remove(callback)
            }
        }
    }
    override fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
        toolEventDispatcher?.activateInteractions(origin, interactionSpecList)
    }

    override fun addDisposible(disposable: Disposable) {
        TODO("Not yet implemented")
    }

    override fun deactivateInteractions(origin: String){
        toolEventDispatcher?.deactivateInteractions(origin)
    }

    override fun dispose() {
        toolEventDispatcher?.deactivateAll()
        toolEventDispatcher = null
        toolEventCallbacks.clear()

        //val disposibles = ArrayList(disposibleTools)
        //disposibleTools.clear()
        //disposibles.forEach { it.dispose() }
    }

    override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {
        defaultInteractions = interactionSpecList
        toolEventDispatcher?.setDefaultInteractions(interactionSpecList)
    }

    override fun updateView(specOverride: Map<String, Any>?) {
        onUpdateView(specOverride)
    }

    companion object {
        private val FIGURE_IMPLICIT_INTERACTIONS = listOf(InteractionSpec(InteractionSpec.Name.ROLLBACK_ALL_CHANGES))
    }
}