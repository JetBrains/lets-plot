/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transform

import org.jetbrains.letsPlot.core.spec.Option.GGBunch
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.transform.SpecSelector.Companion.from

object PlotSpecTransformUtil {
    val GGBUNCH_KEY_PARTS = arrayOf(GGBunch.ITEMS, GGBunch.Item.FEATURE_SPEC)
    private val PLOT_WITH_LAYERS_TARGETS = listOf(
        TargetSpec.PLOT,
        TargetSpec.LAYER,
        TargetSpec.GEOM,
        TargetSpec.STAT
    )

    fun getDataSpecFinders(isGGBunch: Boolean): List<SpecFinder> {
        return getPlotAndLayersSpecFinders(
            isGGBunch,
            DATA
        )
    }

    fun getPlotAndLayersSpecFinders(isGGBunch: Boolean, vararg minorKeys: String): List<SpecFinder> {
        val keyCollections =
            getPlotAndLayersSpecSelectorKeys(
                isGGBunch,
                *minorKeys
            )
        return toFinders(keyCollections)
    }

    private fun toFinders(keyCollections: List<List<String>>): List<SpecFinder> {
        val finders = ArrayList<SpecFinder>()
        for (keys in keyCollections) {
            finders.add(SpecFinder(keys))
        }
        return finders
    }

    fun getPlotAndLayersSpecSelectors(isGGBunch: Boolean, vararg minorKeys: String): List<SpecSelector> {
        val keyCollections =
            getPlotAndLayersSpecSelectorKeys(
                isGGBunch,
                *minorKeys
            )
        return toSelectors(keyCollections)
    }

    private fun toSelectors(keyCollections: List<List<String>>): List<SpecSelector> {
        return keyCollections.map { parts: List<String> -> from(parts) }
    }

    private fun getPlotAndLayersSpecSelectorKeys(isGGBunch: Boolean, vararg minorKeys: String): List<List<String>> {
        val keyCollections = ArrayList<List<String>>()
        for (target in PLOT_WITH_LAYERS_TARGETS) {
            val keys = selectorKeys(target, isGGBunch)
            val keyCollection = listOf(*concat(
                keys,
                minorKeys
            )
            )
            keyCollections.add(keyCollection)
        }
        return keyCollections
    }

    private fun concat(a: Array<String>, b: Array<out String>): Array<String> {
        return (a + b)
    }

    private fun selectorKeys(target: TargetSpec, isGGBunch: Boolean): Array<String> {
        var keys: Array<String>
        when (target) {
            TargetSpec.PLOT -> keys = arrayOf()
            TargetSpec.LAYER -> keys = arrayOf(Plot.LAYERS)
            TargetSpec.GEOM -> keys = arrayOf(Plot.LAYERS, Layer.GEOM)
            TargetSpec.STAT -> keys = arrayOf(Plot.LAYERS, Layer.STAT)
        }

        if (isGGBunch) {
            keys = concat(
                GGBUNCH_KEY_PARTS,
                keys
            )
        }

        return keys
    }

    enum class TargetSpec {
        PLOT,
        LAYER,
        GEOM,
        STAT
    }
}
