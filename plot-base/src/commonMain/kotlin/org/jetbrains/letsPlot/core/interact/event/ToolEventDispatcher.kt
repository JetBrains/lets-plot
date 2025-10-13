/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

import org.jetbrains.letsPlot.core.interact.InteractionSpec

interface ToolEventDispatcher {
    fun initToolEventCallback(callback: (Map<String, Any>) -> Unit)

    fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>)
    fun deactivateInteractions(origin: String): List<InteractionSpec>
    fun deactivateAll()

    /**
     * Set default interactions that run in the background but are suspended when explicit interactions are active.
     * Replaces any existing default interactions (both active and suspended).
     */
    fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>)

    /**
     * @return List of deactivated interaction specs by "origin"
     */
    fun deactivateAllSilently(): Map<String, List<InteractionSpec>>

    companion object {
        const val ORIGIN_FIGURE_IMPLICIT = "org.jetbrains.letsPlot.interact.FigureImplicit"
        const val ORIGIN_FIGURE_CLIENT_DEFAULT = "org.jetbrains.letsPlot.interact.FigureClientDefault"

        fun isExplicitOrigin(origin: String): Boolean {
            return origin != ORIGIN_FIGURE_IMPLICIT && origin != ORIGIN_FIGURE_CLIENT_DEFAULT
        }

        fun <T> filterExplicitOrigins(valueByOrigin: Map<String, T>): Map<String, T> {
            return valueByOrigin.filterKeys {
                isExplicitOrigin(it)
            }
        }
    }
}