/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

class UnsupportedToolEventDispatcher : ToolEventDispatcher {
    override fun activateInteractions(
        origin: String,
        interactionSpecList: List<Map<String, Any>>
    ): List<Map<String, Any>> {
        // ToDo: fire an error-event
        throw IllegalStateException("Unsupported: activateInteractions")
    }

    override fun deactivateInteractions(origin: String): List<Map<String, Any>> = emptyList()

    override fun deactivateAllSilently(): Map<String, List<Map<String, Any>>> = emptyMap()
}