/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_XLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_YLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.SCALE_RATIO
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.SpecOverrideState
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.DeckScaleShareGroups
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.GridScaleShareGroups
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.ScaleShareGroups
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.SubPlots
import org.jetbrains.letsPlot.core.spec.config.GridScaleShareConfig
import org.jetbrains.letsPlot.core.spec.config.DeckScaleShareConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig

object SpecOverrideUtil {

    fun applySpecOverride(
        plotSpec: Map<String, Any>,
        state: SpecOverrideState
    ): Map<String, Any> {
        val specOverrideList = state.specOverrides
        if (specOverrideList.isEmpty()) return plotSpec

        return when (PlotConfig.figSpecKind(plotSpec)) {
            FigKind.PLOT_SPEC -> {
                applySpecOverrideToSinglePlot(plotSpec, specOverrideList)
            }

            FigKind.SUBPLOTS_SPEC -> {
                applySpecOverrideToCompositePlot(plotSpec, state)
            }

            FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("Unsupported: GGBunch")
        }
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

    private fun specOverrideToApply(specId: String, specOverrideList: List<Map<String, Any>>): Map<String, Any>? {
        // Find spec override specifically for the given plot spec
        val forSpecId = specOverrideList.firstOrNull {
            val targetId = it[FigureModelOptions.TARGET_ID]
            targetId == specId
        }
        // Also try to find spec override applicabel to all plot specs in the figure.
        // Such a spec override doesn't have "target id".
        val forAll = specOverrideList.firstOrNull {
            !it.containsKey(FigureModelOptions.TARGET_ID)
        }

        return forAll?.plus(forSpecId ?: emptyMap()) ?: forSpecId
    }

    private fun applySpecOverrideToCompositePlot(
        plotSpec: Map<String, Any>,
        state: SpecOverrideState
    ): Map<String, Any> {
        val specList = (plotSpec[SubPlots.FIGURES] as? List<*>)
        if (specList == null) return plotSpec

        // Expand shared-axis overrides if the layout has scale sharing and there is an active target.
        if (state.activeTargetId != null) {
            val scaleShareGroups = scaleShareGroupsFromSpec(plotSpec)
            if (scaleShareGroups != null && scaleShareGroups.hasSharing) {
                val sourceIndex = specList.indexOfFirst { fig ->
                    (fig as? Map<*, *>)?.get(Plot.SPEC_ID) == state.activeTargetId
                }
                if (sourceIndex >= 0) {
                    state.expand { specOverrides, _ ->
                        expandOverrides(specOverrides, specList, sourceIndex, scaleShareGroups)
                    }
                }
            }
        }

        val nextState = if (state.expandedOverrides.isNotEmpty()) {
            SpecOverrideState(
                specOverrides = state.expandedOverrides,
                activeTargetId = null
            )
        } else {
            // Continue using the original state if no expansion occurred (to preserve active target id for next time).
            state
        }

        val plotSpecCopy = HashMap(plotSpec)
        plotSpecCopy[SubPlots.FIGURES] = specList.map {
            if (it is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                (applySpecOverride(
                    plotSpec = it as Map<String, Any>,
                    nextState
                ))
            } else {
                it
            }
        }
        return plotSpecCopy
    }

    private fun expandOverrides(
        specOverrides: List<Map<String, Any>>,
        figureSpecList: List<*>,
        sourceFigureIndex: Int,
        scaleShareGroups: ScaleShareGroups
    ): List<Map<String, Any>> {
        // Collect SPEC_IDs from each figure, null for blank/composite entries.
        val specIds: List<String?> = figureSpecList.map { fig ->
            @Suppress("UNCHECKED_CAST")
            (fig as? Map<String, Any>)?.get(Plot.SPEC_ID) as? String
        }

        val activeTargetId = checkNotNull(specIds[sourceFigureIndex]) {
            "No SPEC_ID at source figure index $sourceFigureIndex"
        }

        val sourceOverride = checkNotNull(specOverrides.firstOrNull { it[TARGET_ID] == activeTargetId }) {
            "No spec override for active target '$activeTargetId'"
        }

        // Compute shared groups.
        val sharedXGroup = scaleShareGroups.sharedXGroupOf(sourceFigureIndex, specIds.size)
        val sharedYGroup = scaleShareGroups.sharedYGroupOf(sourceFigureIndex, specIds.size)

        // Build expanded list: start with a copy of existing overrides.
        val result = specOverrides.toMutableList()

        // Propagate to siblings.
        val siblingIndices = (sharedXGroup + sharedYGroup).distinct().filter { it != sourceFigureIndex }
        for (siblingIndex in siblingIndices) {
            val siblingSpecId = specIds[siblingIndex] ?: continue

            // Find an existing override for this sibling, if any.
            val existingIdx = result.indexOfFirst { it[TARGET_ID] == siblingSpecId }
            val existingOverride = if (existingIdx >= 0) result[existingIdx] else null

            val siblingOverride = buildSiblingOverride(
                siblingSpecId = siblingSpecId,
                sourceOverride = sourceOverride,
                existingOverride = existingOverride,
                shareX = siblingIndex in sharedXGroup,
                shareY = siblingIndex in sharedYGroup
            )

            if (existingIdx >= 0) {
                result[existingIdx] = siblingOverride
            } else {
                result.add(siblingOverride)
            }
        }

        return result
    }

    private fun buildSiblingOverride(
        siblingSpecId: String,
        sourceOverride: Map<String, Any>,
        existingOverride: Map<String, Any>?,
        shareX: Boolean,
        shareY: Boolean
    ): Map<String, Any> {
        val override = LinkedHashMap<String, Any>()
        override[TARGET_ID] = siblingSpecId

        // Start with existing sibling values (preserve a non-shared-axis state).
        existingOverride?.let { override.putAll(it) }

        @Suppress("UNCHECKED_CAST")
        val sourceScaleRatio = sourceOverride[SCALE_RATIO] as? List<Number?>

        @Suppress("UNCHECKED_CAST")
        val existingScaleRatio = existingOverride?.get(SCALE_RATIO) as? List<Number?>

        // For shared axes: sync with source (copy or remove).
        if (shareX) {
            val xlim = sourceOverride[COORD_XLIM_TRANSFORMED]
            if (xlim != null) override[COORD_XLIM_TRANSFORMED] = xlim
            else override.remove(COORD_XLIM_TRANSFORMED)
        }
        if (shareY) {
            val ylim = sourceOverride[COORD_YLIM_TRANSFORMED]
            if (ylim != null) override[COORD_YLIM_TRANSFORMED] = ylim
            else override.remove(COORD_YLIM_TRANSFORMED)
        }

        // Build SCALE_RATIO: take еру shared-axis component from source, keep non-shared from existing.
        if (sourceScaleRatio != null) {
            val xRatio = if (shareX) sourceScaleRatio.getOrNull(0) else existingScaleRatio?.getOrNull(0)
            val yRatio = if (shareY) sourceScaleRatio.getOrNull(1) else existingScaleRatio?.getOrNull(1)
            override[SCALE_RATIO] = listOf(xRatio ?: 1.0, yRatio ?: 1.0)
        } else {
            // Source has no scale ratio (rollback) — clear shared components.
            val xRatio = if (shareX) null else existingScaleRatio?.getOrNull(0)
            val yRatio = if (shareY) null else existingScaleRatio?.getOrNull(1)
            if (xRatio != null || yRatio != null) {
                override[SCALE_RATIO] = listOf(xRatio ?: 1.0, yRatio ?: 1.0)
            } else {
                override.remove(SCALE_RATIO)
            }
        }

        return override
    }

    private fun scaleShareGroupsFromSpec(compositeSpec: Map<String, Any>): ScaleShareGroups? {
        val gridConfig = GridScaleShareConfig.fromCompositeFigureSpec(compositeSpec)
        if (gridConfig != null) {
            return GridScaleShareGroups(gridConfig.shareX, gridConfig.shareY, gridConfig.ncols)
        }

        val deckConfig = DeckScaleShareConfig.fromCompositeFigureSpec(compositeSpec)
        if (deckConfig != null) {
            return DeckScaleShareGroups(deckConfig.shareX, deckConfig.shareY)
        }

        return null
    }
}