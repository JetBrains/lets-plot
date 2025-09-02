/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_XLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_YLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.CURRENT_SCALE_RANGE
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.INITIAL_SCALE_RANGE
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.SCALE_RATIO
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID

object FigureModelHelper {
    @Suppress("UNCHECKED_CAST")
    fun updateSpecOverrideList(
        specOverrideList: List<Map<String, Any>>,
        newSpecOverride: Map<String, Any>?
    ): List<Map<String, Any>> {
        return if (newSpecOverride == null) {
            // drop all
            emptyList<Map<String, Any>>()
        } else {
            val targetId = newSpecOverride[TARGET_ID]

            @Suppress("NAME_SHADOWING")
            val specOverrideList = ArrayList(specOverrideList)
            val index = specOverrideList.indexOfFirst { it[TARGET_ID] == targetId }

            if (index < 0) {
                val initialScaleRange = newSpecOverride[CURRENT_SCALE_RANGE] as List<Double>
                val scale = calculate(initialScaleRange, newSpecOverride)
                specOverrideList.add(newSpecOverride + scale)

            } else {
                val lims = reconcile(specOverrideList[index], newSpecOverride)
                val initialScaleRange = specOverrideList[index][INITIAL_SCALE_RANGE] as List<Double>
                val scale = calculate(initialScaleRange, newSpecOverride)

                specOverrideList.set(index, lims + scale)
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

    private fun calculate(
        initialScaleRange: List<Double>,
        newSpecs: Map<String, Any>
    ): Map<String, Any> {
        val xScaleRatio = calculateScaleRatio(COORD_XLIM_TRANSFORMED, newSpecs, initialScaleRange[0])
        val yScaleRatio = calculateScaleRatio(COORD_YLIM_TRANSFORMED, newSpecs, initialScaleRange[1])

        return mapOf(
            INITIAL_SCALE_RANGE to initialScaleRange,
            SCALE_RATIO to listOf(xScaleRatio, yScaleRatio)
        )
    }

    private fun calculateScaleRatio(
        option: String,
        newSpecs: Map<String, Any>,
        initialRange: Double
    ): Double {
        @Suppress("UNCHECKED_CAST")
        val newLims = (newSpecs[option] as? List<Double?>) ?: return 1.0

        if (newLims.size != 2) {
            return 1.0
        }

        val newMin = newLims[0] ?: return 1.0
        val newMax = newLims[1] ?: return 1.0

        val newRange = newMax - newMin

        return initialRange / newRange
    }
}