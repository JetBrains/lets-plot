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
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.ROLLBACK_ALL_CHANGES
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.SELECTION_CHANGED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.UPDATE_VIEW


class CompositeToolEventDispatcher constructor(
    private val elements: List<ToolEventDispatcher>,
    private val isDeck: Boolean = false
) : ToolEventDispatcher {

    override fun initToolEventCallback(callback: (Map<String, Any>) -> Unit) {
        val fireUpdateViewDebounced = debounce<Unit>(
            UPDATE_VIEW_DEBOUNCE_DELAY_MS,
            CoroutineScope(Dispatchers.Default)
        ) {
            callback(mapOf(EVENT_NAME to UPDATE_VIEW))
        }

        val wrappedCallback = { event: Map<String, Any> ->
            when (event[EVENT_NAME]) {
                UPDATE_VIEW -> {
                    // Suppress UPDATE_VIEW from children — composite fires its own debounced.
                }
                SELECTION_CHANGED, ROLLBACK_ALL_CHANGES -> {
                    callback(event)
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

    private val interactionTargets: List<ToolEventDispatcher>
        get() = if (isDeck) listOfNotNull(elements.lastOrNull()) else elements

    override fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
        interactionTargets.forEach {
            it.activateInteractions(origin, interactionSpecList)
        }
    }

    override fun deactivateInteractions(origin: String): List<InteractionSpec> {
        return interactionTargets.map {
            it.deactivateInteractions(origin)
        }.lastOrNull() // Expected all elements are the same.
            ?: emptyList()
    }

    override fun deactivateAll() {
        interactionTargets.forEach {
            it.deactivateAll()
        }
    }

    override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {
        interactionTargets.forEach {
            it.setDefaultInteractions(interactionSpecList)
        }
    }

    override fun deactivateAllSilently(): Map<String, List<InteractionSpec>> {
        return interactionTargets.map {
            it.deactivateAllSilently()
        }.lastOrNull() // Expected all elements are the same.
            ?: emptyMap()
    }

    companion object {
        private const val UPDATE_VIEW_DEBOUNCE_DELAY_MS = 30L
    }
}