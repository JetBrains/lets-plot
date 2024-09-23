/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

interface ToolEventDispatcher {
    fun initToolEventCallback(callback: (Map<String, Any>) -> Unit)

    fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>)
    fun deactivateInteractions(origin: String)

    /**
     * @return List of deactivates interaction specs by "origin"
     */
    fun deactivateAllSilently(): Map<String, List<Map<String, Any>>>

    companion object {
        const val ORIGIN_FIGURE_IMPLICIT = "org.jetbrains.letsPlot.interact.FigureImplicit"
    }
}