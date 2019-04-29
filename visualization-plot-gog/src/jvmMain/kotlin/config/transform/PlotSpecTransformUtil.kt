package jetbrains.datalore.visualization.plot.gog.config.transform

import jetbrains.datalore.visualization.plot.gog.config.Option.GGBunch
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.DATA
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecSelector.Companion.from
import java.util.Arrays
import java.util.Arrays.asList
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.ArrayList
import kotlin.collections.List

object PlotSpecTransformUtil {
    val GGBUNCH_KEY_PARTS = arrayOf(GGBunch.ITEMS, GGBunch.Item.FEATURE_SPEC)
    private val PLOT_WITH_LAYERS_TARGETS = asList(
            TargetSpec.PLOT,
            TargetSpec.LAYER,
            TargetSpec.GEOM,
            TargetSpec.STAT
    )

    fun getDataSpecFinders(isGGBunch: Boolean): List<SpecFinder> {
        return getPlotAndLayersSpecFinders(isGGBunch, DATA)
    }

    fun getPlotAndLayersSpecFinders(isGGBunch: Boolean, vararg minorKeys: String): List<SpecFinder> {
        val keyCollections = getPlotAndLayersSpecSelectorKeys(isGGBunch, *minorKeys)
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
        val keyCollections = getPlotAndLayersSpecSelectorKeys(isGGBunch, *minorKeys)
        return toSelectors(keyCollections)
    }

    private fun toSelectors(keyCollections: List<List<String>>): List<SpecSelector> {
        return keyCollections.stream().map { parts: List<String> -> from(parts) }.collect(Collectors.toList())
    }

    private fun getPlotAndLayersSpecSelectorKeys(isGGBunch: Boolean, vararg minorKeys: String): List<List<String>> {
        val keyCollections = ArrayList<List<String>>()
        for (target in PLOT_WITH_LAYERS_TARGETS) {
            val keys = selectorKeys(target, isGGBunch)
            val keyCollection = asList(*concat(keys, minorKeys))
            keyCollections.add(keyCollection)
        }
        return keyCollections
    }

    private fun concat(a: Array<String>, b: Array<out String>): Array<String> {
        return Stream.concat(Arrays.stream(a), Arrays.stream(b)).toArray { size -> arrayOfNulls<String>(size) }
    }

    private fun selectorKeys(target: TargetSpec, isGGBunch: Boolean): Array<String> {
        var keys: Array<String>
        when (target) {
            PlotSpecTransformUtil.TargetSpec.PLOT -> keys = arrayOf()
            PlotSpecTransformUtil.TargetSpec.LAYER -> keys = arrayOf(Plot.LAYERS)
            PlotSpecTransformUtil.TargetSpec.GEOM -> keys = arrayOf(Plot.LAYERS, Layer.GEOM)
            PlotSpecTransformUtil.TargetSpec.STAT -> keys = arrayOf(Plot.LAYERS, Layer.STAT)
        }

        if (isGGBunch) {
            keys = concat(GGBUNCH_KEY_PARTS, keys)
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
