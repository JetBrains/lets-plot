/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

/**
 * Carries spec override state through the plot rebuild pipeline.
 *
 * During interactive pan/zoom, each affected subplot accumulates a "spec override"
 * (coordinate limits, scale ratio) keyed by its target ID. In composite figures with
 * shared axes, the overrides from the actively interacted subplot must be propagated
 * to sibling subplots sharing that axis — this propagation is called "expansion".
 *
 * @param specOverrides the current list of per-target spec overrides.
 * @param activeTargetId the spec ID of the subplot being directly interacted with,
 *        or `null` when no expansion is needed (e.g., on resize).
 * @property expandedOverrides the result of expansion; non-empty only when [expand]
 *           produced new sibling overrides. Read back by the figure model to keep
 *           its stored override list in sync.
 */
class SpecOverrideState(
    val specOverrides: List<Map<String, Any>>,
    val activeTargetId: String?
) {
    var expandedOverrides: List<Map<String, Any>> = emptyList()
        private set

    /**
     * Runs the given [expander] to expand shared-axis overrides to sibling subplots.
     *
     * The expander function is injected by the caller (typically `SpecOverrideUtil`)
     * so that this class doesn't depend on spec parsing utilities in higher-level modules.
     */
    fun expand(expander: (List<Map<String, Any>>, String?) -> List<Map<String, Any>>) {
        expandedOverrides = expander(specOverrides, activeTargetId)
    }

    companion object {
        fun empty() = SpecOverrideState(
            specOverrides = emptyList(),
            activeTargetId = null
        )
    }
}
