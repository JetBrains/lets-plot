/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.letsPlot.commons.debounce
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.InteractionSpec.ZoomBoxMode
import org.jetbrains.letsPlot.core.interact.UnsupportedInteractionException
import org.jetbrains.letsPlot.core.interact.event.ModifiersMatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher.Companion.ORIGIN_FIGURE_CLIENT_DEFAULT
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher.Companion.ORIGIN_FIGURE_IMPLICIT
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher.Companion.filterExplicitOrigins
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher.Companion.isExplicitOrigin
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_TARGET
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_DATA_BOUNDS
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_ERROR_MSG
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_SCALE_FACTOR
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_UNSUPPORTED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.ROLLBACK_ALL_CHANGES
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.SELECTION_CHANGED
import org.jetbrains.letsPlot.core.interact.feedback.DrawRectFeedback
import org.jetbrains.letsPlot.core.interact.feedback.DrawRectFeedback.SelectionMode
import org.jetbrains.letsPlot.core.interact.feedback.PanGeomFeedback
import org.jetbrains.letsPlot.core.interact.feedback.PanGeomFeedback.PanningMode
import org.jetbrains.letsPlot.core.interact.feedback.RollbackAllChangesFeedback
import org.jetbrains.letsPlot.core.interact.feedback.WheelZoomFeedback
import org.jetbrains.letsPlot.core.plot.builder.PlotInteractor


internal class PlotToolEventDispatcher(
    private val plotInteractor: PlotInteractor
) : ToolEventDispatcher {

    private val interactionsByOrigin: MutableMap<
            String,                 // origin
            MutableList<InteractionInfo>> = HashMap()

    // Suspended ORIGIN_FIGURE_CLIENT_DEFAULT interactions (will be reactivated when explicit interactions deactivate)
    private var suspendedDefaultInteractions: List<InteractionSpec> = emptyList()

    private lateinit var toolEventCallback: (Map<String, Any>) -> Unit

    override fun initToolEventCallback(callback: (Map<String, Any>) -> Unit) {
        check(!this::toolEventCallback.isInitialized) { "Repeated initialization of 'toolEventCallback'." }
        toolEventCallback = callback
    }

    override fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
        interactionSpecList.forEach { interactionSpec ->
            activateInteraction(origin, interactionSpec)
        }
    }

    private fun activateInteraction(origin: String, interactionSpec: InteractionSpec) {
        deactivateOverlappingInteractions(origin, interactionSpec)

        try {
            activateInteractionIntern(origin, interactionSpec)
        } catch (e: UnsupportedInteractionException) {
            if (origin != ORIGIN_FIGURE_IMPLICIT) {
                toolEventCallback.invoke(
                    mapOf(
                        EVENT_NAME to INTERACTION_UNSUPPORTED,
                        EVENT_INTERACTION_ORIGIN to origin,
                        EVENT_INTERACTION_NAME to interactionSpec.name.value,
                        EVENT_RESULT_ERROR_MSG to "Not supported: ${e.message}"
                    )
                )
            }
        }
    }

    private fun activateInteractionIntern(origin: String, interactionSpec: InteractionSpec) {

        val modifiersMatcher = ModifiersMatcher.create(interactionSpec.keyModifiers)
        val interactionName: String = interactionSpec.name.value

        val fireSelectionChangedDebounced =
            debounce<Triple<String?, DoubleRectangle, DoubleVector>>(
                DEBOUNCE_DELAY_MS,
                CoroutineScope(Dispatchers.Default)
            ) { (targetId, dataBounds, scaleFactor) ->
                val dataBoundsLTRB = listOf(dataBounds.left, dataBounds.top, dataBounds.right, dataBounds.bottom)
                val scaleFactorList = listOf(scaleFactor.x, scaleFactor.y)
                fireSelectionChanged(origin, interactionName, targetId, dataBoundsLTRB, scaleFactorList)
            }

        val feedback = when (interactionSpec.name) {
            InteractionSpec.Name.DRAG_PAN -> PanGeomFeedback(
                modifiersMatcher = modifiersMatcher,
                onCompleted = { targetId, dataBounds, flipped, panningMode ->
                    // flip panning mode if coord flip
                    @Suppress("NAME_SHADOWING")
                    val panningMode = if (!flipped) {
                        panningMode
                    } else when (panningMode) {
                        PanningMode.FREE -> panningMode
                        PanningMode.HORIZONTAL -> PanningMode.VERTICAL
                        PanningMode.VERTICAL -> PanningMode.HORIZONTAL
                    }
                    val dataBoundsLTRB = dataBounds.run {
                        when (panningMode) {
                            PanningMode.FREE -> listOf(left, top, right, bottom)
                            PanningMode.HORIZONTAL -> listOf(left, null, right, null)
                            PanningMode.VERTICAL -> listOf(null, top, null, bottom)
                        }
                    }
                    fireSelectionChanged(origin, interactionName, targetId, dataBoundsLTRB)
                }
            )

            InteractionSpec.Name.BOX_ZOOM -> {
                val centerStart = interactionSpec.zoomBoxMode == ZoomBoxMode.CENTER_START
                DrawRectFeedback(
                    centerStart,
                    modifiersMatcher = modifiersMatcher,
                    onCompleted = { targetId, dataBounds, flipped, selectionMode, scaleFactor ->
                        // flip selection mode if the coord flips
                        @Suppress("NAME_SHADOWING")
                        val selectionMode = if (!flipped) {
                            selectionMode
                        } else when (selectionMode) {
                            SelectionMode.BOX -> selectionMode
                            SelectionMode.HORIZONTAL_BAND -> SelectionMode.VERTICAL_BAND
                            SelectionMode.VERTICAL_BAND -> SelectionMode.HORIZONTAL_BAND
                        }
                        val dataBoundsLTRB = dataBounds.run {
                            when (selectionMode) {
                                SelectionMode.BOX -> listOf(left, top, right, bottom)
                                SelectionMode.VERTICAL_BAND -> listOf(left, null, right, null)
                                SelectionMode.HORIZONTAL_BAND -> listOf(null, top, null, bottom)
                            }
                        }
                        val scaleFactorList = listOf(scaleFactor.x, scaleFactor.y)
                        fireSelectionChanged(origin, interactionName, targetId, dataBoundsLTRB, scaleFactorList)
                    })
            }

            InteractionSpec.Name.WHEEL_ZOOM -> WheelZoomFeedback(
                modifiersMatcher = modifiersMatcher,
                onCompleted = { targetId, dataBounds, scaleFactor ->
                    fireSelectionChangedDebounced(Triple(targetId, dataBounds, scaleFactor))
                }
            )

            InteractionSpec.Name.ROLLBACK_ALL_CHANGES -> RollbackAllChangesFeedback(
                modifiersMatcher = modifiersMatcher,
                onAction = { targetId: String? ->
                    toolEventCallback.invoke(
                        mapOf(
                            EVENT_NAME to ROLLBACK_ALL_CHANGES,
                            EVENT_INTERACTION_ORIGIN to origin,
                            EVENT_INTERACTION_NAME to interactionName,
                            EVENT_INTERACTION_TARGET to targetId
                        ).filterNotNullValues()
                    )
                }
            )
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

    private fun fireSelectionChanged(
        origin: String,
        interactionName: String,
        targetId: String?,
        dataBoundsLTRB: List<Double?>,
        scaleFactor: List<Double>? = null
    ) {
        toolEventCallback.invoke(
            mapOf(
                EVENT_NAME to SELECTION_CHANGED,
                EVENT_INTERACTION_ORIGIN to origin,
                EVENT_INTERACTION_NAME to interactionName,
                EVENT_RESULT_DATA_BOUNDS to dataBoundsLTRB,
                EVENT_RESULT_SCALE_FACTOR to scaleFactor,
                EVENT_INTERACTION_TARGET to targetId,
            ).filterNotNullValues()
        )
    }

    override fun deactivateInteractions(origin: String): List<InteractionSpec> {
        val deactivatedSpecs = mutableListOf<InteractionSpec>()
        interactionsByOrigin.remove(origin)?.forEach { interactionInfo ->
            interactionInfo.feedbackReg.dispose()
            deactivatedSpecs.add(interactionInfo.interactionSpec)
            toolEventCallback.invoke(
                mapOf(
                    EVENT_NAME to INTERACTION_DEACTIVATED,
                    EVENT_INTERACTION_ORIGIN to origin,
                    EVENT_INTERACTION_NAME to interactionInfo.interactionName
                )
            )
        }

        // Reactivate suspended default interactions if no explicit interactions remain
        if (isExplicitOrigin(origin)) {
            reactivateSuspendedDefaultInteractionsIfNeeded()
        }

        return deactivatedSpecs
    }

    private fun reactivateSuspendedDefaultInteractionsIfNeeded() {
        // Check if there are any explicit interactions still active
        val hasActiveExplicitInteractions = filterExplicitOrigins(interactionsByOrigin).isNotEmpty()

        // If no explicit interactions remain, and we have suspended default interactions, reactivate them
        if (!hasActiveExplicitInteractions && suspendedDefaultInteractions.isNotEmpty()) {
            val toReactivate = suspendedDefaultInteractions
            suspendedDefaultInteractions = emptyList()
            activateInteractions(ORIGIN_FIGURE_CLIENT_DEFAULT, toReactivate)
        }
    }

    override fun deactivateAll() {
        val origins = ArrayList(interactionsByOrigin.keys)
        origins.forEach { origin -> deactivateInteractions(origin) }
        suspendedDefaultInteractions = emptyList()
    }

    override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {
        // Deactivate any existing default interactions (active or suspended)
        deactivateInteractions(ORIGIN_FIGURE_CLIENT_DEFAULT)
        suspendedDefaultInteractions = emptyList()

        // Check if there are any explicit interactions active
        val hasActiveExplicitInteractions = filterExplicitOrigins(interactionsByOrigin).isNotEmpty()

        if (hasActiveExplicitInteractions) {
            // Explicit interactions are active - suspend default interactions
            suspendedDefaultInteractions = interactionSpecList
        } else {
            // No explicit interactions - activate default interactions immediately
            activateInteractions(ORIGIN_FIGURE_CLIENT_DEFAULT, interactionSpecList)
        }
    }

    override fun deactivateAllSilently(): Map<String, List<InteractionSpec>> {
        val deactivatedInteractions = interactionsByOrigin.mapValues { (_, interactionInfoList) ->
            interactionInfoList.map {
                it.feedbackReg.dispose()
                it.interactionSpec
            }
        }.toMutableMap()

        // Include suspended default interactions
        if (suspendedDefaultInteractions.isNotEmpty()) {
            deactivatedInteractions[ORIGIN_FIGURE_CLIENT_DEFAULT] = suspendedDefaultInteractions
        }

        interactionsByOrigin.clear()
        suspendedDefaultInteractions = emptyList()

        return deactivatedInteractions
    }

    private fun deactivateOverlappingInteractions(
        originBeingActivated: String,
        interactionSpecBeingActivated: InteractionSpec
    ) {
        // Special cases
        if (originBeingActivated == ORIGIN_FIGURE_IMPLICIT) {
            // 'implicit' interactions are always compatible
            return
        }

        if (originBeingActivated == ORIGIN_FIGURE_CLIENT_DEFAULT) {
            // 'default' interactions are compatible with 'implicit' interactions
            // but incompatible with other origins
            filterExplicitOrigins(interactionsByOrigin)
                .keys
                .forEach { origin -> deactivateInteractions(origin) }
            return
        }

        // Explicit interaction being activated - suspend default interactions
        suspendedDefaultInteractions += deactivateInteractions(ORIGIN_FIGURE_CLIENT_DEFAULT)

        // Deactivate all other active interactions (except implicit and default)
        filterExplicitOrigins(interactionsByOrigin)
            .keys
            .filter { origin -> origin != originBeingActivated }
            .forEach { origin -> deactivateInteractions(origin) }
    }

    private class InteractionInfo(
        val interactionSpec: InteractionSpec,
        val feedbackReg: Registration
    ) {
        val interactionName: String = interactionSpec.name.value
    }

    companion object {
        private const val DEBOUNCE_DELAY_MS = 30L
    }
}