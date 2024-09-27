/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

object FigureModelOptions {
    // Tools can temporarily override default or provided limits.
    const val COORD_XLIM_TRANSFORMED = "coord_xlim_transformed"  // array of two nullable numbers
    const val COORD_YLIM_TRANSFORMED = "coord_ylim_transformed"


    fun reconcile(
        wasSpecs: Map<String, Any>?,
        newSpecs: Map<String, Any>?
    ): Map<String, Any>? {
        if (newSpecs == null) return null
        if (wasSpecs == null) return newSpecs

        val specsUpdate = HashMap<String, Any>()
        reconcileLims(COORD_XLIM_TRANSFORMED, wasSpecs, newSpecs)?.let {
            specsUpdate[COORD_XLIM_TRANSFORMED] = it
        }
        reconcileLims(COORD_YLIM_TRANSFORMED, wasSpecs, newSpecs)?.let {
            specsUpdate[COORD_YLIM_TRANSFORMED] = it
        }
        return newSpecs + specsUpdate
    }

    private fun reconcileLims(
        option: String,
        wasSpecs: Map<String, Any>,
        newSpecs: Map<String, Any>
    ): List<Double?>? {
        @Suppress("UNCHECKED_CAST")
        val newLims = (newSpecs[option] as? List<Double?>) ?: return null

        @Suppress("UNCHECKED_CAST")
        val wasLims = (wasSpecs[option] as? List<Double?>) ?: return newLims

        // Prevent overriding none-null valued in the current specs with
        // nulls in the incoming specs.nulls.
        return newLims.zip(wasLims).map { (newLim, wasLim) ->
            newLim ?: wasLim
        }
    }
}