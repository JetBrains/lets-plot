/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.CoordConfig
import org.jetbrains.letsPlot.core.spec.config.GuideConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfigTransforms
import org.jetbrains.letsPlot.core.spec.config.ScaleConfig
import org.jetbrains.letsPlot.core.spec.front.tiles.PlotGeomTilesBase

object PlotConfigFrontendUtil {
    internal fun createGuideOptionsMap(scaleConfigs: List<ScaleConfig<*>>): Map<Aes<*>, GuideOptions> {
        val guideOptionsByAes = HashMap<Aes<*>, GuideOptions>()
        for (scaleConfig in scaleConfigs) {
            if (scaleConfig.hasGuideOptions()) {
                val guideOptions = scaleConfig.getGuideOptions().createGuideOptions()
                guideOptionsByAes[scaleConfig.aes] = guideOptions
            }
        }
        return guideOptionsByAes
    }

    internal fun createGuideOptionsMap(guideOptionsList: Map<String, Any>): Map<Aes<*>, GuideOptions> {
        val guideOptionsByAes = HashMap<Aes<*>, GuideOptions>()
        for ((key, value) in guideOptionsList) {
            val aes = Option.Mapping.toAes(key)
            guideOptionsByAes[aes] = GuideConfig.create(value).createGuideOptions()
        }
        return guideOptionsByAes
    }

    internal fun createMappersAndScalesBeforeFacets(config: PlotConfigFrontend): Pair<Map<Aes<*>, ScaleMapper<*>>, Map<Aes<*>, Scale>> {
        val layerConfigs = config.layerConfigs

        val transformByAes = PlotConfigTransforms.createTransforms(
            layerConfigs,
            config.scaleProviderByAes,
            config.mapperProviderByAes,
            excludeStatVariables = false   // Frontend - take in account "stat" variables
        )

        val mappersByAes = PlotConfigScaleMappers.createMappers(
            layerConfigs,
            transformByAes,
            config.mapperProviderByAes,
        )

        val scaleByAes = PlotConfigScales.createScales(
            layerConfigs,
            transformByAes,
            mappersByAes,
            config.scaleProviderByAes
        )
        return Pair(mappersByAes, scaleByAes)
    }

    fun createPlotGeomTiles(
        config: PlotConfigFrontend,
        sharedContinuousDomainX: DoubleSpan? = null,
        sharedContinuousDomainY: DoubleSpan? = null,
    ): PlotGeomTiles {
        // Scale "before facets".
        val (mappersByAesNP, scaleByAesBeforeFacets) =
            createMappersAndScalesBeforeFacets(config).let { (mappers, scales) ->
                // Adjust domains of continuous scales when axis are shared between plots in a composite figure.
                val scalesAdjusted: Map<Aes<*>, Scale> = scales.mapValues { (aes, scale) ->
                    if (aes == Aes.X && sharedContinuousDomainX != null) {
                        scale.with().continuousTransform(
                            Transforms.continuousWithLimits(
                                scale.transform as ContinuousTransform,
                                sharedContinuousDomainX.toPair()
                            )
                        ).build()
                    } else if (aes == Aes.Y && sharedContinuousDomainY != null) {
                        scale.with().continuousTransform(
                            Transforms.continuousWithLimits(
                                scale.transform as ContinuousTransform,
                                sharedContinuousDomainY.toPair()
                            )
                        ).build()
                    } else {
                        scale
                    }
                }

                // Take only non-positional mappers
                Pair(
                    mappers.filterKeys { !Aes.isPositional(it) },
                    scalesAdjusted
                )
            }

        // Coord provider
        val preferredCoordProvider: CoordProvider? = config.layerConfigs.firstNotNullOfOrNull {
            it.geomProto.preferredCoordinateSystem(it)
        }

        val defaultCoordProvider = preferredCoordProvider ?: CoordProviders.cartesian()
        val coordProvider = CoordConfig.createCoordProvider(
            config.get(Option.Plot.COORD),
            scaleByAesBeforeFacets.getValue(Aes.X).transform,
            scaleByAesBeforeFacets.getValue(Aes.Y).transform,
            defaultCoordProvider
        )

        return PlotGeomTilesBase.create(
            config.layerConfigs,
            config.facets,
            coordProvider,
            scaleByAesBeforeFacets,
            mappersByAesNP,
            config.theme,
            config.theme.fontFamilyRegistry,
        )
    }

    fun createPlotAssembler(
        config: PlotConfigFrontend,
        sharedContinuousDomainX: DoubleSpan? = null,
        sharedContinuousDomainY: DoubleSpan? = null,
    ): PlotAssembler {

        val plotGeomTiles = createPlotGeomTiles(
            config,
            sharedContinuousDomainX,
            sharedContinuousDomainY
        )
        return PlotAssembler(
            plotGeomTiles,
            config.facets,
            config.xAxisPosition,
            config.yAxisPosition,
            config.theme,
            title = config.title,
            subtitle = config.subtitle,
            caption = config.caption,
            guideOptionsMap = config.guideOptionsMap
        )
    }
}
