/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.letsPlot.commons.debounce
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_TARGET
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.ROLLBACK_ALL_CHANGES
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.SELECTION_CHANGED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.UPDATE_VIEW


class CompositeToolEventDispatcher constructor(
    private val elements: List<ToolEventDispatcher>,
    private val isDeck: Boolean = false
) : ToolEventDispatcher {

    override fun initToolEventCallback(callback: (Map<String, Any>) -> Unit) {
        if (isDeck) {
            initDeckCallback(callback)
        } else {
            elements.forEach {
                it.initToolEventCallback(callback)
            }
        }
    }

    /**
     * For deck mode: collect the latest SELECTION_CHANGED per child target and forward them
     * all right before a debounced UPDATE_VIEW.
     *
     * Children fire SELECTION_CHANGED immediately on the UI thread (internalDebounce=false),
     * so writes to `latestEvents` are not concurrent. The debounced closure reads the map
     * and forwards all stored events before firing UPDATE_VIEW.
     */
    private fun initDeckCallback(callback: (Map<String, Any>) -> Unit) {
        // Keyed by target ID — keeps only the latest event per child.
        val latestEvents = mutableMapOf<String?, Map<String, Any>>()

        val fireUpdateViewDebounced = debounce<Unit>(
            UPDATE_VIEW_DEBOUNCE_DELAY_MS,
            CoroutineScope(Dispatchers.Default)
        ) {
            // Forward all collected events, then fire UPDATE_VIEW.
            for (event in latestEvents.values) {
                callback(event)
            }
            latestEvents.clear()
            callback(mapOf(EVENT_NAME to UPDATE_VIEW))
        }

        val wrappedCallback = { event: Map<String, Any> ->
            when (event[EVENT_NAME]) {
                UPDATE_VIEW -> {
                    // Suppress UPDATE_VIEW from children — composite fires its own debounced.
                }

                SELECTION_CHANGED, ROLLBACK_ALL_CHANGES -> {
                    val targetId = event[EVENT_INTERACTION_TARGET] as? String
                    latestEvents[targetId] = event
                    fireUpdateViewDebounced(Unit)
                }

                else -> {
                    callback(event)
                }
            }
        }

        elements.forEach {
            it.initToolEventCallback(wrappedCallback)
        }
    }

    override fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
        elements.forEach {
            it.activateInteractions(origin, interactionSpecList)
        }
    }

    override fun deactivateInteractions(origin: String): List<InteractionSpec> {
        return elements.map {
            it.deactivateInteractions(origin)
        }.lastOrNull() // Expected all elements are the same.
            ?: emptyList()
    }

    override fun deactivateAll() {
        elements.forEach {
            it.deactivateAll()
        }
    }

    override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {
        elements.forEach {
            it.setDefaultInteractions(interactionSpecList)
        }
    }

    override fun deactivateAllSilently(): Map<String, List<InteractionSpec>> {
        return elements.map {
            it.deactivateAllSilently()
        }.lastOrNull() // Expected all elements are the same.
            ?: emptyMap()
    }

    companion object {
        private const val UPDATE_VIEW_DEBOUNCE_DELAY_MS = 100L
    }
}