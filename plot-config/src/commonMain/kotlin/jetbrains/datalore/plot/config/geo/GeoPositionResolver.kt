/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.geo

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.plot.config.Option.GGBunch.ITEMS
import jetbrains.datalore.plot.config.Option.GGBunch.Item.FEATURE_SPEC
import jetbrains.datalore.plot.config.Option.GeomName.LIVE_MAP
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.transform.PlotSpecTransform
import jetbrains.datalore.plot.config.transform.SpecFinder
import jetbrains.datalore.plot.config.transform.SpecSelector.Companion.of

object GeoPositionsResolver {

    public fun fetchAndReplace(
    plotSpec: Map<String, Any>
    ) : Async<Map<String, Any>> {
        val specTransformBuilder = PlotSpecTransform.builderForRawSpec();

        // Process geodataframe
        specTransformBuilder.change(
            of(*layersPath(plotSpec).toTypedArray()),
            GeometryFromGeoDataFrameChange()
        );


        // run
        val resultPlotSpec: Map<String, Any>  = specTransformBuilder.build().apply(plotSpec.toMutableMap());

        return Asyncs.constant(resultPlotSpec)
    }

    private fun containsLivemap(plotSpec: Map<String, Any>): Boolean {
        var containsLivemap = false;
        for (it in SpecFinder(layersPath(plotSpec)).findSpecs(plotSpec as Map<*, *>)) {
            if (it.get(GEOM)!! == LIVE_MAP) {
                containsLivemap = true
                break
            }
        }
        return containsLivemap;
    }

    internal fun layersPath(plotSpec: Map<String, Any>): List<String> {
        if (PlotConfig.isPlotSpec(plotSpec)) {
            return listOf(LAYERS);
        }

        if (PlotConfig.isGGBunchSpec(plotSpec)) {
            return listOf(ITEMS, FEATURE_SPEC, LAYERS);
        }

        throw IllegalArgumentException("Plot spec kind is not set");
    }
}


