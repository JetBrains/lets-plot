/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_XLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_YLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID

object FigureModelHelper {
    fun updateSpecOverrideList(
        specOverrideList: List<Map<String, Any>>,
        newSpecOverride: Map<String, Any>?
    ): List<Map<String, Any>> {
        return if (newSpecOverride == null) {
            // drop all
            emptyList<Map<String, Any>>()
        } else {
            val targetId = newSpecOverride[TARGET_ID]

            @Suppress("UNUSED_VARIABLE")
            val specOverrideList = ArrayList(specOverrideList)
            val index = specOverrideList.indexOfFirst { it[TARGET_ID] == targetId }
            if (index < 0) {
                specOverrideList.add(newSpecOverride)
            } else {
                val reconciled = reconcile(specOverrideList[index], newSpecOverride)
                specOverrideList.set(index, reconciled)
            }
            specOverrideList
        }
    }

    private fun reconcile(
        wasSpecs: Map<String, Any>,
        newSpecs: Map<String, Any>
    ): Map<String, Any> {

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