/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

import org.jetbrains.letsPlot.core.interact.InteractionSpec

class UnsupportedToolEventDispatcher : ToolEventDispatcher {
    override fun initToolEventCallback(callback: (Map<String, Any>) -> Unit) {}

    override fun activateInteractions(
        origin: String,
        interactionSpecList: List<InteractionSpec>
    ) {
        // ToDo: fire an error-event
        throw IllegalStateException("Unsupported: activateInteractions")
    }

    override fun deactivateInteractions(origin: String): List<InteractionSpec> = emptyList()

    override fun deactivateAll() {}

    override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {}

    override fun deactivateAllSilently(): Map<String, List<InteractionSpec>> = emptyMap()
}