/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact

import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher


class CompositeToolEventDispatcher(
    private val elements: List<ToolEventDispatcher>
) : ToolEventDispatcher {

    override fun initToolEventCallback(callback: (Map<String, Any>) -> Unit) {
        elements.forEach {
            it.initToolEventCallback(callback)
        }
    }

    override fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>) {
        elements.forEach {
            it.activateInteractions(origin, interactionSpecList)
        }
    }

    override fun deactivateInteractions(origin: String) {
        elements.forEach {
            it.deactivateInteractions(origin)
        }
    }

    override fun deactivateAllSilently(): Map<String, List<Map<String, Any>>> {
        return elements.map {
            it.deactivateAllSilently()
        }.lastOrNull() // Expected all elements are same.
            ?: emptyMap()
    }
}