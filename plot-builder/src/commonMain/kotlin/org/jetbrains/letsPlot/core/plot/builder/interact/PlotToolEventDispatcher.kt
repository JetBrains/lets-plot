/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import org.jetbrains.letsPlot.commons.debounce
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.DrawRectFeedback
import org.jetbrains.letsPlot.core.interact.InteractionTarget
import org.jetbrains.letsPlot.core.interact.PanGeomFeedback
import org.jetbrains.letsPlot.core.interact.WheelZoomFeedback
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import org.jetbrains.letsPlot.core.plot.builder.PlotInteractor


internal class PlotToolEventDispatcher(
    private val plotInteractor: PlotInteractor
) : ToolEventDispatcher {

    private val interactionsByOrigin: MutableMap<
            String,                 // origin
            MutableList<InteractionInfo>> = HashMap()

    private lateinit var toolEventCallback: (Map<String, Any>) -> Unit

    override fun initToolEventCallback(callback: (Map<String, Any>) -> Unit) {
        check(!this::toolEventCallback.isInitialized) { "Repeated initialization of 'toolEventCallback'." }
        toolEventCallback = callback
    }

    override fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>) {
        interactionSpecList.forEach { interactionSpec ->
            activateInteraction(origin, interactionSpec)
        }
    }

    private fun activateInteraction(origin: String, interactionSpec: Map<String, Any>) {
        deactivateOverlappingInteractions(origin, interactionSpec)

        val interactionName = interactionSpec.getValue(ToolInteractionSpec.NAME) as String
        val debouncedWheelZoom = debounce<Pair<DoubleRectangle, InteractionTarget>>(500) { (rect, _) ->
            println("Wheel zoom tool: apply: $rect")
        }
        
        // ToDo: sent "completed" event in "onCompleted"
        val feedback = when (interactionName) {
            ToolInteractionSpec.DRAG_PAN -> PanGeomFeedback(
                onCompleted = { _, target ->
                    println("Pan tool: apply: $target")
                }
            )

            ToolInteractionSpec.BOX_ZOOM -> DrawRectFeedback(
                onCompleted = { (r, target) ->
                    // translate to "geom" space.
                    target.zoom(r)
                }
            )

            ToolInteractionSpec.WHEEL_ZOOM -> WheelZoomFeedback(
                onZoomed = { rect, target ->
                    //println("Wheel zoom: apply: $rect")
                    //target.zoom(delta)
                    debouncedWheelZoom(rect to target)
                }
            )

            else -> {
                // ToDo: send an error event
                throw IllegalStateException("Unsupported interaction: $interactionName")
            }
        }

        val feedbackRegistration = plotInteractor.startToolFeedback(feedback)
        interactionsByOrigin.getOrPut(origin) { ArrayList() }.add(
            InteractionInfo(
                interactionSpec = interactionSpec,
                feedbackReg = feedbackRegistration
            )
        )

        toolEventCallback.invoke(
            mapOf(
                EVENT_NAME to INTERACTION_ACTIVATED,
                EVENT_INTERACTION_ORIGIN to origin,
                EVENT_INTERACTION_NAME to interactionName
            )
        )
    }

    override fun deactivateInteractions(origin: String) {
        interactionsByOrigin.remove(origin)?.forEach { interactionInfo ->
            interactionInfo.feedbackReg.dispose()
            toolEventCallback.invoke(
                mapOf(
                    EVENT_NAME to INTERACTION_DEACTIVATED,
                    EVENT_INTERACTION_ORIGIN to origin,
                    EVENT_INTERACTION_NAME to interactionInfo.interactionName
                )
            )
        }
    }

    override fun deactivateAllSilently(): Map<String, List<Map<String, Any>>> {
        val deactivatedInteractions = interactionsByOrigin.mapValues { (_, interactionInfoList) ->
            interactionInfoList.map {
                it.feedbackReg.dispose()
                it.interactionSpec
            }
        }
        interactionsByOrigin.clear()
        return deactivatedInteractions
    }

    private fun deactivateOverlappingInteractions(
        originBeingActivated: String,
        interactionSpecBeingActivated: Map<String, Any>
    ) {
        // For now just deactivate all active interactions
        ArrayList(interactionsByOrigin.keys)
            .filter { origin -> origin != originBeingActivated }
            .forEach { origin -> deactivateInteractions(origin) }
    }

    private class InteractionInfo(
        val interactionSpec: Map<String, Any>,
        val feedbackReg: Registration
    ) {
        val interactionName = interactionSpec.getValue(ToolInteractionSpec.NAME) as String
    }
}