/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.letsPlot.commons.debounce
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.DrawRectFeedback
import org.jetbrains.letsPlot.core.interact.PanGeomFeedback
import org.jetbrains.letsPlot.core.interact.WheelZoomFeedback
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_DATA_BOUNDS
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_COMPLETED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec.ZoomBoxMode
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
        val completeInteractionDebounced =
            debounce<DoubleRectangle>(DEBOUNCE_DELAY_MS, CoroutineScope(Dispatchers.Default)) { dataBounds ->
                println("Debounced interaction: $interactionName, dataBounds: $dataBounds")
                completeInteraction(origin, interactionName, dataBounds)
            }

        // ToDo: sent "completed" event in "onCompleted"
        val feedback = when (interactionName) {
            ToolInteractionSpec.DRAG_PAN -> PanGeomFeedback(
                onCompleted = { dataBounds ->
                    println("Pan tool: apply $dataBounds")
                    completeInteraction(origin, interactionName, dataBounds)
                }
            )

            ToolInteractionSpec.BOX_ZOOM -> {
                val fixedAspectRatio = interactionSpec[ToolInteractionSpec.ZOOM_BOX_MODE] == ZoomBoxMode.CENTER_START
                DrawRectFeedback(fixedAspectRatio) { dataBounds ->
                    println("client: data $dataBounds")
                    completeInteraction(origin, interactionName, dataBounds)
                }
            }


            ToolInteractionSpec.WHEEL_ZOOM -> WheelZoomFeedback(
                onCompleted = { dataBounds ->
                    completeInteractionDebounced(dataBounds)
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

    private fun completeInteraction(
        origin: String,
        interactionName: String,
        dataBounds: DoubleRectangle
    ) {
        toolEventCallback.invoke(
            mapOf(
                EVENT_NAME to INTERACTION_COMPLETED,
                EVENT_INTERACTION_ORIGIN to origin,
                EVENT_INTERACTION_NAME to interactionName,
                EVENT_RESULT_DATA_BOUNDS to listOf(
                    dataBounds.left, dataBounds.top,
                    dataBounds.right, dataBounds.bottom
                )
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

    companion object {
        private const val DEBOUNCE_DELAY_MS = 30L
    }
}