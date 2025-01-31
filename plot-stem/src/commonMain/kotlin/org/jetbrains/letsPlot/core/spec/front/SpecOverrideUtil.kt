/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.SubPlots
import org.jetbrains.letsPlot.core.spec.config.PlotConfig

object SpecOverrideUtil {
    fun applySpecOverride(
        plotSpec: Map<String, Any>,
        specOverrideList: List<Map<String, Any>>
    ): Map<String, Any> {
        if (specOverrideList.isEmpty()) return plotSpec

        return when (PlotConfig.figSpecKind(plotSpec)) {
            FigKind.PLOT_SPEC -> {
                applySpecOverrideToSinglePlot(plotSpec, specOverrideList)
            }

            FigKind.SUBPLOTS_SPEC -> {
                applySpecOverrideToCompositePlot(
                    plotSpec,
                    specOverrideList
                )
            }

            FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("Unsupported: GGBunch")
        }
    }

    private fun specOverrideToApply(specId: String, specOverrideList: List<Map<String, Any>>): Map<String, Any>? {
        // Find spec override specifically for the given plot spec
        val forSpecId = specOverrideList.firstOrNull {
            val targetId = it[FigureModelOptions.TARGET_ID]
            targetId == specId
        }
        // Also try to find spec override applicabel to all plot specs in the figure.
        // Such spec override doesn't have "target id".
        val forAll = specOverrideList.firstOrNull {
            !it.containsKey(FigureModelOptions.TARGET_ID)
        }

        return forAll?.plus(forSpecId ?: emptyMap()) ?: forSpecId
    }

    private fun applySpecOverrideToSinglePlot(
        plotSpec: Map<String, Any>,
        specOverrideList: List<Map<String, Any>>
    ): Map<String, Any> {
        val specId = (plotSpec[Plot.SPEC_ID] as? String) ?: throw IllegalStateException(
            "${Plot.SPEC_ID} missing from plot specifications. " +
                    "Possible cause: specifications were not processed by the backend preprocessor."
        )
        val specOverrideToApply = specOverrideToApply(specId, specOverrideList)
        return specOverrideToApply?.let {
            plotSpec + mapOf(Plot.SPEC_OVERRIDE to it)
        } ?: plotSpec
    }

    private fun applySpecOverrideToCompositePlot(
        plotSpec: Map<String, Any>,
        specOverrideList: List<Map<String, Any>>
    ): Map<String, Any> {
        val specList = (plotSpec[SubPlots.FIGURES] as? List<*>)
        if (specList == null) return plotSpec

        val plotSpecCopy = HashMap(plotSpec)
        plotSpecCopy[SubPlots.FIGURES] = specList.map {
            if (it is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                (applySpecOverride(
                    plotSpec = it as Map<String, Any>,
                    specOverrideList
                ))
            } else {
                it
            }
        }
        return plotSpecCopy
    }
}