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

    private fun findBySpecId(specId: String?, specOverrideList: List<Map<String, Any>>): Map<String, Any>? {
        if (specId == null) return null
        val forSpecId = specOverrideList.firstOrNull {
            val targetId = it[FigureModelOptions.TARGET_ID]
            targetId == specId
        }
        val forAll = specOverrideList.firstOrNull {
            !it.containsKey(FigureModelOptions.TARGET_ID)
        }

        return forAll?.plus(forSpecId ?: emptyMap()) ?: forSpecId
    }

    private fun applySpecOverrideToSinglePlot(
        plotSpec: Map<String, Any>,
        specOverrideList: List<Map<String, Any>>
    ): Map<String, Any> {
        val specId = plotSpec[Plot.SPEC_ID] as? String
        val specOverrideToApply = findBySpecId(specId, specOverrideList)
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