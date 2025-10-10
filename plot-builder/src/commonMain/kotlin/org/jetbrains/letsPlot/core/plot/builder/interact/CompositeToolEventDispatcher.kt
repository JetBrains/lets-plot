/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher


class CompositeToolEventDispatcher(
    private val elements: List<ToolEventDispatcher>
) : ToolEventDispatcher {

    override fun initToolEventCallback(callback: (Map<String, Any>) -> Unit) {
        elements.forEach {
            it.initToolEventCallback(callback)
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
}