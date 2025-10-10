/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

interface ToolEventDispatcher {
    fun initToolEventCallback(callback: (Map<String, Any>) -> Unit)

    fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>)
    fun deactivateInteractions(origin: String): List<Map<String, Any>>
    fun deactivateAll()

    /**
     * Set default interactions that run in the background but are suspended when explicit interactions are active.
     * Replaces any existing default interactions (both active and suspended).
     */
    fun setDefaultInteractions(interactionSpecList: List<Map<String, Any>>)

    /**
     * @return List of deactivated interaction specs by "origin"
     */
    fun deactivateAllSilently(): Map<String, List<Map<String, Any>>>

    companion object {
        const val ORIGIN_FIGURE_IMPLICIT = "org.jetbrains.letsPlot.interact.FigureImplicit"
        const val ORIGIN_FIGURE_CLIENT_DEFAULT = "org.jetbrains.letsPlot.interact.FigureClientDefault"

        fun isExplicitOrigin(origin: String): Boolean {
            return origin != ORIGIN_FIGURE_IMPLICIT && origin != ORIGIN_FIGURE_CLIENT_DEFAULT
        }

        fun <T> filterExplicitOrigins(interactionsByOrigin: Map<String, T>): Map<String, T> {
            return interactionsByOrigin.filterKeys {
                isExplicitOrigin(it)
            }
        }
    }
}