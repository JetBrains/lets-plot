/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.DrawRectFeedback
import org.jetbrains.letsPlot.core.interact.PanGeomFeedback
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
    private val activeInteractionsByOrigin: MutableMap<String, MutableMap<String, Registration>> = HashMap()

    override fun activateInteraction(origin: String, interactionSpec: Map<String, Any>): List<Map<String, Any>> {

        val responseEvents = ArrayList<Map<String, Any>>()
        val interactionName = interactionSpec.getValue(ToolInteractionSpec.NAME) as String

        responseEvents.addAll(deactivateOverlappingInteractions(interactionName))

        val feedback = when (interactionName) {
            ToolInteractionSpec.DRAG_PAN -> PanGeomFeedback(
                onCompleted = { _, target ->
                    println("Pan tool: apply: $target")
                }
            )

            ToolInteractionSpec.BOX_ZOOM -> DrawRectFeedback(
                onCompleted = { (r, target) ->
                    // translate to "geom" space.
                    val translated = r.subtract(target.geomBounds.origin)
                    println("Zoom tool: apply: $translated")
                    target.zoom(translated)
                }
            )

            else -> {
                // ToDo: send an error event
                throw IllegalStateException("Unsupported interaction: $interactionName")
            }
        }

        val feedbackRegistration = plotInteractor.startToolFeedback(feedback)
        activeInteractionsByOrigin.getOrPut(origin) { HashMap() }[interactionName] = feedbackRegistration

        responseEvents.add(
            mapOf(
                EVENT_NAME to INTERACTION_ACTIVATED,
                EVENT_INTERACTION_ORIGIN to origin,
                EVENT_INTERACTION_NAME to interactionName
            )
        )
        return responseEvents
    }

    override fun deactivateInteraction(origin: String, interactionName: String): Map<String, Any> {
        activeInteractionsByOrigin[origin]?.remove(interactionName)?.dispose()
        return mapOf(
            EVENT_NAME to INTERACTION_DEACTIVATED,
            EVENT_INTERACTION_ORIGIN to origin,
            EVENT_INTERACTION_NAME to interactionName
        )
    }

    private fun deactivateOverlappingInteractions(activatedInteractionName: String): List<Map<String, Any>> {
        // For now just deactivate all active interactions
        val originAndIntInteractionList = activeInteractionsByOrigin.flatMap { (origin, list) ->
            list.map { (interaction, _) -> Pair(origin, interaction) }
        }

        return originAndIntInteractionList.map {
            deactivateInteraction(it.first, it.second)
        }
    }
}