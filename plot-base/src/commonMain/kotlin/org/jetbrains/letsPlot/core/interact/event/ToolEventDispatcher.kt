/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

interface ToolEventDispatcher {
    fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>): List<Map<String, Any>>
    fun deactivateInteractions(origin: String): List<Map<String, Any>>

    /**
     * @return List of deactivates interaction specs by "origin"
     */
    fun deactivateAllSilently(): Map<String, List<Map<String, Any>>>
}