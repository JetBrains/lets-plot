/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import org.jetbrains.letsPlot.commons.logging.PortableLogging
import jetbrains.datalore.plot.config.FailureHandler
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.SubPlots.Figure.BLANK
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.server.config.transform.PlotConfigServerSideTransforms


object BackendSpecTransformUtil {
    private val LOG = PortableLogging.logger(BackendSpecTransformUtil::class)


    fun processTransform(plotSpecRaw: MutableMap<String, Any>): MutableMap<String, Any> {
        return try {
            when (PlotConfig.figSpecKind(plotSpecRaw)) {
                FigKind.PLOT_SPEC -> processTransformIntern(plotSpecRaw)
                FigKind.SUBPLOTS_SPEC -> processTransformInSubPlots(plotSpecRaw)
                FigKind.GG_BUNCH_SPEC -> processTransformInBunch(plotSpecRaw)
            }
        } catch (e: RuntimeException) {
            val failureInfo = FailureHandler.failureInfo(e)
            if (failureInfo.isInternalError) {
                LOG.error(e) { failureInfo.message }
            }
            HashMap(PlotConfig.failure(failureInfo.message))
        }
    }

    private fun processTransformInSubPlots(compositeFigureSpecRaw: MutableMap<String, Any>): MutableMap<String, Any> {
        if (!compositeFigureSpecRaw.containsKey(Option.SubPlots.FIGURES)) {
            compositeFigureSpecRaw[Option.SubPlots.FIGURES] = emptyList<Any>()
            return compositeFigureSpecRaw
        }

        val elementListRaw = compositeFigureSpecRaw[Option.SubPlots.FIGURES] as List<*>
        val elementListProcessed = processTransformFigureList(elementListRaw)
        val compositeFigureSpec = HashMap<String, Any>(compositeFigureSpecRaw)
        compositeFigureSpec[Option.SubPlots.FIGURES] = elementListProcessed
        return compositeFigureSpec
    }

    private fun processTransformFigureList(figureListRaw: List<*>): List<Any> {
        return figureListRaw.map { figRaw ->
            if (figRaw == null || figRaw == BLANK) {
                BLANK
            } else {
                if (figRaw !is Map<*, *>) {
                    throw IllegalArgumentException("Subplots: a figure spec (a Map) expected but was: ${figRaw::class.simpleName}")
                }

                @Suppress("UNCHECKED_CAST")
                val figCopy = HashMap<String, Any>(figRaw as Map<String, Any>)
                when (PlotConfig.figSpecKind(figCopy)) {
                    FigKind.PLOT_SPEC -> processTransformIntern(figCopy)
                    FigKind.SUBPLOTS_SPEC -> processTransformInSubPlots(figCopy)
                    FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("GGBunch is not expected among subplots.")
                }
            }
        }
    }

    private fun processTransformInBunch(bunchSpecRaw: MutableMap<String, Any>): MutableMap<String, Any> {
        if (!bunchSpecRaw.containsKey(Option.GGBunch.ITEMS)) {
            bunchSpecRaw[Option.GGBunch.ITEMS] = emptyList<Any>()
            return bunchSpecRaw
        }

        // List of items
        val itemsRaw: Any = bunchSpecRaw.get(Option.GGBunch.ITEMS)!!
        if (itemsRaw !is List<*>) {
            throw IllegalArgumentException("GGBunch: list of features expected but was: ${itemsRaw::class.simpleName}")
        }

        val items = ArrayList<MutableMap<String, Any>>()
        for (rawItem in itemsRaw) {
            if (rawItem !is Map<*, *>) {
                throw IllegalArgumentException("GGBunch item: Map of attributes expected but was: ${rawItem!!::class.simpleName}")
            }

            @Suppress("UNCHECKED_CAST")
            val item = HashMap<String, Any>(rawItem as Map<String, Any>)
            // Item feature spec (Map)
            if (!item.containsKey(Option.GGBunch.Item.FEATURE_SPEC)) {
                throw IllegalArgumentException("GGBunch item: absent required attribute: ${Option.GGBunch.Item.FEATURE_SPEC}")
            }

            val featureSpecRaw = item[Option.GGBunch.Item.FEATURE_SPEC]!!
            if (featureSpecRaw !is Map<*, *>) {
                throw IllegalArgumentException("GGBunch item '${Option.GGBunch.Item.FEATURE_SPEC}' : Map of attributes expected but was: ${featureSpecRaw::class.simpleName}")
            }

            // Plot spec
            @Suppress("UNCHECKED_CAST")
            val featureSpec = HashMap<String, Any>(featureSpecRaw as Map<String, Any>)
            val kind = PlotConfig.figSpecKind(featureSpec)
            if (kind != FigKind.PLOT_SPEC) {
                throw IllegalArgumentException("${FigKind.PLOT_SPEC} expected but was: $kind")
            }

            val plotSpec = processTransformIntern(featureSpec)
            item[Option.GGBunch.Item.FEATURE_SPEC] = plotSpec
            items.add(item)
        }

        bunchSpecRaw[Option.GGBunch.ITEMS] = items
        return bunchSpecRaw
    }

    private fun processTransformIntern(plotSpecRaw: MutableMap<String, Any>): MutableMap<String, Any> {
        val (plotSpec, _) = processTransformIntern2(plotSpecRaw)
        return plotSpec
    }

    /**
     * For tests only!
     */
    internal fun getTransformedSpecsAndPlotConfig(plotSpecRaw: MutableMap<String, Any>): Pair<MutableMap<String, Any>, PlotConfigServerSide> {
        return processTransformIntern2(plotSpecRaw)
    }

    private fun processTransformIntern2(plotSpecRaw: MutableMap<String, Any>): Pair<MutableMap<String, Any>, PlotConfigServerSide> {
        // testing of error handling
//            throwTestingException(plotSpecRaw)

        var plotSpec = PlotConfigServerSideTransforms.migrationTransform().apply(plotSpecRaw)
        plotSpec = PlotConfigServerSideTransforms.bistroTransform().apply(plotSpec)
        plotSpec = PlotConfigServerSideTransforms.entryTransform().apply(plotSpec)
        val plotConfig = PlotConfigServerSide(plotSpec)
        plotConfig.updatePlotSpec()
        return Pair(plotSpec, plotConfig)
    }


    @Suppress("unused")
    private fun throwTestingException(plotSpec: Map<String, Any>) {
        if (plotSpec.containsKey(Option.Plot.TITLE)) {
            @Suppress("UNCHECKED_CAST")
            val title = (plotSpec[Option.Plot.TITLE] as Map<String, Any>)[Option.Plot.TITLE_TEXT]!!
            if ("Throw testing exception" == title) {
//                    throw RuntimeException()
//                    throw RuntimeException("My sudden crush")
                throw IllegalArgumentException("User configuration error")
//                    throw IllegalStateException("User configuration error")
//                    throw IllegalStateException()   // Huh?
            }
        }
    }
}